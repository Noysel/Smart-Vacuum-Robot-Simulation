package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

import bgu.spl.mics.MessageBusImpl;
//import bgu.spl.mics.application.objects.SingletonHolder;

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

    public void setTime(int tickTime) {
        time = tickTime;
    }

    public void setCurrentPose(Pose pose) {
        currentPose = pose;
        poses.add(pose);
        notifyAll();
    }

    public CloudPoint ConvertCoordinates(List<Double> coordinates, Pose Detectedpose) {
        double radYaw = Math.toRadians(Detectedpose.getYaw());
        double cosYaw = Math.cos(radYaw);
        double sinYaw = Math.sin(cosYaw);
        double xG = (cosYaw * coordinates.get(0)) - (sinYaw * coordinates.get(1)) + Detectedpose.getX();
        double yG = (sinYaw * coordinates.get(0)) + (cosYaw * coordinates.get(1)) + Detectedpose.getY();
        return new CloudPoint(xG, yG);
    }

    public Boolean insertLandMark(TrackedObject trackedObj) { //return true if inserting new LandMark
        List<CloudPoint> globalCoordinates = new LinkedList<>();
        while (currentPose.getTime() < trackedObj.getTime()) {
            try {
				this.wait();
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
                ///
			}
        }
        for (Pose pose : poses) {
            if (pose.getTime() == trackedObj.getTime()) {
                for (List<Double> point : trackedObj.getCoordinates()) {
                    globalCoordinates.add(ConvertCoordinates(point, pose));
                } 
                for (LandMark landMark : landMarks) {
                    if (trackedObj.getID() == landMark.getID()) {
                        Iterator<CloudPoint> LMIterator = landMark.getCoordinates().iterator();
                        Iterator<CloudPoint> GIterator = globalCoordinates.iterator();
                        while (GIterator.hasNext()) {
                            if (LMIterator.hasNext()) {
                            CloudPoint trackedP = GIterator.next();
                            CloudPoint landMarkP = LMIterator.next();
                            double landMarkX = landMarkP.getX();
                            double landMarkY = landMarkP.getY();
                            landMarkP.setX((landMarkX+trackedP.getX())/2);
                            landMarkP.setY((landMarkY+trackedP.getY())/2);
                            }
                            else {
                                landMark.addCoordinates(GIterator.next());
                            }
                        }
                        return false;
                    }
                }
                    landMarks.add(new LandMark(trackedObj.getID(), trackedObj.getDescription(), globalCoordinates));
                    return true;
                }
            }
            return true;
        }
    }
