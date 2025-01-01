package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.TerminateBroadcast;
import bgu.spl.mics.TickBroadcast;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.StatisticalFolder;

import java.util.List;
import bgu.spl.mics.application.objects.TrackedObject;

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
    private StatisticalFolder statisticalFolder;

    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlamService");
        this.fs = fusionSlam;
        this.statisticalFolder = StatisticalFolder.getInstance();
        
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, timeEvent -> {
            fs.setTime(timeEvent.getTime());
        });
        subscribeBroadcast(TerminateBroadcast.class, Terminate -> {
            terminate();
        });

        subscribeBroadcast(CrashedBroadcast.class, Crashed -> {
            terminate();
        });

        subscribeEvent(PoseEvent.class, poseEvent -> {
            fs.setCurrentPose(poseEvent.getPose());
        });

        subscribeEvent(TrackedObjectEvent.class, trackedObjectsEvent -> {
            List<TrackedObject> trackedObjList = trackedObjectsEvent.getTrackedObjectList();
            for (TrackedObject trackedObj : trackedObjList){
                if (fs.insertLandMark(trackedObj)){
                    statisticalFolder.increasenumLandMarks();
                }
            }
            complete(trackedObjectsEvent, true); //
        });

        
    }
}
