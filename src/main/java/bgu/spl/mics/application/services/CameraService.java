package bgu.spl.mics.application.services;

import java.util.concurrent.TimeUnit;
import bgu.spl.mics.Future;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */

    private Camera camera;
    private StatisticalFolder statisticalFolder;

    public CameraService(Camera camera) {
        super("CameraService_" + camera.getID());
        this.camera = camera;
        this.statisticalFolder = StatisticalFolder.getInstance();
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for
     * sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (Callback<TickBroadcast>) tickBroadcast -> {
            long currentTime = tickBroadcast.getTime();
            System.out.println(getName() + "recived tick" + currentTime); ///////////
            StampedDetectedObjects stampedObj = camera.interval(currentTime);
            if (stampedObj != null) {
                if (stampedObj.getTime() == -1) {
                    camera.setStatus(STATUS.ERROR);
                    sendBroadcast(new CrashedBroadcast(this.getName(), stampedObj.getDetectedObjects().get(0).getDescription()));
                    terminate();
                }

                if (stampedObj.getTime() == -2) {
                    camera.setStatus(STATUS.DOWN);
                    sendBroadcast(new TerminateBroadcast(getName()));
                    terminate();
                }

                    DetectObjectEvent ev = new DetectObjectEvent(stampedObj);
                    Future<Boolean> futureObj = sendEvent(ev);
                    statisticalFolder.increasenumDetectedObjects();
                    if (futureObj != null && futureObj.get(500, TimeUnit.MILLISECONDS) == null) {
                        System.out.println(getName() + " Time has elapsed, no services has resolved the event - terminating");
                            terminate();
                    }
                }
            
        });
        this.subscribeBroadcast(TerminateBroadcast.class, Terminate -> {
            camera.setStatus(STATUS.DOWN);
            this.terminate();
        });
        this.subscribeBroadcast(CrashedBroadcast.class, Terminate -> {
            camera.setStatus(STATUS.DOWN);
            this.terminate();
        });
        System.out.println(getName() + " is UP!"); /////////////
        camera.setStatus(STATUS.UP);
    }

    public STATUS getStatus() {
        return camera.getStatus();
    }
}
