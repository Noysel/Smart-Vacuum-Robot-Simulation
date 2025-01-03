package bgu.spl.mics.application.objects;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.DetectObjectEvent;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    private String ID;
    private int time;
    private String description;
    private List<List<Double>> coordinates;
    private DetectObjectEvent event;

    public TrackedObject(String ID, int time, String description, List<List<Double>> coordinates) {
        this.ID = ID;
        this.time = time;
        this.description = description;
        this.coordinates = coordinates;
        this.event = null;
    }

    public TrackedObject(String ID, int time, String description, List<List<Double>> coordinates, DetectObjectEvent event) {
        this.ID = ID;
        this.time = time;
        this.description = description;
        this.coordinates = coordinates;
        this.event = event;
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
    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    public DetectObjectEvent getEvent() {
        return event;
    }

    public String toString() {
        return "objID:" + ID + ", objTime: " + time + " description:" + description + ", coord:" + coordinates;
    }
}
