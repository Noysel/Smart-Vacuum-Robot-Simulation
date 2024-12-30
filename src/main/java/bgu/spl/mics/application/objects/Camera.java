package bgu.spl.mics.application.objects;
import java.util.LinkedList;
import java.util.List;
/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */

public class Camera {
    private enum Status {
        UP,
        DOWN,
        ERROR
    }
    private int ID;
    private int frequency;
    private Status status;
    private List<StampedDetectedObjects> allObjects;
    private List<StampedDetectedObjects> detectedObjectsList;

    public Camera(int ID, int frequency, Status status) {
        this.ID = ID;
        this.frequency = frequency;
        this.status = status;
        this.allObjects = CameraParser.parseCameraData("camera_data.json");
        this.detectedObjectsList = new LinkedList<>();
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
    public List<StampedDetectedObjects> getDetectedObjectList() {
        return this.detectedObjectsList;
    }

    public StampedDetectedObjects interval(int tickTime) {
        for (StampedDetectedObjects obj : allObjects) {
            if (tickTime < obj.getTime() + frequency) {
                break;
            }
            else {
                if (tickTime == obj.getTime() + frequency)
                detectedObjectsList.add(obj);
                allObjects.remove(obj);
                return obj;
            }
        }
        return null;
    } 

}
