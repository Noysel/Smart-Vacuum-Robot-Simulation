package bgu.spl.mics.application.objects;

/**
 * CloudPoint represents a specific point in a 3D space as detected by the LiDAR.
 * These points are used to generate a point cloud representing objects in the environment.
 */
public class CloudPoint {

    private double x;
    private double y;
    private double z;

    public CloudPoint(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 0.104;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }

    public void setX(double newX) {
        x = newX;
    }

    public void setY(double newY) {
        x = newY;
    }

    public String toString() {
        return "x:" + x + ", y:" + y;
    }
}
