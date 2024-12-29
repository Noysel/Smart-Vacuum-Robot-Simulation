package bgu.spl.mics.application.objects;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    private String ID;
    private int time;
    private List<CloudPoint> cloudPoints;
    public StampedCloudPoints(String ID, int time) {
        this.ID = ID;
        this.time = time;
        cloudPoints = new LinkedList<>();
    }
    public String getID() {
        return ID;
    }
    public int getTime() {
        return time;
    }
    public List<CloudPoint> geCloudPoints() {
        return cloudPoints;
    }
}
