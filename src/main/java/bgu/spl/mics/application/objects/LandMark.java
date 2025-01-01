package bgu.spl.mics.application.objects;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    private String ID;
    private String description;
    private List<CloudPoint> coordinates;  

    public LandMark(String ID, String description, List<CloudPoint> coordinates) {
        this.ID = ID;
        this.description = description;
        this.coordinates = coordinates;
    }

    public String getID() {
        return ID;
    }

    public String getDescription() {
        return description;
    }

    public List<CloudPoint> getCoordinates() {
        return coordinates;
    }

    public void addCoordinates(CloudPoint point){
        coordinates.add(point);
        //
    }

}
