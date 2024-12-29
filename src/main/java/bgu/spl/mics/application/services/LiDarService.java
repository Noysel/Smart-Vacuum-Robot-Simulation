package bgu.spl.mics.application.services;

import bgu.spl.mics.DetectObjectEvent;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.TerminateBroadcast;
import bgu.spl.mics.TickBroadcast;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;

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
    private int time;
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super("LidarService");
        liDar = LiDarWorkerTracker;
        time = 0;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, Tick -> {
            time++;
        });
        subscribeBroadcast(TerminateBroadcast.class, Terminate -> {
            this.terminate();
        });
        subscribeBroadcast(CrashedBroadcast.class, Terminate -> {
            this.terminate(); // CHECK
        });
        subscribeEvent(DetectObjectEvent.class, TrackedObjectsEvents -> {
            if (time + frequency)
            this.sendEvent(new TrackedObjectEvent<>());
        });
}
}
