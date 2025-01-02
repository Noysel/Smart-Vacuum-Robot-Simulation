package bgu.spl.mics.application.objects;

public class Main {

    public static void main(String[] args) {
        Configuration conf = InputParser.parseConfiguration("example_input_2\\configuration_file.json");
        for (Camera cm : conf.getCameras().getCamerasConfiguration()) {
            System.out.println(cm);
        }
        for (LiDarWorkerTracker lidar : conf.getLiDarWorkers().getLidarConfigurations()) {
            System.out.println(lidar); //
        }

    }

}
