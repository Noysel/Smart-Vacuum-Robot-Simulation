package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

import bgu.spl.mics.MessageBusImpl;
import java.util.concurrent.CopyOnWriteArrayList;

import bgu.spl.mics.application.messages.TrackedObjectEvent;

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
    private Pose currentPose;
    private List<TrackedObjectEvent> notYetTO;
    private StatisticalFolder statisticalFolder;

    private static class SingletonHolder {
        private static FusionSlam instance = new FusionSlam();
    }

    public static FusionSlam getInstance() {
        return SingletonHolder.instance;
    }

    public FusionSlam() {
        this.landMarks = new LinkedList<>();
        this.poses = new LinkedList<>();
        this.currentPose = null;
        this.notYetTO = new LinkedList<>();
        this.statisticalFolder = StatisticalFolder.getInstance();
    }

    public FusionSlam(List<LandMark> landMarks, List<Pose> poses) {
        this.landMarks = landMarks;
        this.poses = poses;
        this.currentPose = null;
        this.notYetTO = new LinkedList<>();
        this.statisticalFolder = StatisticalFolder.getInstance();
    }

    public TrackedObjectEvent setCurrentPose(Pose pose) {
        currentPose = pose;
        poses.add(pose);
        for (TrackedObjectEvent trackedObjEvent : notYetTO) {
            List<TrackedObject> trackedObjList = trackedObjEvent.getTrackedObjectList();
            if (!trackedObjList.isEmpty() && currentPose.getTime() == trackedObjList.get(0).getTime()) {
                notYetTO.remove(trackedObjEvent);
                insertLandMark(trackedObjEvent);
                return trackedObjEvent;
            }
        }
        return null;
    }

    public CloudPoint ConvertCoordinates(List<Double> coordinates, Pose Detectedpose) {
        double radYaw = Math.toRadians(Detectedpose.getYaw());
        double cosYaw = Math.cos(radYaw);
        double sinYaw = Math.sin(radYaw);
        double xG = (cosYaw * coordinates.get(0)) - (sinYaw * coordinates.get(1)) + Detectedpose.getX();
        double yG = (sinYaw * coordinates.get(0)) + (cosYaw * coordinates.get(1)) + Detectedpose.getY();
        return new CloudPoint(xG, yG);
    }
    public boolean insertLandMark(TrackedObjectEvent trackedObjEvent) {
        List<TrackedObject> trackedObjList = trackedObjEvent.getTrackedObjectList();
        if (!trackedObjList.isEmpty() && currentPose.getTime() < trackedObjList.get(0).getTime()) {
            notYetTO.add(trackedObjEvent);
            return false;
        }
    
        // Temporary list to store updated landmarks
        List<LandMark> updatedLandMarks = new LinkedList<>();
    
        for (TrackedObject trackedObj : trackedObjList) {
            boolean needNewLandMark = true;
            List<CloudPoint> globalCoordinates = new LinkedList<>();
    
            for (Pose pose : poses) {
                if (pose.getTime() == trackedObj.getTime()) {
                    for (List<Double> point : trackedObj.getCoordinates()) {
                        globalCoordinates.add(ConvertCoordinates(point, pose));
                    }
    
                    for (LandMark landMark : landMarks) {
                        if (trackedObj.getID().equals(landMark.getID())) {
                            List<CloudPoint> newCoordinates = new LinkedList<>();
    
                            Iterator<CloudPoint> LMIterator = landMark.getCoordinates().iterator();
                            Iterator<CloudPoint> GIterator = globalCoordinates.iterator();
    
                            while (GIterator.hasNext()) {
                                if (LMIterator.hasNext()) {
                                    CloudPoint trackedP = GIterator.next();
                                    CloudPoint landMarkP = LMIterator.next();
                                    double landMarkX = landMarkP.getX();
                                    double landMarkY = landMarkP.getY();
                                    landMarkP.setX((landMarkX + trackedP.getX()) / 2);
                                    landMarkP.setY((landMarkY + trackedP.getY()) / 2);
                                    newCoordinates.add(landMarkP);
                                } else {
                                    newCoordinates.add(GIterator.next());
                                }
                            }
                            updatedLandMarks.add(new LandMark(landMark.getID(), landMark.getDescription(), newCoordinates));
                            needNewLandMark = false;
                            break;
                        }
                    }
                }
            }
    
            if (needNewLandMark) {
                landMarks.add(new LandMark(trackedObj.getID(), trackedObj.getDescription(), globalCoordinates));
                System.out.println("********************FUSION SLAM: created new LandMark: " + trackedObj.getID());
                statisticalFolder.increasenumLandMarks();
            }
        }
    
        // Update the main landmark list with the new updates
        for (LandMark updatedLandMark : updatedLandMarks) {
            landMarks.removeIf(lm -> lm.getID().equals(updatedLandMark.getID()));
            landMarks.add(updatedLandMark);
        }
    
        return true;
    }

    public List<LandMark> getWorldMap() {
        return landMarks;
    }
    
}