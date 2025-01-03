package bgu.spl.mics.application.objects;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
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
    @SerializedName("id")
    private int ID;
    @SerializedName("camera_key")
    private String cameraKey;
    private int frequency;
    private Status status;
    private List<StampedDetectedObjects> allObjects;
    private List<StampedDetectedObjects> detectedObjectsList = new LinkedList<>();

    public Camera(int ID, int frequency, String cameraKey, String dataPath) {
        this.ID = ID;
        this.frequency = frequency;
        this.status = Status.UP;
        this.allObjects = CameraParser.parseCameraData(dataPath, cameraKey);
        this.detectedObjectsList = new LinkedList<>();
    }

    public void initDefault(String dataPath) {
        if (status == null) {
            status = Status.UP;
        }
        if (allObjects == null) {
            allObjects = CameraParser.parseCameraData(dataPath, cameraKey);
        }
        if (detectedObjectsList == null){
            detectedObjectsList = new LinkedList<>();
        }
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

    public String getCameraKey() {
        return this.cameraKey;
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

    public String toString() {
        return "Camera{" +
                "ID=" + ID +
                ", frequency=" + frequency +
                ", status=" + status +
                ", allObjects=" + allObjects.size() + 
                ", detectedObjectsList=" + detectedObjectsList.size() + 
                '}';
    }

}
