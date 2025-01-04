package bgu.spl.mics.application.services;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectEvent;
import bgu.spl.mics.application.objects.*;



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
    private long timeTick;
    private StatisticalFolder statisticalFolder;

    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super("LidarService_" + LiDarWorkerTracker.getID());
        liDar = LiDarWorkerTracker;
        timeTick = 0;
        this.statisticalFolder = StatisticalFolder.getInstance();

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
            System.out.println(getName() + "recived time tick " + timeTick);
            List<TrackedObject> listTracked = liDar.CheckIfTimed(timeTick);
            if (listTracked != null) {
                for (TrackedObject trackedObj : listTracked) {
                    DetectObjectEvent detEvent = trackedObj.getEvent();
                    if (!detEvent.isCompleted()) {
                        detEvent.complete();
                        complete(detEvent, true);
                    }
                }
                Future<Boolean> futureObj = sendEvent(new TrackedObjectEvent(listTracked));
                statisticalFolder.increasenumTrackedObjects();
                if (futureObj.get(100, TimeUnit.MILLISECONDS) == null) { // CHECK
                    System.out.println("Time has elapsed, no services has resolved the event - terminating");
                        terminate();
            }
            }
        });
        subscribeBroadcast(TerminateBroadcast.class, Terminate -> {
            liDar.setStatus(STATUS.DOWN);
            this.terminate();
        });
        subscribeBroadcast(CrashedBroadcast.class, Terminate -> {
            liDar.setStatus(STATUS.DOWN);
            this.terminate(); 
        });
        subscribeEvent(DetectObjectEvent.class, DetectedObjectsEvent -> {
            System.out.println(getName() + "recived detected object event");
            List<TrackedObject> listTracked = liDar.interval(timeTick, DetectedObjectsEvent);
            if (listTracked != null && !listTracked.isEmpty()){
                
                if (listTracked.get(0).getID() == "-1") {
                    liDar.setStatus(STATUS.ERROR);
                    sendBroadcast(new CrashedBroadcast(this.getName(), "Connection to LiDAR lost"));
                    terminate();
                }

                if (listTracked.get(0).getID() == "-2") {
                    liDar.setStatus(STATUS.DOWN);
                    sendBroadcast(new TerminateBroadcast(getName()));
                    terminate();
                }

                Future<Boolean> futureObj = sendEvent(new TrackedObjectEvent(listTracked));
                DetectedObjectsEvent.complete();
                complete(DetectedObjectsEvent, true);
                 if (futureObj.get(500, TimeUnit.MILLISECONDS) == null) { // CHECK
                        System.out.println(getName() + " Time has elapsed, no services has resolved the event - terminating");
                            terminate();
                }
                // CHECK IF NEED TO DO SOMETHING IF ITS NOT NULL
            }
        });
        liDar.setStatus(STATUS.UP);
        System.out.println(getName() + "is UP!");
}

public STATUS getStatus() {
    return liDar.getStatus();
}

}
