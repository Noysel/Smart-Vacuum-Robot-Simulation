package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.TerminateBroadcast;
import bgu.spl.mics.TickBroadcast;
import bgu.spl.mics.application.objects.FusionSlam;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    private FusionSlam fs;

    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlamService");
        this.fs = fusionSlam;
        
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, TrackedObjectsEvents -> {
            //??
        });
        subscribeBroadcast(TerminateBroadcast.class, Terminate -> {
            terminate();
        });

        subscribeBroadcast(CrashedBroadcast.class, Crashed -> {
            terminate();
        });

        subscribeEvent(PoseEvent.class, poseEvent -> {

        });

        subscribeEvent(TrackedObjectEvent.class, TrackedObjectsEvents -> {
            //??
        });

        
    }
}
