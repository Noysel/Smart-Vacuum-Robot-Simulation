package bgu.spl.mics.Parsers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import bgu.spl.mics.application.objects.StampedDetectedObjects;

import java.lang.reflect.Type;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CameraParser {

      public static List<StampedDetectedObjects> parseCameraData(String filePath, String cameraKey) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<Map<String, List<StampedDetectedObjects>>>(){}.getType();
            Map<String, List<StampedDetectedObjects>> data = gson.fromJson(reader, type);
            return data.get(cameraKey);  // Retrieve the list of detected objects for the specific camera
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
