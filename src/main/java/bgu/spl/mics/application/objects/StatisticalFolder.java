package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicInteger;

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
        }
        while (!systemRunTime.compareAndSet(oldVal, newVal));
    }

    public void increasenumDetectedObjects() {
        int oldVal;
        int newVal;
        do {
            oldVal = numDetectedObjects.get();
            newVal = oldVal + 1;
        }
        while (!numDetectedObjects.compareAndSet(oldVal, newVal));
    }

    public void increasenumTrackedObjects() {
        int oldVal;
        int newVal;
        do {
            oldVal = numTrackedObjects.get();
            newVal = oldVal + 1;
        }
        while (!numTrackedObjects.compareAndSet(oldVal, newVal));
    }

    public void increasenumLandMarks() {
        int oldVal;
        int newVal;
        do {
            oldVal = numLandMarks.get();
            newVal = oldVal + 1;
        }
        while (!numLandMarks.compareAndSet(oldVal, newVal));    }
}
