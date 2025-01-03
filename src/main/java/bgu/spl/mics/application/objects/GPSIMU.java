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

    private int currentTick;
    private STATUS status;
    private List<Pose> poseList;
    private Iterator<Pose> poseIterator;
    private Pose lastPose;

    public GPSIMU() {
        this.currentTick = 0;
        this.status = STATUS.DOWN;
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

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public Pose getCurrentPose() {
        return lastPose;
    }

    public String toString() {
        return "pose list: " + poseList;
    }
}
