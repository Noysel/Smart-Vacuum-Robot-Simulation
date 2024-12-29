package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.FusionSlam.FusionSlamHolder.SingletonHolder;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping
 * (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update
 * a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam
 * exists.
 */
public class FusionSlam {

    private List<LandMark> landMarks;
    private List<Pose> poses;

    private static class SingletonHolder {
        private static FusionSlam instance = new FusionSlam();
    }

    public static FusionSlam getInstance() {
        return SingletonHolder.instance;
    }

    public FusionSlam() {
        this.landMarks = new LinkedList<>();
        this.poses = new LinkedList<>();
    }

    public FusionSlam(List<LandMark> landMarks, List<Pose> poses) {
        this.landMarks = landMarks;
        this.poses = poses;
    }

}
