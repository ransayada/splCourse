package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;

import bgu.spl.mics.application.messages.*;
import java.sql.Timestamp;
import java.util.concurrent.CountDownLatch;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {

    private long duration;
    private CountDownLatch initializationCount = null;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration = duration;
    }
    public void setInitializationCount(CountDownLatch initializationCount){
        this.initializationCount=initializationCount;
    }

    @Override
    /**
     * subscribe into Deactivation Even and then sleep for is duration until finish, than log to Diary, complete is actions
     * subscribe to Victory Broadcast in order to know when to terminate
     * signal is fellows that it finish his event
     */
    protected void initialize() {
        subscribeEvent(DeactivationEvent.class, (DeactivationEvent event) -> {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
            }
            Timestamp time = new Timestamp(System.currentTimeMillis());
            Diary.getInstance().setR2D2Deactivate(time.getTime());

            complete(event, true);
        });
        subscribeBroadcast(VictoryBroadcast.class, (VictoryBroadcast broad) -> {
            Timestamp time = new Timestamp(System.currentTimeMillis());
            Diary.getInstance().setR2D2Terminate(time.getTime());
            terminate();

        });
        initializationCount.countDown(); //Signal he finished initializing
    }
}
