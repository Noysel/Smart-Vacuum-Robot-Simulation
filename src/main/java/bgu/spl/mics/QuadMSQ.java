package bgu.spl.mics;

import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

public class QuadMSQ<T> {

    private MicroService microService;
    private Queue<Message> messageQ;
    private List<? extends Event<T>> eventSub;
    private List<Class<? extends Broadcast>> broadcastSub;

    public QuadMSQ (MicroService microService) {
        this.microService = microService;
        this.messageQ = new LinkedList<>();
        this.eventSub = new ArrayList<>();
        this.broadcastSub = new ArrayList<>();
    }
    public MicroService getMicroService() {
        return this.microService;
    }

    public Queue<Message> getMessageQueue() {
        return this.messageQ;
    }

    public List<? extends Event<T>> getEventSub() {
        return this.eventSub;
    }

    public List<Class<? extends Broadcast>> getBroadcastSub() {
        return this.broadcastSub;
    }
}
