package bgu.spl.mics.application.services;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bgu.spl.mics.Callback;
import bgu.spl.mics.DetectObjectEvent;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.TerminateBroadcast;
import bgu.spl.mics.TickBroadcast;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.StampedCloudPoints;
import bgu.spl.mics.application.objects.TrackedObject;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.LandMark;



/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    private LiDarWorkerTracker liDar;
    private int timeTick;
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super("LidarService");
        liDar = LiDarWorkerTracker;
        timeTick = 0;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (Callback<TickBroadcast>) tickBroadcast -> {
            timeTick = tickBroadcast.getTime();
            List<TrackedObject> listTracked = liDar.CheckIfTimed(timeTick);
            if (listTracked != null){
                for (TrackedObject trackedObj : listTracked) {
                    DetectObjectEvent detEvent = trackedObj.getEvent();
                    if (!detEvent.isCompleted()) {
                        detEvent.complete();
                        complete(detEvent, true);
                    }
                }
                Future<LandMark> futureObj = sendEvent(new TrackedObjectEvent(listTracked));
                if (futureObj.get(100, TimeUnit.MILLISECONDS) == null) { // CHECK
                    System.out.println("Time has elapsed, no services has resolved the event - terminating");
                        terminate();
            }
            }
        });
        subscribeBroadcast(TerminateBroadcast.class, Terminate -> {
            this.terminate();
        });
        subscribeBroadcast(CrashedBroadcast.class, Terminate -> {
            this.terminate(); // CHECK..
        });
        subscribeEvent(DetectObjectEvent.class, DetectedObjectsEvent -> {
            List<TrackedObject> listTracked = liDar.interval(timeTick, DetectedObjectsEvent);
            if (listTracked != null){
                Future<LandMark> futureObj = sendEvent(new TrackedObjectEvent(listTracked));
                DetectedObjectsEvent.complete();
                complete(DetectedObjectsEvent, true);
                 if (futureObj.get(100, TimeUnit.MILLISECONDS) == null) { // CHECK
                        System.out.println("Time has elapsed, no services has resolved the event - terminating");
                            terminate();
                }
                // CHECK IF NEED TO DO SOMETHING IF ITS NOT NULL
            }
        });
}
}
