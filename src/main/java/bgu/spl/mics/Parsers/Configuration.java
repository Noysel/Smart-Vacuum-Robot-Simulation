package bgu.spl.mics.Parsers;

import java.util.List;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;

/**
 * Configuration class to represent the entire configuration structure.
 */
public class Configuration {
   private Cameras Cameras;
   private LiDarWorkers LiDarWorkers;
   private String poseJsonFile;
   private int TickTime;
   private int Duration;

   public List<Camera> getCamerasConfiguration() {
    return Cameras.getCamerasConfiguration();
   }

   public List<LiDarWorkerTracker> getLidarConfigurations() {
    return LiDarWorkers.getLidarConfigurations();
   }

   public String getCameraDataPath() {
    return Cameras.getCameraDatasPath();
   }

   public String getLidarDataPath() {
    return LiDarWorkers.getLidarsDataPath();
   }

   public String getPoseJsonFile() {
    return poseJsonFile;
   }

   public int getTickTime() {
    return TickTime;
   }

   public int getDuration() {
    return Duration;
   }

}

   class Cameras {

    private List<Camera> CamerasConfigurations;
    private String camera_datas_path;

    public List<Camera> getCamerasConfiguration() {
        return CamerasConfigurations;
    }

    public String getCameraDatasPath() {
        return camera_datas_path;
    }
   }

   class LiDarWorkers {

    private List<LiDarWorkerTracker> LidarConfigurations;
    private String lidars_data_path;

    public List<LiDarWorkerTracker> getLidarConfigurations() {
        return LidarConfigurations;
    }

    public String getLidarsDataPath() {
        return lidars_data_path;
    }
   }

