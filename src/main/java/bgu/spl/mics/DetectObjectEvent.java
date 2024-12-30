package bgu.spl.mics;

import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.TrackedObject;

public class DetectObjectEvent implements Event<Boolean> {

    private DetectedObject obj;
    private int currentTime;
    public DetectObjectEvent(DetectedObject obj) {
        this.obj = obj;
    }
    public DetectedObject getDetectedObj() {
        return this.obj;
    }
}
