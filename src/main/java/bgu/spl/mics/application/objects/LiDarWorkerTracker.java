package bgu.spl.mics.application.objects;
import bgu.spl.mics.application.objects.TrackedObject;
import java.util.List;
import java.util.LinkedList;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
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
    public LiDarWorkerTracker(int ID, int frequency, Status status) {
        this.ID = ID;
        this.frequency = frequency;
        this.status = status;
        this.lastTrackedObjects = new LinkedList<TrackedObject>();
        this.allObj = LiDarDataBase.getInstance("lidar_data.json").getStampedCloudPoints();

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
    public StampedCloudPoints interval(int tickTime){
        for (StampedCloudPoints obj : allObj) {
            if (tickTime < obj.getTime() + frequency) {
                break;
            }
            else {
                lastTrackedObjects.add(obj);
                allObj.remove(obj);
                return obj;
            }
        }
        return null;
    }

}
