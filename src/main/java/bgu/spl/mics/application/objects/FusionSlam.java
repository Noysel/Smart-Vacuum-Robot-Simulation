package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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

/**
 * Processes a TrackedObjectEvent to update or insert landmarks into the global map.
 *
 * Invariant:
 * - The `landMarks` list contains only unique landmarks, identified by their `ID`.
 * - The `notYetTO` list contains only events with tracked objects whose times are greater than the `currentPose` time.
 * - Each `TrackedObject` in `trackedObjEvent` is processed once per call.
 *
 * Preconditions:
 * - `trackedObjEvent` is not null.
 * - `trackedObjEvent.getTrackedObjectList()` is not null and contains valid `TrackedObject` instances.
 * - `currentPose` is not null and represents the robot's latest pose.
 *
 * Postconditions:
 * - If the `currentPose` time is less than the time of the `TrackedObject` in `trackedObjEvent`:
 *   - `trackedObjEvent` is added to the `notYetTO` list.
 *   - The method returns `false`.
 * - If the `currentPose` time is greater than or equal to the time of the `TrackedObject`:
 *   - Each `TrackedObject` is processed:
 *     - If a matching landmark exists (same `ID`):
 *       - The landmark's coordinates are updated by averaging with the global coordinates of the tracked object.
 *       - If the number of CloudPoints is greater than the landmark's number, add them to the landmarks coordinates.
 *     - If no matching landmark exists:
 *       - A new landmark is created with the tracked object's global coordinates.
 *       - The number of landmarks in the `statisticalFolder` is incremented.
 *   - The method updates or inserts landmarks and returns `true`.
 */

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

    public void clearLandmarks() {
        this.landMarks.clear();
    }

    // Method to insert a new landmark if it doesn't already exist
    public boolean insertLandMark(LandMark newLandmark) {
        Optional<LandMark> existingLandmark = landMarks.stream()
                .filter(l -> l.getID().equals(newLandmark.getID()))
                .findFirst();
        if (!existingLandmark.isPresent()) {
            landMarks.add(newLandmark);
            return true;
        }
        return false;
    }

    // Method to update an existing landmark
    public boolean updateLandMark(LandMark updatedLandmark) {
        for (int i = 0; i < landMarks.size(); i++) {
            LandMark lm = landMarks.get(i);
            if (lm.getID().equals(updatedLandmark.getID())) {
                landMarks.set(i, updatedLandmark);
                return true;
            }
        }
        return false;
    }

    public List<LandMark> getLandmarks() {
        return landMarks;
    }

}