package bgu.spl.mics.application.services;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;

import java.util.List;

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
    private StatisticalFolder statisticalFolder;
    private CountDownLatch latch;


    public PoseService(GPSIMU gpsimu, CountDownLatch latch) {
        super("PoseService");
        this.gpsimu = gpsimu;
        this.statisticalFolder = StatisticalFolder.getInstance();
        this.latch = latch;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the
     * current pose.
     */
    @Override
    protected void initialize() {

        subscribeBroadcast(TickBroadcast.class, (Callback<TickBroadcast>) tickBroadcast -> {
            if (gpsimu.increaseCurrentTick()) {
                Pose lastPose = gpsimu.getCurrentPose();
                sendEvent(new PoseEvent(lastPose)); //Future<Boolean> futureObj = 
                statisticalFolder.addPose(lastPose);
                System.out.println("PoseService sent Pose: " + lastPose.getTime());
            }
            else {
                sendBroadcast(new TerminateBroadcast(getName()));
            }

        });
        subscribeBroadcast(TerminateBroadcast.class, terminate -> {
            if (terminate.getSender().equals("FusionSlamService")) {
                gpsimu.setStatus(STATUS.DOWN);
                terminate();
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, crashed -> {
            gpsimu.setStatus(STATUS.DOWN);
            terminate();
        });
        gpsimu.setStatus(STATUS.UP);
        System.out.println(getName() + "is UP!");
        latch.countDown();
    }
}
