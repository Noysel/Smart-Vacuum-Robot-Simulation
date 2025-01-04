package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.KillTimeEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StatisticalFolder;

/**
 * TimeService acts as the global timer for the system, broadcasting
 * TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    /**
     * Constructor for TimeService.
     *
     * @param TickTime The duration of each tick in milliseconds.
     * @param Duration The total number of ticks before the service terminates.
     */
    private long tickTime;
    private long duration;
    private long currentTick;
    private StatisticalFolder statisticalFolder;

    public TimeService(int TickTime, int Duration) {
        super("TimeService");
        this.tickTime = TickTime;
        this.duration = 30; ////////////////////////////////////////////
        this.statisticalFolder = StatisticalFolder.getInstance();
        this.currentTick = 1;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified
     * duration.
     */
    @Override
    protected void initialize() {

        subscribeEvent(KillTimeEvent.class, killTime -> {
            terminate();
        });
        
        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (currentTick <= duration) {
                try {
                    Thread.sleep(this.tickTime);
                    System.out.println("Tick: " + currentTick); //////////
                    sendBroadcast(new TickBroadcast(currentTick));
                    currentTick++;
                    statisticalFolder.increaseSystemRunTime();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            else {
                terminate();
            }
        });
        System.out.println("Tick: " + currentTick);
        sendBroadcast(new TickBroadcast(currentTick));
        currentTick++;
    }
}
