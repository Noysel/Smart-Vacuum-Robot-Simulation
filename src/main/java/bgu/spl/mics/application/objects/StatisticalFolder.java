package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import bgu.spl.mics.MessageBusImpl;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks
 * identified.
 */
public class StatisticalFolder {
    private AtomicInteger systemRunTime;
    private AtomicInteger numDetectedObjects;
    private AtomicInteger numTrackedObjects;
    private AtomicInteger numLandMarks;
    private String error;
    private String faultySensor;
    private List<LandMark> worldMap;
    private LastFrames lastFrames = new LastFrames();;
    private List<Pose> poses = new LinkedList<>();

    private static class SingletonHolder {
        private volatile static StatisticalFolder instance = new StatisticalFolder();
    }

    public static StatisticalFolder getInstance() {
        return SingletonHolder.instance;
    }

    public StatisticalFolder(int systemRunTime, int numDetectedObjects, int numTrackedObjects, int numLandMarks) {
        this.systemRunTime = new AtomicInteger(systemRunTime);
        this.numDetectedObjects = new AtomicInteger(numDetectedObjects);
        this.numTrackedObjects = new AtomicInteger(numTrackedObjects);
        this.numLandMarks = new AtomicInteger(numLandMarks);
        //this.poses = new LinkedList<>();
    }

    public StatisticalFolder() {
        this.systemRunTime = new AtomicInteger(0);
        this.numDetectedObjects = new AtomicInteger(0);
        this.numTrackedObjects = new AtomicInteger(0);
        this.numLandMarks = new AtomicInteger(0);
    }

    public int getSystemRumTime() {
        return systemRunTime.get();
    }

    public void setError(String description, String sender) {
        error = description;
        faultySensor = sender;
    }

    public boolean isError() {
        if (error == null) {
            return false;
        }
        return true;
    }

    public int getnumDetectedObjects() {
        return numDetectedObjects.get();
    }

    public int getnumTrackedObjects() {
        return numTrackedObjects.get();
    }

    public int getnumLandMarks() {
        return numLandMarks.get();
    }

    public void increaseSystemRunTime() {
        int oldVal;
        int newVal;
        do {
            oldVal = systemRunTime.get();
            newVal = oldVal + 1;
        } while (!systemRunTime.compareAndSet(oldVal, newVal));
    }

    public void increasenumDetectedObjects() {
        int oldVal;
        int newVal;
        do {
            oldVal = numDetectedObjects.get();
            newVal = oldVal + 1;
        } while (!numDetectedObjects.compareAndSet(oldVal, newVal));
    }

    public void increasenumTrackedObjects() {
        int oldVal;
        int newVal;
        do {
            oldVal = numTrackedObjects.get();
            newVal = oldVal + 1;
        } while (!numTrackedObjects.compareAndSet(oldVal, newVal));
    }

    public void increasenumLandMarks() {
        int oldVal;
        int newVal;
        do {
            oldVal = numLandMarks.get();
            newVal = oldVal + 1;
        } while (!numLandMarks.compareAndSet(oldVal, newVal));
    }

    public void setLastDetectedObj(List<DetectedObject> lastDetectedObj) {
        lastFrames.setLastDetectedObj(lastDetectedObj);
    }

    public void setlastTrackedObj(List<TrackedObject> lastTrackedObj) {
        lastFrames.setlastTrackedObj(lastTrackedObj);
    }

    public void addPose(Pose lastPose) {
        this.poses.add(lastPose);
    }

    public void setWorldMap(List<LandMark> worldMap) {
        this.worldMap = worldMap;
    }

    public void createOutputFile(String filename) {
        System.out.println(isError() + ", " + error + "IS ERROR @@@@@@@@@@@@@@@@@@@@");


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            if (isError()) {
                ErrorOutputData errorData = new ErrorOutputData(
                    error,
                    faultySensor,
                    lastFrames,
                    poses,
                    new Statistics(systemRunTime.get(), numDetectedObjects.get(), numTrackedObjects.get(), numLandMarks.get(), worldMap),
                    worldMap
                );
                gson.toJson(errorData, writer);
            } else {
                OutputData data = new OutputData(
                    systemRunTime.get(),
                    numDetectedObjects.get(),
                    numTrackedObjects.get(),
                    numLandMarks.get(),
                    worldMap
                );
                gson.toJson(data, writer);
            }
            System.out.println("Output JSON has been written to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class OutputData {
        int systemRuntime;
        int numDetectedObjects;
        int numTrackedObjects;
        int numLandmarks;
        List<LandMark> landMarks;

        public OutputData(int systemRuntime, int numDetectedObjects, int numTrackedObjects, int numLandmarks, List<LandMark> landMarks) {
            this.systemRuntime = systemRuntime;
            this.numDetectedObjects = numDetectedObjects;
            this.numTrackedObjects = numTrackedObjects;
            this.numLandmarks = numLandmarks;
            this.landMarks = landMarks;
        }
    }

    private class ErrorOutputData {
        String error;
        String faultySensor;
        LastFrames lastFrames;
        List<Pose> poses;
        Statistics statistics;
        List<LandMark> landMarks;


        public ErrorOutputData(String error, String faultySensor, LastFrames lastFrames, List<Pose> poses, Statistics statistics, List<LandMark> landMarks) {
            this.error = error;
            this.faultySensor = faultySensor;
            this.lastFrames = lastFrames;
            this.poses = poses;
            this.statistics = statistics;
            this.landMarks = landMarks;
        }
    }

    private class Statistics {
        int systemRuntime;
        int numDetectedObjects;
        int numTrackedObjects;
        int numLandmarks;
        List<LandMark> landMarks;

        public Statistics(int systemRuntime, int numDetectedObjects, int numTrackedObjects, int numLandmarks, List<LandMark> landMarks) {
            this.systemRuntime = systemRuntime;
            this.numDetectedObjects = numDetectedObjects;
            this.numTrackedObjects = numTrackedObjects;
            this.numLandmarks = numLandmarks;
            this.landMarks = landMarks;
        }
    }
}

class LastFrames {
    private volatile List<DetectedObject> lastDetectedObj;
    private volatile List<TrackedObject> lastTrackedObj;

    public synchronized void setLastDetectedObj(List<DetectedObject> lastDetectedObj) {
        this.lastDetectedObj = lastDetectedObj;
    }

    public synchronized void setlastTrackedObj(List<TrackedObject> lastTrackedObj) {
        this.lastTrackedObj = lastTrackedObj;
    }
}
