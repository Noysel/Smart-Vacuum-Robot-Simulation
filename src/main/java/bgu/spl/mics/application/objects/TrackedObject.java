package bgu.spl.mics.application.objects;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    private String ID;
    private int time;
    private String description;
    private List<CloudPoint> coordinates;

    public TrackedObject(String ID, int time, String description) {
        this.ID = ID;
        this.time = time;
        this.description = description;
        List<CloudPoint> coordinates = new LinkedList<>();
    }
    public String getID() {
        return ID;
    }
    public int getTime() {
        return time;
    }
    public String getDescription() {
        return description;
    }
    public List<CloudPoint> getCoordinates() {
        return coordinates;
    }

}
