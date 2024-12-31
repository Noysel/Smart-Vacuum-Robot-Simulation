package bgu.spl.mics.application.services;

import java.util.concurrent.TimeUnit;

import bgu.spl.mics.Callback;
import bgu.spl.mics.DetectObjectEvent;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.TickBroadcast;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import java.util.List;
import bgu.spl.mics.application.objects.Pose;


/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */

    private GPSIMU gpsimu;
    public PoseService(GPSIMU gpsimu) {
        super("PoseService");
        this.gpsimu = gpsimu;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (Callback<TickBroadcast>) tickBroadcast -> {
            int currentTick = tickBroadcast.getTime();
            List<Pose> poseList = gpsimu.getPoseList();
            for (Pose pose : poseList) {
                if (currentTick == pose.getTime()) {
                    Future<Boolean> futureObj = sendEvent(new PoseEvent(pose));
                    if (futureObj.get(100, TimeUnit.MILLISECONDS) == null) {
                        System.out.println("Time has elapsed, no services has resolved the event - terminating");
                        terminate();
                    }
                    break;
                }
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, crashed -> {
            terminate();
        });
    }
}
