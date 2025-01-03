package bgu.spl.mics.application.objects;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

import bgu.spl.mics.application.services.PoseParser;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private enum Status {
        UP,
        DOWN,
        ERROR
    }
    private int currentTick;
    private Status status;
    private List<Pose> poseList;
    private Iterator<Pose> poseIterator;
    private Pose lastPose;

    public GPSIMU() {
        this.currentTick = 0;
        this.status = Status.UP;
        this.poseList = PoseParser.parsePoseData("example_input_2\\pose_data.json");
        this.poseIterator = poseList.iterator();
        this.lastPose = null;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public void increaseCurrentTick() {
        currentTick++;
        this.lastPose = poseIterator.next();
    }

    public Status getStatus() {
        return status;
    }
    public Pose getCurrentPose() {
        return lastPose;
    }

    public String toString() {
        return "pose list: " + poseList;
    }
}
