package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.StatisticalFolder;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    private long tickTime;
    private long duration;
    private StatisticalFolder statisticalFolder;

    public TimeService(int TickTime, int Duration) {
        super("TimeService");
        this.tickTime = TickTime;
        this.duration = Duration;
        this.statisticalFolder = StatisticalFolder.getInstance();
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        try {
        for (int tick = 1; tick <= duration; tick++) {
            Thread.sleep(tickTime);
            sendBroadcast(new TickBroadcast(tick));
            statisticalFolder.increaseSystemRunTime();
        }
        sendBroadcast(new TerminateBroadcast(getName()));  
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    } finally {
        sendBroadcast(new TerminateBroadcast(getName()));
        terminate(); 
    }
    }
}
