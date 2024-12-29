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

    public LandMark(String ID, String description) {
        this.ID = ID;
        this.description = description;
        List<CloudPoint> coordinates = new LinkedList<>();
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

}
