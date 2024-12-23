package bgu.spl.mics.application.objects;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
import jave.util.List;
enum status {
    UP,
    DOWN,
    ERROR
}
public class Camera {
    private int id;
    private int frequency;
    private enum status;
    private List<DetectedObject> detectedObjectList;

}
