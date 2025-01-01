package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Configuration class to represent the entire configuration structure.
 */
public class Configuration {
    private CameraConfig Cameras;
    private LiDarWorkerConfig LiDarWorkers;
    private String poseJsonFile;
    private int TickTime;
    private int Duration;

    // Getters and setters for all properties
    public CameraConfig getCameras() {
        return Cameras;
    }

    public void setCameras(CameraConfig cameras) {
        Cameras = cameras;
    }

    public LiDarWorkerConfig getLiDarWorkers() {
        return LiDarWorkers; //
    }

    public void setLiDarWorkers(LiDarWorkerConfig liDarWorkers) {
        LiDarWorkers = liDarWorkers;
    }

    public String getPoseJsonFile() {
        return poseJsonFile;
    }

    public void setPoseJsonFile(String poseJsonFile) {
        this.poseJsonFile = poseJsonFile;
    }

    public int getTickTime() {
        return TickTime;
    }

    public void setTickTime(int tickTime) {
        TickTime = tickTime;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    /**
     * Nested class to manage configurations specific to cameras.
     */
    public static class CameraConfig {
        private List<Camera> CamerasConfigurations;
        private String camera_datas_path;

        public List<Camera> getCamerasList() {
            return CamerasConfigurations;
        }

        public void setCamerasList(List<Camera> cameras) {
            CamerasConfigurations = cameras;
        }

        public String getCamera_datas_path() {
            return camera_datas_path;
        }

        public void setCamera_datas_path(String camera_datas_path) {
            this.camera_datas_path = camera_datas_path;
        }
    }

    /**
     * Nested class to manage configurations specific to LiDar workers.
     */
    public static class LiDarWorkerConfig {
        private List<LiDarWorkerTracker> LidarConfigurations;
        private String lidars_data_path;

        public List<LiDarWorkerTracker> getLidarConfigurations() {
            return LidarConfigurations;
        }

        public void setLidarConfigurations(List<LiDarWorkerTracker> lidarConfigurations) {
            LidarConfigurations = lidarConfigurations;
        }

        public String getLidars_data_path() {
            return lidars_data_path;
        }

        public void setLidars_data_path(String lidars_data_path) {
            this.lidars_data_path = lidars_data_path;
        }
    }
}