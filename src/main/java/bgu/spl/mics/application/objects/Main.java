package bgu.spl.mics.application.objects;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.TickBroadcast;
import bgu.spl.mics.application.services.*;
import java.util.List;
import java.util.LinkedList;

public class Main {

    public static void main(String[] args) {
      
        Configuration conf = InputParser.parseConfiguration("example_input_2\\configuration_file.json");
        List<CameraService> camServList = new LinkedList<>();
        List<LiDarService> lidarServList = new LinkedList<>();
        for (Camera camera : conf.getCameras().getCamerasConfiguration()) {
            CameraService cameraServ = new CameraService(camera);
            camServList.add(cameraServ);
            cameraServ.run();
        }
        for (LiDarWorkerTracker lidar : conf.getLiDarWorkers().getLidarConfigurations()) {
            LiDarService lidarServ = new LiDarService(lidar);
            lidarServList.add(lidarServ);
            lidarServ.run();
        }
        GPSIMU gps = new GPSIMU();
        PoseService poseServ = new PoseService(gps);
        poseServ.run();
        FusionSlamService fusionSlamServ = new FusionSlamService(FusionSlam.getInstance());
        fusionSlamServ.run();
        TimeService timeServ = new TimeService(conf.getTickTime(), conf.getDuration());
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (!lidarServList.isEmpty() || !camServList.isEmpty() || fusionSlamServ.getStatus() == STATUS.DOWN || gps.getStatus() == STATUS.DOWN) {
            for (CameraService camServ : camServList) {
                if (camServ.getStatus() == STATUS.UP) {
                    camServList.remove(camServ);
                }
            }
            for (LiDarService lidarServ : lidarServList) {
                if (lidarServ.getStatus() == STATUS.UP) {
                    lidarServList.remove(lidarServ);
                }
            }
        }
        timeServ.run();

        System.out.println(lidarServList);
        System.out.println(gps.getStatus());
        System.out.println(fusionSlamServ.getStatus());

    }

}
