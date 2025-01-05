package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.TrackedObject;
import java.util.List;

public class TrackedObjectEvent implements Event<Boolean> {

    private List<TrackedObject> listOfTracked;
    private String sender;
    public TrackedObjectEvent(List<TrackedObject> listOfTracked, String sender) {
        this.listOfTracked = listOfTracked;
        this.sender = sender;
    }
    public List<TrackedObject> getTrackedObjectList() {
        return listOfTracked;
    }
    
    public String getSedner() {
        return sender;
    }

}
