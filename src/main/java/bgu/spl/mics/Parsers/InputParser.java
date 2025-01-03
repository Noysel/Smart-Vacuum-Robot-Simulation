package bgu.spl.mics.Parsers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;

import java.lang.reflect.Type;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class InputParser {

    public static Configuration parseConfiguration(String filePath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            Configuration conf = gson.fromJson(reader, Configuration.class);
            for (Camera cm : conf.getCamerasConfiguration()) {
                cm.initDefault(conf.getCameraDataPath());
            }
            for (LiDarWorkerTracker lidar : conf.getLidarConfigurations()) {
                lidar.initDefault(conf.getLidarDataPath());
            }
            return conf;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
