package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    private CountDownLatch initializationCount = null;

    public HanSoloMicroservice() {
        super("Han");
    } //Empty Constructor for tests

    public void setInitializationCount(CountDownLatch initializationCount) {
        this.initializationCount = initializationCount;
    }

    /**
    *Han subscribe into an Attack event
     * 1) fetch this event
     * 2) acquire the necessary ewoks list
     * 3) sleeping until the end of the duration
     * 4) realising the ewoks list
     * 5)raise the total atacks counter by 1
     * 6)finish is event handling
     * Han subscribed to the victory broadcast like every m.s.
     * Han subscribed to the no more attack broadcast to know when to terminate
     **/
    @Override
    protected void initialize() {

        /*Please note that the duplicate code fragment for HanSolo and C3PO below (AttackEvent Callback) is due to the
        inability to complete the event from outside a MicroService instance.*/
        subscribeEvent(AttackEvent.class, (AttackEvent event) -> {
            List<Integer>  ew = event.getSerial();
            ArrayList<Integer> requiredEwoks = array(ew);
            long duration = event.getDuration();
            Ewoks.getInstance().acquire(requiredEwoks);
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {

            }
            Ewoks.getInstance().release(requiredEwoks);
            Diary.getInstance().incrementTotalAttacks();
            complete(event, true);
        });
        subscribeBroadcast(VictoryBroadcast.class, (VictoryBroadcast broad) -> {
            Timestamp time = new Timestamp(System.currentTimeMillis());
            Diary.getInstance().setHanSoloTerminate(time.getTime());
            terminate();

        });
        subscribeBroadcast(NoMoreAttacksBroadcast.class, (NoMoreAttacksBroadcast broad) -> {
            Timestamp time = new Timestamp(System.currentTimeMillis());
            Diary.getInstance().setHanSoloFinish(time.getTime());
            System.out.println("HanSoloT finish"); //TODO:remove prints

        });


        initializationCount.countDown(); //Signal he finished initializing
    }
    //making array list from a list
    private ArrayList<Integer> array(List<Integer> l) {
        ArrayList<Integer> arr = new ArrayList<>(l.size());
        arr.addAll(l);
        return arr;
    }
}

