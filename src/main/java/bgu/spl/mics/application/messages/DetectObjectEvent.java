package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.TrackedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;


public class DetectObjectEvent implements Event<Boolean> {

    private StampedDetectedObjects obj;
    private boolean isCompleted;
    public DetectObjectEvent(StampedDetectedObjects obj) {
        this.obj = obj;
        this.isCompleted = false;
    }
    public StampedDetectedObjects getDetectedObj() {
        return this.obj;
    }
    public void complete() {
        this.isCompleted = true;
    }
    public boolean isCompleted() {
        return this.isCompleted;
    }
}
