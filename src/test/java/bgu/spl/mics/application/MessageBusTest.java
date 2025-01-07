package bgu.spl.mics.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.TimeUnit;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import bgu.spl.mics.MessageBus;

class MessageBusTest {
/* 
    private MessageBusImpl messageBus;
    private CameraService cameraService;
    private LiDarService liDarService;
    private TimeService timeService;
    private Camera camera;
    private LiDarWorkerTracker liDarWorkerTracker;

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();
        camera = new Camera(1, 30, "Cam1", "/path");
        liDarWorkerTracker = new LiDarWorkerTracker(1, 30);
        cameraService = new CameraService(camera);
        liDarService = new LiDarService(liDarWorkerTracker);
        timeService = new TimeService(100, 10);  // Assuming TimeService takes tickTime and duration as parameters

        messageBus.register(cameraService);
        messageBus.register(liDarService);
        messageBus.register(timeService);
    }

    @Test
    void testRegisterAndUnregister() {
        assertTrue(messageBus.isRegistered(cameraService), "CameraService should be registered");
        assertTrue(messageBus.isRegistered(liDarService), "LiDarService should be registered");

        messageBus.unregister(cameraService);
        messageBus.unregister(liDarService);

        assertFalse(messageBus.isRegistered(cameraService), "CameraService should be unregistered");
        assertFalse(messageBus.isRegistered(liDarService), "LiDarService should be unregistered");
    }

    @Test
    void testSendDetectedObjectEvent() {
        messageBus.subscribeEvent(DetectedObjectEvent.class, liDarService);

        StampedDetectedObjects detectedObjects = new StampedDetectedObjects(1, Collections.emptyList());
        DetectedObjectEvent event = new DetectedObjectEvent(detectedObjects, "Cam1");
        Future<Boolean> future = messageBus.sendEvent(event);

        assertNotNull(future, "Future should not be null after sending an event");


        
        try {
            Message receivedEvent = messageBus.awaitMessage(liDarService);
            assertEquals(DetectedObjectEvent.class, receivedEvent.getClass(), "LiDarService should receive DetectedObjectEvent");
        } catch (InterruptedException e) {
            fail("LiDarService should not be interrupted while awaiting message");
        }
    }

    @Test
    void testSendBroadcast() {
        Broadcast testBroadcast = new Broadcast() {};
        messageBus.subscribeBroadcast(testBroadcast.getClass(), cameraService);
        messageBus.subscribeBroadcast(testBroadcast.getClass(), liDarService);

        messageBus.sendBroadcast(testBroadcast);

        try {
            Message receivedMsg1 = messageBus.awaitMessage(cameraService);
            Message receivedMsg2 = messageBus.awaitMessage(liDarService);
            assertTrue(receivedMsg1 instanceof Broadcast, "CameraService should receive a Broadcast");
            assertTrue(receivedMsg2 instanceof Broadcast, "LiDarService should receive a Broadcast");
        } catch (InterruptedException e) {
            fail("Neither service should be interrupted while waiting for a broadcast");
        }
    }
        */
}