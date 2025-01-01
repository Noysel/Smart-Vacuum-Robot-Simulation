package bgu.spl.mics.application.services;

import java.util.concurrent.TimeUnit;
import bgu.spl.mics.Future;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.DetectObjectEvent;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.TerminateBroadcast;
import bgu.spl.mics.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.objects.TrackedObject;

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
        super("CameraService");
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
            int currentTime = tickBroadcast.getTime();
            StampedDetectedObjects stampedObj = camera.interval(currentTime);
            if (stampedObj != null) {
                    DetectObjectEvent ev = new DetectObjectEvent(stampedObj);
                    Future<Boolean> futureObj = sendEvent(ev);
                    statisticalFolder.increasenumDetectedObjects();
                    if (futureObj.get(100, TimeUnit.MILLISECONDS) == null) {
                        System.out.println("Time has elapsed, no services has resolved the event - terminating");
                            terminate();
                    }
                }
            
        });
        this.subscribeBroadcast(TerminateBroadcast.class, Terminate -> {
            this.terminate();
        });
        this.subscribeBroadcast(CrashedBroadcast.class, Terminate -> {
            this.terminate(); // CHECK TO TERMINATE.
        });
    }
}
