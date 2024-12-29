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
    private int time;
    public CameraService(Camera camera) {
        super("CameraService");
        this.camera = camera;
        time = 0;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, DetectOjbectsEvents -> {
            time++;
            StampedDetectedObjects stampedObj = camera.interval(time);
            if (stampedObj != null) {
                for (DetectedObject obj : stampedObj.getDetectedObjects()) {
                    DetectObjectEvent ev = new DetectObjectEvent(obj);
                    Future<TrackedObject> futureObj = sendEvent(ev); // CHECKK
                    if (futureObj != null) {
                        TrackedObject trackedObj = futureObj.get();
                        complete(ev, trackedObj);
                    }
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
