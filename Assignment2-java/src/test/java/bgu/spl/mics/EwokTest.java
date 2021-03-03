package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Ewok;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class EwokTest {
    private Ewok ewok;

    @BeforeEach
    public void setUp() throws Exception {
        ewok = new Ewok(1);
    }
    /*Test flow:
    *make sure that the ewok is avilable
    */
    @Test
    public void testGetAvailable() {
        assertTrue(ewok.getAvailable());
    }
    /*Test flow:
    *make sure that the ewok is avilable to acquire
    *acquire the ewok and then make sure it cant be acquire againe
    */
    @Test
    public void testAcquire() {
        assertTrue(ewok.getAvailable());
        ewok.acquire();
        assertFalse(ewok.getAvailable());
    }
    /*Test flow:
    *make sure that the ewok is not avilable to acquire
    *releasing the ewok and then make sure it can be acquire 
    */
    @Test
    public void testRelease() {
        ewok.acquire();
        assertFalse(ewok.getAvailable());
        ewok.release();
        assertTrue(ewok.getAvailable());
    }
}
