package bgu.spl.mics.application.objects;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.Parsers.Configuration;
import bgu.spl.mics.Parsers.InputParser;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.services.*;
import bgu.spl.mics.Parsers.*;
import java.util.List;
import java.util.LinkedList;

public class Main {

    public static void main(String[] args) {

        /*
         * Configuration conf =
         * InputParser.parseConfiguration("example_input_2\\configuration_file.json");
         * List<CameraService> camServList = new LinkedList<>();
         * List<LiDarService> lidarServList = new LinkedList<>();
         * int numOfServices = 0;
         * for (Camera camera : conf.getCamerasConfiguration()) {
         * CameraService cameraServ = new CameraService(camera);
         * camServList.add(cameraServ);
         * numOfServices++;
         * cameraServ.run();
         * }
         * for (LiDarWorkerTracker lidar : conf.getLidarConfigurations()) {
         * LiDarService lidarServ = new LiDarService(lidar);
         * lidarServList.add(lidarServ);
         * numOfServices++;
         * lidarServ.run();
         * }
         * GPSIMU gps = new GPSIMU();
         * PoseService poseServ = new PoseService(gps);
         * numOfServices++;
         * poseServ.run();
         * FusionSlamService fusionSlamServ = new
         * FusionSlamService(FusionSlam.getInstance(), numOfServices);
         * fusionSlamServ.run();
         * TimeService timeServ = new TimeService(conf.getTickTime(),
         * conf.getDuration());
         * try {
         * Thread.sleep(1000);
         * }
         * catch (InterruptedException e) {
         * e.printStackTrace();
         * }
         * while (!lidarServList.isEmpty() || !camServList.isEmpty() ||
         * fusionSlamServ.getStatus() == STATUS.DOWN || gps.getStatus() == STATUS.DOWN)
         * {
         * for (CameraService camServ : camServList) {
         * if (camServ.getStatus() == STATUS.UP) {
         * camServList.remove(camServ);
         * }
         * }
         * for (LiDarService lidarServ : lidarServList) {
         * if (lidarServ.getStatus() == STATUS.UP) {
         * lidarServList.remove(lidarServ);
         * }
         * }
         * }
         * timeServ.run();
         * boolean isError = false;
         * for (Camera camera : conf.getCamerasConfiguration()) {
         * if (camera.getStatus() == STATUS.ERROR) {
         * isError = true;
         * break;
         * }
         * }
         * for (LiDarWorkerTracker lidar : conf.getLidarConfigurations()) {
         * if (lidar.getStatus() == STATUS.ERROR) {
         * isError = true;
         * break;
         * }
         * }
         * 
         * if (!isError) {
         * 
         * }
         * 
         */

        //////////////////////////
        /*
         * System.out.println(lidarServList);
         * System.out.println(gps.getStatus());
         * System.out.println(fusionSlamServ.getStatus());
         */
        Configuration conf = InputParser.parseConfiguration("example_input_2\\configuration_file.json");
        

        
        Thread cameraThread1 = new Thread(new CameraService(conf.getCamerasConfiguration().get(0)));
        // Thread cameraThread2 = new Thread(new
        // CameraService(conf.getCamerasConfiguration().get(1)));
        Thread timeThread = new Thread(new TimeService(conf.getTickTime(), conf.getDuration()));
        Thread Lidar1Thread = new Thread(new LiDarService(conf.getLidarConfigurations().get(0)));
        // Thread Lidar2 = new Thread(new
        // LiDarService(conf.getLidarConfigurations().get(1)));
        Thread poseThread = new Thread(new PoseService(new GPSIMU(conf.getPoseJsonFile())));
        Thread fusionSlamThread = new Thread(new FusionSlamService(new FusionSlam(), 3));

        // Start the threads
        cameraThread1.start();
        // cameraThread2.start();
        Lidar1Thread.start();
        // Lidar2.start();
        poseThread.start();
        fusionSlamThread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timeThread.start();

        try {
            fusionSlamThread.join();
            timeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
