package bgu.spl.mics.application.services;

import java.util.concurrent.TimeUnit;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;

import java.util.List;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectEvent;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;

/**
 * PoseService is responsible for maintaining the robot's current pose (position
 * and orientation)
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
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the
     * current pose.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (Callback<TickBroadcast>) tickBroadcast -> {
            gpsimu.increaseCurrentTick();
            Pose lastPose = gpsimu.getCurrentPose();
            Future<Boolean> futureObj = sendEvent(new PoseEvent(lastPose));
            if (futureObj.get(100, TimeUnit.MILLISECONDS) == null) {
                System.out.println("Time has elapsed, no services has resolved the event - terminating");
                terminate();
            }
        });
        subscribeBroadcast(TerminateBroadcast.class, terminate -> {
            gpsimu.setStatus(STATUS.DOWN);
            terminate();
        });

        subscribeBroadcast(CrashedBroadcast.class, crashed -> {
            gpsimu.setStatus(STATUS.DOWN);
            terminate();
        });
        gpsimu.setStatus(STATUS.UP);
    }
}
