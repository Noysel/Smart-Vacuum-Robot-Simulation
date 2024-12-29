package bgu.spl.mics.application.objects;
import java.util.LinkedList;
import java.util.List;

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

    public GPSIMU(int currentTick, Status status) {
        this.currentTick = currentTick;
        this.status = status;
        this.poseList = new LinkedList<>();
    }

    public int getCurrentTick() {
        return currentTick;
    }
    public Status getStatus() {
        return status;
    }
    public List<Pose> getPoseList() {
        return poseList;
    }
}
