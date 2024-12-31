package bgu.spl.mics.application.services;

import bgu.spl.mics.Message;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Pose;

public class PoseEvent implements Event<Boolean> {
    private Pose pose;
    public PoseEvent(Pose pose) {
        this.pose = pose;
    }

    public Pose getPose() {
        return pose;
    }

}
