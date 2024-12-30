package bgu.spl.mics;

import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.TrackedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;


public class DetectObjectEvent implements Event<Boolean> {

    private StampedDetectedObjects obj;
    public DetectObjectEvent(StampedDetectedObjects obj) {
        this.obj = obj;
    }
    public StampedDetectedObjects getDetectedObj() {
        return this.obj;
    }
}
