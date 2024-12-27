package bgu.spl.mics.application.objects;
import java.util.List;
/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */

public class Camera {
    public enum Status {
        UP,
        DOWN,
        ERROR
    }
    private int id;
    private int frequency;
    private Status status;
    private List<DetectedObject> detectedObjectsList;

    public Camera() {
        
    }

}
