package com.example.lenovo.droidalarm;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Lenovo on 03-Sep-17.
 */

public class Alarm {

    //Calendar calendar;
    int hour;
    int minutes;
    String time;
    boolean state;
    int alarm_id;
    String label;
    String ringtone;
    String off_method;
    int shake_count;
    int tap_count;
    ArrayList<Integer> weekList;

    public Alarm(int hour,int minutes,String time, boolean state, int id, String label, String ring, String method, int count,int tCount, ArrayList<Integer> wList) {
        this.hour=hour;
        this.minutes=minutes;
        this.time = time;
        this.state = state;
        this.alarm_id=id;
        this.label=label;
        this.ringtone=ring;
        this.off_method=method;
        this.shake_count=count;
        this.tap_count=tCount;
        this.weekList=wList;
    }
}
