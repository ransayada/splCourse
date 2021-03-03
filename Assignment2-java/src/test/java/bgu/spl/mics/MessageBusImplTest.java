package bgu.spl.mics;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.services.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    private MessageBusImpl mb;
    private MicroService ms1;
    private MicroService ms2;
    private MicroService ms3;

    @BeforeEach
    public void SetUp() {
        mb = MessageBusImpl.getInstance();
        ms1 = new HanSoloMicroservice();
        mb.register(ms1);
        ms2 = new C3POMicroservice();
        mb.register(ms2);
        ms3 = new C3POMicroservice();
        mb.register(ms3);
    }

    @AfterEach
    public void tearDown() {
        mb.unregister(ms1);
        mb.unregister(ms2);
        mb.unregister(ms3);
    }

    /*Test Case (Test sendEvent and awaitMessage - same flow):
     *Assuming ms1 and ms2 are registered.
     * Subscribe ms1 to AttackEvents with empty callback, and ms2 to send an event of this type.
     */
    @Test
    public void testSendEvent() throws InterruptedException {
        AttackEvent e1 = new AttackEvent();
        mb.subscribeEvent(AttackEvent.class, ms1);
        ms2.sendEvent(e1);
        try{
            assertTrue(e1.equals(mb.awaitMessage(ms1)));
        } catch (InterruptedException inter) {
            fail();
        }
    }

    /* Identical Test Case to the above, this time for sendBroadcast().
     * Tests to microservices subscribed to the same broadcast. ms1 and ms3 to receive the message.
     */
    @Test
    public void testSendBroadcast() throws InterruptedException {
        VictoryBroadcast b1 = new VictoryBroadcast();
        mb.subscribeBroadcast(VictoryBroadcast.class, ms1);
        mb.subscribeBroadcast(VictoryBroadcast.class, ms3);
        ms2.sendBroadcast(b1);
        try {
            assertTrue(b1.equals(mb.awaitMessage(ms1)));
            assertTrue(b1.equals(mb.awaitMessage(ms3)));
        }catch (InterruptedException inter){
            fail();
        }
    }

    /*TestCase:
     * Assuming the microservice already pulled the Message e1 and has the matching future f1.
     * Complete event e1.
     */
    @Test
    public void testComplete()  throws InterruptedException {
        AttackEvent e1 = new AttackEvent();
        mb.subscribeEvent(AttackEvent.class, ms1);
        Future f1 = mb.sendEvent(e1);
        assertFalse(f1.isDone());
        try {
            mb.complete((AttackEvent)(mb.awaitMessage(ms1)), true);
        } catch (InterruptedException inter) {
            fail();
        }
        assertTrue(f1.isDone());
        assertEquals(true, f1.get());
    }

}