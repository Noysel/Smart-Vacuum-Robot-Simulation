package bgu.spl.mics.application.objects;

import com.google.gson.annotations.SerializedName;

/**
 * DetectedObject represents an object detected by the camera.
 * It contains information such as the object's ID and description.
 */

public class DetectedObject {

    @SerializedName("id")
    private String ID;
    private String description;
    
    public DetectedObject(String ID, String description) {
        this.ID = ID;
        this.description = description;
    }
    public String getID() {
        return this.ID;
    }
    public String getDescription() {
        return this.description;
    }
}
