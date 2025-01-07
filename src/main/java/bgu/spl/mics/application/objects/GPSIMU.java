package bgu.spl.mics.application.objects;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.Parsers.PoseParser;

import java.util.Iterator;

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

    public GPSIMU(String filePath) {
        this.currentTick = 0;
        this.status = STATUS.DOWN;
        this.poseList = PoseParser.parsePoseData(filePath);
        this.poseIterator = poseList.iterator();
        this.lastPose = null;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public boolean increaseCurrentTick() {
        currentTick++;
        if (poseIterator.hasNext()) {
            this.lastPose = poseIterator.next();
            return true;
        }
        return false;
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
