package bgu.spl.mics.application.objects;

public class Main {

    public static void main(String[] args) {
        Configuration conf = InputParser.parseConfiguration("example input\\configuration_file.json");
        for (Camera cm : conf.getCameras().getCamerasConfigurations()) {
            System.out.println(cm);
        }

    }

}
