package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.objects.TrackedObject;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import java.util.Iterator;
import java.util.LinkedList;

import bgu.spl.mics.application.messages.DetectObjectEvent;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using
 * data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {

    @SerializedName("id")
    private int ID;
    private int frequency;
    private STATUS status;
    private List<TrackedObject> lastTrackedObjects;
    private List<StampedCloudPoints> allObj;
    private List<TrackedObject> notYetTO;

    public LiDarWorkerTracker(int ID, int frequency) {
        this.ID = ID;
        this.frequency = frequency;
        this.status = STATUS.DOWN;
        this.lastTrackedObjects = new LinkedList<TrackedObject>();
        this.allObj = null;
        this.notYetTO = new LinkedList<>();

    }

    public void initDefault(String filePath) {
        if (status == null) {
            status = STATUS.DOWN;
        }
        if (lastTrackedObjects == null) {
            lastTrackedObjects = new LinkedList<>();
        }
        if (allObj == null) {
            allObj = LiDarDataBase.getInstance(filePath).getStampedCloudPoints();
        }
        if (notYetTO == null) {
            notYetTO = new LinkedList<>();
        }
    }

    public int getID() {
        return this.ID;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public STATUS getStatus() {
        return this.status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public int getLidarsDataBaseSize() {
        return allObj.size();
    }

    public List<TrackedObject> getLastTrDetectedObjects() {
        return this.lastTrackedObjects;
    }

    public List<TrackedObject> CheckIfTimed(long tickTime) {
        LinkedList<TrackedObject> newLastTracked = new LinkedList<>();
        for (TrackedObject trackedObj : notYetTO) {
            if (tickTime >= trackedObj.getTime() + frequency) {

                if (trackedObj.getID().equals("ERROR")) {
                    List<TrackedObject> errorList = new LinkedList<>();
                    errorList.add(new TrackedObject("-1", 0, null, null));
                    return errorList;
                }
                newLastTracked.add(trackedObj);
                notYetTO.remove(trackedObj);
            }
        }
        if (!newLastTracked.isEmpty()) {
            lastTrackedObjects = newLastTracked;
            return lastTrackedObjects;
        }
        return null;
    }

    public List<TrackedObject> interval(long tickTime, DetectObjectEvent event) {
        StampedDetectedObjects stampedObj = event.getDetectedObj();
        System.out.println("StampedObj event's size: " + stampedObj.getDetectedObjects().size());

        LinkedList<TrackedObject> newLastTracked = new LinkedList<>();
        for (DetectedObject detObj : stampedObj.getDetectedObjects()) {
            System.out.println(detObj.getID() + " Recived by LiDar " + this.ID);
            for (StampedCloudPoints cloudPoints : allObj) {
                if (cloudPoints.getID().equals(detObj.getID()) && stampedObj.getTime() == cloudPoints.getTime() || cloudPoints.getID().equals("ERROR")) {
                    if (tickTime < cloudPoints.getTime()) {
                        TrackedObject trObj = new TrackedObject(cloudPoints.getID(), cloudPoints.getTime(),
                                detObj.getDescription(), cloudPoints.geCloudPoints(), event);
                        notYetTO.add(trObj);
                        break;
                    } else {
                        if (cloudPoints.getID().equals("ERROR")) {
                            List<TrackedObject> errorList = new LinkedList<>();
                            errorList.add(new TrackedObject("-1", 0, null, null));
                            return errorList; // Check if lastTracked should be the errorList
                        } else {
                            TrackedObject trObj = new TrackedObject(cloudPoints.getID(), cloudPoints.getTime(),
                                    detObj.getDescription(), cloudPoints.geCloudPoints());
                            newLastTracked.add(trObj);
                            break;
                        }
                    }
                }
            }
            
        }
        if (newLastTracked != null) {
            lastTrackedObjects = newLastTracked;
            return newLastTracked;
        }
        return null;
    }

    public String toString() {
        return ("id:" + ID + ", frequency:" + frequency + ", status:" + status + ", lastTracked:" + lastTrackedObjects
                + ", ollObj: " + allObj.size() + ", notYet:" + notYetTO);
    }
}
