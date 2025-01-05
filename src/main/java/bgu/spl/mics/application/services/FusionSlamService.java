package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.KillTimeEvent;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectEvent;
import bgu.spl.mics.application.objects.*;

import java.util.List;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents
 * from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global
     *                   map.
     */
    private FusionSlam fs;
    private STATUS status;
    private int numOfServices;
    private StatisticalFolder statisticalFolder;


    public FusionSlamService(FusionSlam fusionSlam, int numOfServices) {
        super("FusionSlamService");
        this.fs = fusionSlam;
        this.status = STATUS.DOWN;
        this.numOfServices = numOfServices;
        statisticalFolder = StatisticalFolder.getInstance();
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and
     * TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, TerminateBroadcast -> {
            if (TerminateBroadcast.getSender().equals("TimeService")) {
                sendBroadcast(new TerminateBroadcast(getName()));
                statisticalFolder.setWorldMap(fs.getWorldMap());
                terminate();
            }

            else {
                numOfServices--;
                System.out.println(numOfServices + " numOfServicesLeft");
                if (numOfServices == 0) {
                    sendEvent(new KillTimeEvent());
                    sendBroadcast(new TerminateBroadcast(getName()));
                    statisticalFolder.setWorldMap(fs.getWorldMap());
                    statisticalFolder.createOutputFile("example_input_2\\output_file.json");
                    terminate();
                }
            }

        });

        subscribeBroadcast(CrashedBroadcast.class, Crashed -> {
            sendEvent(new KillTimeEvent());
            statisticalFolder.createOutputFile("example_input_2\\output_file.json");
            terminate();
        });

        subscribeEvent(PoseEvent.class, poseEvent -> {
            System.out.println("Fusion Slam recived PoseEvent, pose's time: " + poseEvent.getPose().getTime());
            complete(poseEvent, true);
            TrackedObjectEvent trackedObjectEvent = fs.setCurrentPose(poseEvent.getPose());
            if (trackedObjectEvent != null) {
                complete(trackedObjectEvent, true);
            }
        });

        subscribeEvent(TrackedObjectEvent.class, trackedObjectsEvent -> {
            for (TrackedObject trackObj : trackedObjectsEvent.getTrackedObjectList()) {
                System.out.println("Fusion Slam recived trackedObjEvent: " + trackObj.getID() + " from: " + trackedObjectsEvent.getSedner());
            }
            if (fs.insertLandMark(trackedObjectsEvent)) {
                complete(trackedObjectsEvent, true);
            }
        });
        status = STATUS.UP;
        System.out.println(getName() + "is UP!");
    }

    public STATUS getStatus() { // getStatus for the main to know that the service we
        return status;
    }
}
