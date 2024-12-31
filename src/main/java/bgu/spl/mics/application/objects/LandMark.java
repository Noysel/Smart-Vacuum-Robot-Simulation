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
    private int updateNum;

    public LandMark(String ID, String description, List<CloudPoint> coordinates) {
        this.ID = ID;
        this.description = description;
        this.coordinates = coordinates;
        this.updateNum = 0;
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

    public int getUpdateNum(){
        return updateNum;
    }

    public void increaseUpdateNum() {
        updateNum++;
        //hhhiiii
    }

}
