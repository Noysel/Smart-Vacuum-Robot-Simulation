package bgu.spl.mics.application.objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Arrays;
import java.util.LinkedList;

class FusionSlamTest {

    private FusionSlam fusionSlam;

    @BeforeEach
    void setUp() {
        fusionSlam = FusionSlam.getInstance();
        fusionSlam.clearLandmarks();  // Ensure FusionSlam is reset before each test
    }

    @Test
    void testInsertValidLandmark() {
        LandMark validLandmark = new LandMark("L1", "Building", Arrays.asList(new CloudPoint(0, 0)));
        assertTrue(fusionSlam.insertLandMark(validLandmark), "Inserting a valid landmark should return true");
        assertEquals(1, fusionSlam.getLandmarks().size(), "There should be one landmark after insertion");
        assertEquals(validLandmark, fusionSlam.getLandmarks().get(0), "The inserted landmark should be the same as the one added");
    }

    @Test
    void testInsertDuplicateLandmark() {
        LandMark landmark = new LandMark("L1", "Building", Arrays.asList(new CloudPoint(0, 0)));
        fusionSlam.insertLandMark(landmark);
        assertFalse(fusionSlam.insertLandMark(landmark), "Duplicate insertion should fail");
        assertEquals(1, fusionSlam.getLandmarks().size(), "There should still be only one landmark after attempting duplicate insertion");
    }

    @Test
    void testUpdateLandmarkCoordinates() {
        LandMark landmark = new LandMark("L1", "Building", new LinkedList<>(Arrays.asList(new CloudPoint(0, 0))));
        fusionSlam.insertLandMark(landmark);

        // Assume updateLandMark accepts a LandMark object and updates it in the system
        landmark.getCoordinates().add(new CloudPoint(1, 1));  // Update existing landmark by adding new coordinates
        assertTrue(fusionSlam.updateLandMark(landmark), "Updating landmark should succeed");
        assertEquals(2, fusionSlam.getLandmarks().get(0).getCoordinates().size(), "Landmark should have updated coordinates");
    }
}
