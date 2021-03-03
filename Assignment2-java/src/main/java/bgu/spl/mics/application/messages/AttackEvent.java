package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.List;

public class AttackEvent implements Event<Boolean> {

    private List<Integer> serials;
    private int duration;

    public AttackEvent(){} //Empty constructor for tests
    public AttackEvent(List<Integer> ser, int dur) {
        serials = ser;
        duration = dur;
    }
    public int getDuration(){return duration;}
    public List<Integer> getSerial(){return serials;}
}
