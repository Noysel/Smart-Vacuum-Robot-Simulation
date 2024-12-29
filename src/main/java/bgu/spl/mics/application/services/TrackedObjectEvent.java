package bgu.spl.mics.application.services;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.TrackedObject;

public class TrackedObjectEvent implements Event<LandMark> {

    private DetectedObject obj;
    public TrackedObjectEvent(DetectedObject obj) {
        this.obj = obj;
    }
    public DetectedObject getTrackedObject() {
        return obj;
    }

}
