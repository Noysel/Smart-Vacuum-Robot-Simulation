package bgu.spl.mics.application;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.Parsers.InputParser;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.FusionSlamService;
import bgu.spl.mics.application.services.LiDarService;
import bgu.spl.mics.application.services.PoseService;
import bgu.spl.mics.application.services.TimeService;
import bgu.spl.mics.Parsers.*;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the
     *             path to the configuration file.
     */
    public static void main(String[] args) {
        Configuration conf = InputParser.parseConfiguration("example_input_with_error\\configuration_file.json");
        List<Thread> threads = new LinkedList<>();
        int numOfServices = 0;
        for (Camera camera : conf.getCamerasConfiguration()) {
            Thread cameraService = new Thread(new CameraService(camera));
            threads.add(cameraService);
            numOfServices++;
            cameraService.start();
        }
        for (LiDarWorkerTracker lidar : conf.getLidarConfigurations()) {
            Thread lidarService = new Thread(new LiDarService(lidar));
            threads.add(lidarService);
            numOfServices++;
            lidarService.start();
        }
        GPSIMU gps = new GPSIMU(conf.getPoseJsonFile());
        Thread poseService = new Thread(new PoseService(gps));
        numOfServices++;
        poseService.start();
        Thread fusionSlamService = new Thread(new FusionSlamService(FusionSlam.getInstance(), numOfServices));
        fusionSlamService.start();
        Thread timeService = new Thread(new TimeService(conf.getTickTime(), conf.getDuration())); 
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /* 
        while (!threads.isEmpty() || fusionSlamService.get)
                || gps.getStatus() == STATUS.DOWN) {
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
            */
        timeService.run();

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
