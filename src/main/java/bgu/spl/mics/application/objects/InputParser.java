package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
            if (conf != null && conf.getCameras() != null && conf.getCameras().getCamerasConfigurations() != null) {
                List<Camera> cameras = new LinkedList<>();
                String dataPath = conf.getCameras().getCamera_datas_path();
                
                for (Configuration.CameraConfiguration camConfig : conf.getCameras().getCamerasConfigurations()) {
                    Camera camera = new Camera(
                        camConfig.getId(),
                        camConfig.getFrequency(),
                        camConfig.getCamera_key(),
                        dataPath);
                    cameras.add(camera);
                }
                
                conf.getCameras().setCamerasList(cameras);
            }
            return conf;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
