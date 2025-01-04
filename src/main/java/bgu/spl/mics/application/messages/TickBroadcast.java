package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private long time;
    public TickBroadcast(long time) {
        this.time = time;
    }
    public long getTime() {
        return time;
    }

}
