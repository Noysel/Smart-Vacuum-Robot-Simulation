package bgu.spl.mics.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.TimeUnit;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.services.*;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.Event;
import bgu.spl.mics.Message;
import bgu.spl.mics.Future;

class MessageBusImplTest {

    private MessageBusImpl messageBus;
    private MicroService microService1;
    private MicroService microService2;

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();
        microService1 = new MicroService("MicroService1") {
            @Override
            protected void initialize() {}
        };
        microService2 = new MicroService("MicroService2") {
            @Override
            protected void initialize() {}
        };
        messageBus.register(microService1);
        messageBus.register(microService2);
    }

    @Test
    void testRegister() {
        assertTrue(messageBus.isRegistered(microService1), "MicroService1 should be registered");
        assertTrue(messageBus.isRegistered(microService2), "MicroService2 should be registered");
    }

    @Test
    void testSubscribeEvent() {
        class TestEvent implements Event<String> {}

        messageBus.subscribeEvent(TestEvent.class, microService1);

        TestEvent event = new TestEvent();
        messageBus.sendEvent(event);

        try {
            Message receivedMessage = messageBus.awaitMessage(microService1);
            assertNotNull(receivedMessage, "MicroService1 should receive the TestEvent");
            assertTrue(receivedMessage instanceof TestEvent, "Received message should be an instance of TestEvent");
        } catch (InterruptedException e) {
            fail("MicroService1 should not be interrupted while awaiting a message");
        }
    }

    @Test
    void testSendEvent() {
        class TestEvent implements Event<String> {}

        messageBus.subscribeEvent(TestEvent.class, microService1);

        TestEvent event = new TestEvent();
        Future<String> future = messageBus.sendEvent(event);

        assertNotNull(future, "Future should not be null for a sent event");

        // Simulate handling the event
        try {
            Message receivedMessage = messageBus.awaitMessage(microService1);
            assertTrue(receivedMessage instanceof TestEvent, "Received message should be a TestEvent");
            messageBus.complete((TestEvent) receivedMessage, "Success");
            assertEquals("Success", future.get(1, TimeUnit.SECONDS), "Future should resolve to the result 'Success'");
        } catch (InterruptedException e) {
            fail("MicroService1 should not be interrupted while awaiting a message");
        }
    }
}