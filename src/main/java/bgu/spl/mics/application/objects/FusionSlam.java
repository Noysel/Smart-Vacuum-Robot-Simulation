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
    private int time;
    private Pose currentPose;

    private static class SingletonHolder {
        private static FusionSlam instance = new FusionSlam();
    }

    public static FusionSlam getInstance() {
        return SingletonHolder.instance;
    }

    public FusionSlam() {
        this.landMarks = new LinkedList<>();
        this.poses = new LinkedList<>();
        this.time = 0;
        this.currentPose = null;
    }

    public FusionSlam(List<LandMark> landMarks, List<Pose> poses) {
        this.landMarks = landMarks;
        this.poses = poses;
        this.time = 0;
        this.currentPose = null;
    }

    public CloudPoint ConvertCoordinates(CloudPoint coordinates) {
        double radYaw = Math.toRadians(currentPose.getYaw());
        double cosYaw = Math.cos(radYaw);
        double sinYaw = Math.sin(cosYaw);
        double xG = (cosYaw * coordinates.getX()) - (sinYaw * coordinates.getY()) + currentPose.getX();
        double yG = (sinYaw * coordinates.getX()) + (cosYaw * coordinates.getY()) + currentPose.getY();
        return new CloudPoint(xG, yG);
    }

    public void insertLandMark(TrackedObject trackedObj) {

        List<CloudPoint> globalCoordinates = new LinkedList<>();
        for (CloudPoint point : trackedObj.getCoordinates()) {
            globalCoordinates.add(ConvertCoordinates(point));
        }
        if (!landMarks.contains(trackedObj)) {
            landMarks.add(new LandMark(trackedObj.getID(), trackedObj.getDescription(), globalCoordinates));
        }
        else {
            //complete
        }

    }

}
