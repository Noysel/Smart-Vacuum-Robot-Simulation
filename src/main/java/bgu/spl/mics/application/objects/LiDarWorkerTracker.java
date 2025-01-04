package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.objects.TrackedObject;
import java.util.List;

import com.google.gson.annotations.SerializedName;

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
        this.allObj = LiDarDataBase.getInstance("lidar_data.json").getStampedCloudPoints();
        this.notYetTO = new LinkedList<>();

    }

    public void initDefault(String filePath) {
        if (status == null){
            status = STATUS.DOWN;
        }
        if (lastTrackedObjects == null){
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

    public List<TrackedObject> getLastTrDetectedObjects() {
        return this.lastTrackedObjects;
    }

    public List<TrackedObject> CheckIfTimed(long tickTime) {
        LinkedList<TrackedObject> newLastTracked = new LinkedList<>();
        for (TrackedObject trackedObj : notYetTO){
            if (tickTime >= trackedObj.getTime() + frequency) {
                
                newLastTracked.add(trackedObj);
                notYetTO.remove(trackedObj);
            }
        }
        if (!newLastTracked.isEmpty()){
            lastTrackedObjects = newLastTracked;
            return lastTrackedObjects;
        }
        return null;
    }

    public List<TrackedObject> interval(long tickTime, DetectObjectEvent event) {
        StampedDetectedObjects stampedObj = event.getDetectedObj();

        if (allObj.isEmpty()) {
            List<TrackedObject> terminationList = new LinkedList<>();
            terminationList.add(new TrackedObject("-2", 0, null, null));
            return terminationList;
        }
        for (StampedCloudPoints StampedCPbj : allObj) {
            if (StampedCPbj.getTime() == stampedObj.getTime() + frequency && StampedCPbj.getID() == "ERROR") {
                List<TrackedObject> errorList = new LinkedList<>();
                errorList.add(new TrackedObject("-1", 0, null, null));
                return errorList;
            }

            if (tickTime < StampedCPbj.getTime() + frequency) {
                for (DetectedObject detObj : stampedObj.getDetectedObjects()) {
                    if (detObj.getID() == StampedCPbj.getID()) {
                        TrackedObject trObj = new TrackedObject(StampedCPbj.getID(), StampedCPbj.getTime(), detObj.getDescription(), StampedCPbj.geCloudPoints(), event);
                        notYetTO.add(trObj);
                        allObj.remove(StampedCPbj);
                    }
                }
            }
            else {
                LinkedList<TrackedObject> newLastTracked = new LinkedList<>();
                for (DetectedObject detObj : stampedObj.getDetectedObjects()) {
                    if (detObj.getID() == StampedCPbj.getID() && stampedObj.getTime() == StampedCPbj.getTime()) {
                        TrackedObject trObj = new TrackedObject(StampedCPbj.getID(), StampedCPbj.getTime(), detObj.getDescription(), StampedCPbj.geCloudPoints());
                        newLastTracked.add(trObj);
                        allObj.remove(StampedCPbj);
                    }
                }
                if (newLastTracked != null) {
                    lastTrackedObjects = newLastTracked;   
                }
                return lastTrackedObjects;
            }
        }
        return null;
    }

    public String toString() {
        return ("id:" + ID + ", frequency:" + frequency + ", status:" + status + ", lastTracked:" + lastTrackedObjects + ", ollObj: " + allObj.size()+ ", notYet:" + notYetTO); 
    }
}
