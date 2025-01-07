package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;

class CameraTest {

    private Camera camera;

    @BeforeEach
    void setUp() {
        // Initialize the Camera object
        camera = new Camera(1, 5, "cameraKey", "mockPath");
        camera.initDefault("mockPath");

        // Create a dummy list of StampedDetectedObjects for testing
        List<DetectedObject> detectedObjects1 = Arrays.asList(
            new DetectedObject("Object1", "Type1"),
            new DetectedObject("Object2", "Type2")
        );
        List<DetectedObject> detectedObjects2 = Arrays.asList(
            new DetectedObject("Object3", "Type3"),
            new DetectedObject("Object4", "Type4")
        );

        // Populate the dummy allObj list in the Camera object
        List<StampedDetectedObjects> dummyAllObj = new LinkedList<>();
        dummyAllObj.add(new StampedDetectedObjects(5, detectedObjects1));
        dummyAllObj.add(new StampedDetectedObjects(10, detectedObjects2));

        // Set the dummy list as the camera's allObjects
        camera.setAllObj(dummyAllObj);
    }

    @Test
    void testIntervalWithMatchingTickTime() {
        // Test with a tick time that matches
        StampedDetectedObjects result = camera.interval(10);
        assertNotNull(result, "Interval should return a non-null result for matching tick time");
        assertEquals(5, result.getTime(), "The time of the detected objects should match the tick time");
        assertEquals(2, result.getDetectedObjects().size(), "Expected two detected objects for the matching tick time");
    }

    @Test
    void testIntervalWithNoMatchingTickTime() {
        // Test with a tick time that does not match any recorded time
        StampedDetectedObjects result = camera.interval(1);
        assertNull(result, "Interval should return null for a tick time with no matching detected objects");
    }
}