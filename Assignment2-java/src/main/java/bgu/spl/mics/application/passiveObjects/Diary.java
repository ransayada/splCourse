package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.MicroService;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {
    private static class SingletonHolder{
        private static Diary instance = new Diary();
    }
    private AtomicInteger totalAttacks;
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long LeiaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long R2D2Terminate;
    private long LandoTerminate;


    private Diary() {
        totalAttacks = new AtomicInteger(0);
        HanSoloFinish = System.currentTimeMillis();
        C3POFinish = System.currentTimeMillis();
        R2D2Deactivate = System.currentTimeMillis();
        LeiaTerminate = System.currentTimeMillis();
        HanSoloTerminate = System.currentTimeMillis();
        C3POTerminate = System.currentTimeMillis();
        R2D2Terminate = System.currentTimeMillis();
        LandoTerminate = System.currentTimeMillis();
    }

    public static Diary getInstance() { // singleton instance checker
            return SingletonHolder.instance;
    }

    public int getTotalAttacks() {
        return totalAttacks.get();
    }

    public long getHanSoloFinish() {
        return HanSoloFinish;
    }

    public long getC3POFinish() {
        return C3POFinish;
    }

    public long getR2D2Deactivate() {
        return R2D2Deactivate;
    }

    public long getLeiaTerminate() {
        return LeiaTerminate;
    }

    public long getHanSoloTerminate() {
        return HanSoloTerminate;
    }

    public long getC3POTerminate() {
        return C3POTerminate;
    }

    public long getR2D2Terminate() {
        return R2D2Terminate;
    }

    public long getLandoTerminate() {
        return LandoTerminate;
    }

    //incrementTotalAttacks increase the number of attacks by one.
    public void incrementTotalAttacks() {
        this.totalAttacks.getAndIncrement();
    }

    public void setHanSoloFinish(long hanSoloFinish) {
        HanSoloFinish = hanSoloFinish;
    }

    public void setC3POFinish(long c3POFinish) {
        C3POFinish = c3POFinish;
    }

    public void setR2D2Deactivate(long r2D2Deactivate) {
        R2D2Deactivate = r2D2Deactivate;
    }

    public void setLeiaTerminate(long leiaTerminate) {
        LeiaTerminate = leiaTerminate;
    }

    public void setHanSoloTerminate(long hanSoloTerminate) {
        HanSoloTerminate = hanSoloTerminate;
    }

    public void setC3POTerminate(long c3POTerminate) {
        C3POTerminate = c3POTerminate;
    }

    public void setR2D2Terminate(long r2D2Terminate) {
        R2D2Terminate = r2D2Terminate;
    }

    public void setLandoTerminate(long landoTerminate) {
        LandoTerminate = landoTerminate;
    }
    public void resetNumberAttacks(){ totalAttacks = new AtomicInteger(0);//TODO:remove after tests
}
}

