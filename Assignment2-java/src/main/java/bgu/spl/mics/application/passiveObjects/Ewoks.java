package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.MessageBusImpl;

import java.util.*;

import static java.util.Collections.sort;


/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private static class SingletonHolder {
        private static Ewoks instance = new Ewoks();
    }
    private Vector<Ewok> ewokVector; //vector because it is tread safe

    // private constructor
    private Ewoks() {
        this.ewokVector = new Vector<Ewok>(); // vector of ewoks
    }
    public static  Ewoks getInstance() {
        return Ewoks.SingletonHolder.instance;
    }

    //building the ewoks vec out of new ewok objects
    public synchronized void init(int numOfEwoks){
        if(ewokVector.size()!=0){
            throw new IllegalArgumentException("the vector should be empty in this moment");
        }
        for (int i = 0; i < numOfEwoks; i++) {
            this.ewokVector.add(new Ewok(i+1));
        }
    }
    /*
    1) the collection from the input is sorted
    2) the program finds where the object in the ewoks vector
    3) we acquire the ewok in this index
     */
    //acquire a list of ewoks
    public void acquire(ArrayList<Integer> requiredEwoks) {
        sort(requiredEwoks);
        for (int i = 0; i < requiredEwoks.size(); i++) {
            ewokVector.elementAt(requiredEwoks.get(i)-1).acquire();
        }
    }

    //realese a list of ewoks
    public void release(ArrayList<Integer> requiredEwoks) {
        for (int i = 0; i < requiredEwoks.size(); i++) {
            ewokVector.elementAt(requiredEwoks.get(i)-1).release();
        }
    }

}
