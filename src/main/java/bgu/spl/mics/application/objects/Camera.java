package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import bgu.spl.mics.Parsers.CameraParser;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */

public class Camera {

    @SerializedName("id")
    private int ID;
    @SerializedName("camera_key")
    private String cameraKey;
    private int frequency;
    private STATUS status;
    private List<StampedDetectedObjects> allObjects;
    private List<StampedDetectedObjects> detectedObjectsList = new LinkedList<>();

    public Camera(int ID, int frequency, String cameraKey, String dataPath) {
        this.ID = ID;
        this.frequency = frequency;
        this.status = STATUS.DOWN;
        this.allObjects = CameraParser.parseCameraData(dataPath, cameraKey);
        this.detectedObjectsList = new LinkedList<>();
    }

    public void initDefault(String dataPath) {
        if (status == null) {
            status = STATUS.DOWN;
        }
        if (allObjects == null) {
            allObjects = CameraParser.parseCameraData(dataPath, cameraKey);
        }
        if (detectedObjectsList == null) {
            detectedObjectsList = new LinkedList<>();
        }
    }

    public int getID() {
        return this.ID;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public STATUS getStatus() {
        return this.status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public String getCameraKey() {
        return this.cameraKey;
    }

    public List<StampedDetectedObjects> getDetectedObjectList() {
        return this.detectedObjectsList;
    }

    public StampedDetectedObjects interval(long tickTime) {
        if (allObjects.isEmpty()) {
            return new StampedDetectedObjects(-2, null);
        }

        for (StampedDetectedObjects obj : allObjects) {
          //  if (tickTime < obj.getTime() + frequency) {
              //  break;
            if (tickTime == obj.getTime() + frequency) {

                if (obj.getDetectedObjects().isEmpty()) {
                    System.out.println("cam null - is empty");
                    return null;
                }
                for (DetectedObject detectedObj : obj.getDetectedObjects()) {
                    if (detectedObj.getID().equals("ERROR")) {
                        System.out.println("camera detected error");
                        return new StampedDetectedObjects(-1, obj.getDetectedObjects());
                    }
                }
                detectedObjectsList.add(obj);
                allObjects.remove(obj);
                //System.out.println("cam returned obj");
                return obj;
            }

        }
        //System.out.println("cam null after for");
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
