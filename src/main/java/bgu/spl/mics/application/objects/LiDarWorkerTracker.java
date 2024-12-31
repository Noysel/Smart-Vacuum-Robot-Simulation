package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.objects.TrackedObject;
import java.util.List;
import java.util.LinkedList;
import bgu.spl.mics.DetectObjectEvent;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using
 * data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private enum Status {
        UP,
        DOWN,
        ERROR
    }

    private int ID;
    private int frequency;
    private Status status;
    private List<TrackedObject> lastTrackedObjects;
    private List<StampedCloudPoints> allObj;
    private List<DetectObjectEvent> notYetEvent;

    public LiDarWorkerTracker(int ID, int frequency, Status status) {
        this.ID = ID;
        this.frequency = frequency;
        this.status = status;
        this.lastTrackedObjects = new LinkedList<TrackedObject>();
        this.allObj = LiDarDataBase.getInstance("lidar_data.json").getStampedCloudPoints();
        this.notYetEvent = new LinkedList<>();

    }

    public int getID() {
        return this.ID;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public Status geStatus() {
        return this.status;
    }

    public List<TrackedObject> getLastTrDetectedObjects() {
        return this.lastTrackedObjects;
    }

    public List<DetectObjectEvent> getTimedEvents(int tickTime) {
        List<DetectObjectEvent> output = new LinkedList<>();
        for (DetectObjectEvent event : notYetEvent){
            StampedDetectedObjects stampedObj = event.getDetectedObj();
            if (tickTime >= stampedObj.getTime() + frequency) {
                output.add(event);
            }
        }
        return output;
    }

    public List<TrackedObject> interval(int tickTime, DetectObjectEvent detectedObjEvent) {
        StampedDetectedObjects stampedObj = detectedObjEvent.getDetectedObj();
        for (StampedCloudPoints obj : allObj) {
            if (tickTime < obj.getTime() + frequency) {
                for (DetectedObject detObj : stampedObj.getDetectedObjects()) {
                    if (detObj.getID() == obj.getID()) {
                        notYetEvent.add(detectedObjEvent);
                        allObj.remove(obj);
                    }
                }
            }
            else {
                LinkedList<TrackedObject> newLastTracked = new LinkedList<>();
                for (DetectedObject detObj : stampedObj.getDetectedObjects()) {
                    if (detObj.getID() == obj.getID()) {
                        TrackedObject trObj = new TrackedObject(obj.getID(), obj.getTime(), detObj.getDescription(), obj.geCloudPoints());
                        newLastTracked.add(trObj);
                        allObj.remove(obj);
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
}
