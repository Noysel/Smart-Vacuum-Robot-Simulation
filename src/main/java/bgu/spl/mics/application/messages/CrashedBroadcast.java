package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class CrashedBroadcast implements Broadcast {

    private String crashSender;
    private String description;

    public CrashedBroadcast(String crashSender, String description) {
        this.crashSender = crashSender;
        this.description = description;
    }

    public String getCrashSender() {
        return crashSender;
    }

    public String getDescription() {
        return description;
    }
}
