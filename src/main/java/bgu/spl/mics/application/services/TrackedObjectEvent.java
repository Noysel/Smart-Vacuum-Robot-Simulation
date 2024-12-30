package bgu.spl.mics.application.services;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.TrackedObject;
import java.util.List;

public class TrackedObjectEvent implements Event<LandMark> {

    private List<TrackedObject> listOfTracked;
    public TrackedObjectEvent(List<TrackedObject> listOfTracked) {
        this.listOfTracked = listOfTracked;
    }
    public List<TrackedObject> getTrackedObjectList() {
        return listOfTracked;
    }

}
