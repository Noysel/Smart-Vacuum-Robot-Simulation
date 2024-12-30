package bgu.spl.mics.application.objects;
import java.util.LinkedList;
import java.util.List;
/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for
 * tracked objects.
 */
public class LiDarDataBase {

    private static class SingletonHolder {
        private static final LiDarDataBase instance = new LiDarDataBase();
    }
    private List<StampedCloudPoints> cloudPoints;

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */

     public LiDarDataBase() {
        this.cloudPoints = null;
     }

    public static LiDarDataBase getInstance(String filePath) {
        SingletonHolder.instance.init(filePath);
        return SingletonHolder.instance;
    }

    private void init(String filePath) {
        if (cloudPoints == null) {
            cloudPoints = LiDarDataParser.parseLidarData(filePath);
        }
    }

    public List<StampedCloudPoints> getStampedCloudPoints() {
        return cloudPoints;
    }
}
