package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class CameraTest {

    private Camera camera;

    @BeforeEach
    void setUp() {
        // Mock data for Camera
        camera = new Camera(1, 5, "cameraKey", "mockPath");
        camera.initDefault("mockPath");
    }

    @Test
    void testIntervalWithMatchingTickTime() {
        StampedDetectedObjects obj = camera.interval(5);
        assertNotNull(obj);
        assertEquals(5, obj.getTime());
    }

    @Test
    void testIntervalWithNoMatchingTickTime() {
        StampedDetectedObjects obj = camera.interval(3);
        assertNull(obj);
    }

    @Test
    void testIntervalWithEmptyObjects() {
        camera.getDetectedObjectList().clear();
        StampedDetectedObjects obj = camera.interval(5);
        assertEquals(-2, obj.getTime());
    }
}