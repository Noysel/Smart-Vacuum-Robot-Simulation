package bgu.spl.mics.application.objects;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.TickBroadcast;
import bgu.spl.mics.application.services.*;


import java.util.List;
import java.util.LinkedList;

public class Main {

    public static void main(String[] args) {
      
        Configuration conf = InputParser.parseConfiguration("example_input_2\\configuration_file.json");
          /* 
        //MessageBusImpl messageBus = MessageBusImpl.getInstance();
        for (Camera camera : conf.getCameras().getCamerasConfiguration()) {
            CameraService cameraServ = new CameraService(camera);
            cameraServ.run();
        }
        for (LiDarWorkerTracker lidar : conf.getLiDarWorkers().getLidarConfigurations()) {
            LiDarService lidarServ = new LiDarService(lidar);
            lidarServ.run();
        }
        PoseService poseServ = new PoseService(new GPSIMU());
        poseServ.run();
        FusionSlamService fusionSlamServ = new FusionSlamService(FusionSlam.getInstance());
        fusionSlamServ.run();
        TimeService timeServ = new TimeService(conf.getTickTime(), conf.getDuration());
        timeServ.run();
        */

        GPSIMU gps = new GPSIMU();
        System.out.println(gps);
        for (Camera cm : conf.getCameras().getCamerasConfiguration()) {
            System.out.println(cm);
        }
        for (LiDarWorkerTracker lidar : conf.getLiDarWorkers().getLidarConfigurations()) {
            System.out.println(lidar); 
        }

    }

}
