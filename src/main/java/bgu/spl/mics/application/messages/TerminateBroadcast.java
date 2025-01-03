package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TerminateBroadcast implements Broadcast {

    private String sender;
    public TerminateBroadcast(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }
}
