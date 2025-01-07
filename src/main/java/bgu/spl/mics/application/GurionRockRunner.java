package bgu.spl.mics.application;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StatisticalFolder;
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
        if (args.length < 1) {
            System.err.println("Need path for the configuration file!");
        }
        String configFilePath = args[0];
        Configuration conf = InputParser.parseConfiguration(configFilePath);
        int numOfServices = conf.getCamerasConfiguration().size() + conf.getLidarConfigurations().size() + 2; // +2 for GPS and FusionSlam
        CountDownLatch readyLatch = new CountDownLatch(numOfServices);

        List<Thread> serviceThreads = new LinkedList<>();
        
        // Start CameraService for each camera configuration
        for (Camera camera : conf.getCamerasConfiguration()) {
            Thread cameraThread = new Thread(() -> {
                new CameraService(camera, readyLatch).run();
            });
            serviceThreads.add(cameraThread);
            cameraThread.start();
        }

        // Start LiDarService for each LiDar configuration
        for (LiDarWorkerTracker lidar : conf.getLidarConfigurations()) {
            Thread lidarThread = new Thread(() -> {
                new LiDarService(lidar, readyLatch).run();
            });
            serviceThreads.add(lidarThread);
            lidarThread.start();
        }

        // Start PoseService
        GPSIMU gps = new GPSIMU(conf.getPoseJsonFile());
        Thread poseThread = new Thread(() -> {
            new PoseService(gps, readyLatch).run();
        });
        serviceThreads.add(poseThread);
        poseThread.start();

        // Start FusionSlamService
        String basePath = new File(configFilePath).getParent();
        Thread fusionSlamService = new Thread(() -> {
            new FusionSlamService(FusionSlam.getInstance(), numOfServices, readyLatch, basePath).run();
        });
        serviceThreads.add(fusionSlamService);
        fusionSlamService.start();

        // Start TimeService after all other services are ready
        try {
            readyLatch.await();  // Wait for all services to signal they are ready
            Thread timeService = new Thread(new TimeService(conf.getTickTime(), conf.getDuration()));
            timeService.start();
            serviceThreads.add(timeService);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Main thread was interrupted while waiting for services to start.");
        }

        // Wait for all threads to complete
        for (Thread thread : serviceThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Main thread was interrupted while waiting for a service thread to finish.");
            }
        }
    }
}
