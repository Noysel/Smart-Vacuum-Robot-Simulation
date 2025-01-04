package bgu.spl.mics.Parsers;
import java.util.concurrent.atomic.AtomicInteger;
import bgu.spl.mics.application.objects.LandMark;

public class Output {
    private int systemRuntime;
    private int numDetectedObjects;
    private int numTrackedObjects;
    private int numLandmarks;
    private LandMark[] landMarks;

    public Output(int systemRuntime, int numDetectedObjects, int numTrackedObjects, int numLandmarks, LandMark[] landMarks) {
        this.systemRuntime = systemRuntime;
        this.numDetectedObjects = numDetectedObjects;
        this.numTrackedObjects = numTrackedObjects;
        this.numLandmarks = numLandmarks;
        this.landMarks = landMarks;
    }

    public int getSystemRuntime() {
        return systemRuntime;
    }

    public int getNumDetectedObjects() {
        return numDetectedObjects;
    }

    public int getNumTrackedObjects() {
        return numTrackedObjects;
    }

    public int getNumLandmarks() {
        return numLandmarks;
    }

    public LandMark[] getLandMarks() {
        return landMarks;
    }
}

