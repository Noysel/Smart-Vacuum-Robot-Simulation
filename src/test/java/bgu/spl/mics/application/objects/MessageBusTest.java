package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.TimeUnit;

class MessageBusTest {

    private MessageBusImpl messageBus;
    private MicroService ms1;
    private MicroService ms2;

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();
        ms1 = new MicroService("Service1") {
            @Override
            protected void initialize() {}
        };
        ms2 = new MicroService("Service2") {
            @Override
            protected void initialize() {}
        };
    }

    @Test
    void testRegisterAndUnregister() {
        messageBus.register(ms1);
        assertThrows(IllegalStateException.class, () -> messageBus.awaitMessage(ms2));
        messageBus.unregister(ms1);
        assertThrows(IllegalStateException.class, () -> messageBus.awaitMessage(ms1));
    }

    @Test
    void testSendEvent() throws InterruptedException {
        messageBus.register(ms1);
        messageBus.register(ms2);
        messageBus.subscribeEvent(TestEvent.class, ms2);

        Future<String> future = messageBus.sendEvent(new TestEvent("TestMessage"));
        assertNotNull(future);

        Message msg = messageBus.awaitMessage(ms2);
        assertTrue(msg instanceof TestEvent);
    }

    @Test
    void testSendBroadcast() throws InterruptedException {
        messageBus.register(ms1);
        messageBus.register(ms2);
        messageBus.subscribeBroadcast(TestBroadcast.class, ms1);
        messageBus.subscribeBroadcast(TestBroadcast.class, ms2);

        messageBus.sendBroadcast(new TestBroadcast());

        Message msg1 = messageBus.awaitMessage(ms1);
        Message msg2 = messageBus.awaitMessage(ms2);

        assertTrue(msg1 instanceof TestBroadcast);
        assertTrue(msg2 instanceof TestBroadcast);
    }
}