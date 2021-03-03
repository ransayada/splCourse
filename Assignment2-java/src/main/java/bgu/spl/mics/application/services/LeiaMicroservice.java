package bgu.spl.mics.application.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.messages.*;


/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
    private Attack[] attacks;
    private CountDownLatch initializationCount = null;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
    }
    public void setInitializationCount(CountDownLatch initializationCount){
        this.initializationCount=initializationCount;
    }

    /**
     * BackGround: In our implementation, Leia is the commander of the crew, she manages all the actions, follow events,
     * and is the only one who send messages.
     * <p>
     * Leia's initialize flow will perform most actions as a way to organize them
     * and as she doesn't receive Messages from others.
     * The only message she'll subscribe to is the VictoryBroadcast in order to terminate with the others.
     * Initialize flow:
     * 1. Subscribe to VictoryBroadcast. The callback will log the time and terminate.
     * 2. Wait's until other finished initialization.
     * 2. Call manageAttacks() function to send and follow attacks
     * 3. call manageDeactivation() function to send and follow DeactivationEvent
     * 4. call manageBombDestroyer() function to send and follow BombDestroyerEvent
     * 5. send VictoryBroadcast to update the others.
     */
    protected void initialize() {
        subscribeBroadcast(VictoryBroadcast.class, (param) -> {
            Timestamp time = new Timestamp(System.currentTimeMillis());
            Diary.getInstance().setLeiaTerminate(time.getTime());
            terminate();
        });
        try {
            initializationCount.await(); //use CountDownLatch to make sure other finished initializing

        } catch (InterruptedException e) {
        }
        ;
        manageAttacks();
        System.out.println("Leia attack finish"); //TODO:remove prints

        manageDeactivation();
        System.out.println("Leia De finish"); //TODO:remove prints

        manageBombDestroyer();
        System.out.println("Leia bomb finish"); //TODO:remove prints

        sendBroadcast(new VictoryBroadcast());
        System.out.println("Leia send victory"); //TODO:remove prints


    }

    /**
     * Leia will use the function to send attackEvents and follow their future objects.
     * In order for Leia to signal the followers of attackEvents that they are done attacking,
     * Leia will use the NoMoreAttacksBroadcast.
     */
    private void manageAttacks() {
        ArrayList<Future> futuresToFollow = new ArrayList<>();
        //Send attackEvents
        for (Attack at : attacks) {
            AttackEvent e = new AttackEvent(at.getSerials(), at.getDuration());
            futuresToFollow.add(sendEvent(e));
        }
        //No more attacks to send. Send NoMoreAttacksBroadcast
        sendBroadcast(new NoMoreAttacksBroadcast());
        //Follow futures to make sure attacks are over.
        for (Future f : futuresToFollow) {
            while (!f.isDone()) { //In order not to cast (although the result is always boolean in our flow), we use isDone function.
                try {
                    f.get();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    /**
     * Leia will use the function to send DeactivationEvent and follow it's future object.
     */
    private void manageDeactivation() {
        Future f = sendEvent(new DeactivationEvent());
        while (!f.isDone()) { //In order not to cast (although the result is always boolean in our flow), we use isDone function.
            try {
                f.get();
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Leia will use the function to send BombDestroyerEvent and follow it's future object.
     */
    private void manageBombDestroyer() {
        Future f = sendEvent(new BombDestroyerEvent());
        while (!f.isDone()) { //In order not to cast (although the result is always boolean in our flow), we use isDone function.
            try {
                f.get();
            } catch (InterruptedException e) {
            }
        }
    }
}
