package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {
    int serialNumber;
    boolean available;

    public Ewok(int serialNumber) {
        if (serialNumber < 0) {
            throw new IllegalArgumentException("index of ewok must be non-negative int");
        }
        this.serialNumber = serialNumber;
        this.available = true;
    }

    public boolean getAvailable() {
        synchronized (this) {
            return available;
        }
    }

    /**
     * Acquires an Ewok
     */
    public synchronized void acquire() {
        while (!available) {
            try {
                this.wait();
            } catch (InterruptedException e) {
            }
        }
        this.available = false;
    }

    /**
     * release an Ewok
     */
    public synchronized void release() {
        if (available) {
            throw new IllegalArgumentException("you can't release an ewok that hasnt been acquired yet");
        } else {
            this.available = true;

            this.notifyAll();
        }
    }
}

