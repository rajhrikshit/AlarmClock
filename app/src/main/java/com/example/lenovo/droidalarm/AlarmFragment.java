package com.example.lenovo.droidalarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Lenovo on 02-Sep-17.
 */

public class AlarmFragment extends Fragment {

    //Instance Variables
    public static final int REQUEST_CODE=1;
    public static final String ALARM_ID="AlarmId";
    public static final String ALARM_HOUR="AlarmHour";
    public static final String ALARM_MIN="AlarmMinutes";
    public static final String ALARM_STATE="alarm_state";
    public static final String ALARM_TIME="alarm_time";
    public static final String ALARM_LABEL="alarm_label";
    public static final String RINGTONE="ringtone";
    public static final String OFF_METHOD="off_method";
    public static final String SHAKE_COUNT="count";
    public static final String WEEKDAYS="weekdays";
    public static final String TAP_COUNT="tCount";
    public static final String SNOOZE_TIME="snoozeTime";
    public static final String MATH_DIFF="mathD";
    public static final String SHAKE_DIFF="shakeD";

    public static boolean in_del_action_mode=false;
    public static ArrayList<Alarm> deleteList=new ArrayList<>();
    FloatingActionButton mfab;
    public static AlarmManager alarmManager;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager  mLayoutManager;
    private RecyclerAdapter mRecyclerAdapter;
    private int counter=0;
    public static int snooze_time=5;
    public static int math_diff=0;
    public static int shake_force=0;
    private View v;

    DBAdapter dbAdapter;


    AlarmRow.OnItemClickListener callback=new AlarmRow.OnItemClickListener() {
        @Override
        public void OnItemClick(int id) {
            Intent intent=new Intent(getActivity(),MainActivity.class);
            intent.putExtra(MainActivity.EXISTING_CALL,id);
            startActivityForResult(intent,REQUEST_CODE);
        }

        @Override
        public void OnItemLongClick(int id) {
            ParentActivity.toolbar.getMenu().clear();
            ParentActivity.toolbar.inflateMenu(R.menu.menu_action_delete);
            in_del_action_mode = true;
            mRecyclerAdapter.notifyDataSetChanged();
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        @Override
        public void OnItemChecked(int id){

            for(Alarm i:RecyclerAdapter.alarmArrayList){
                if(i.alarm_id==id){
                    deleteList.add(i);
                }
            }
            counter++;
        }

        @Override
        public void OnItemUnchecked(int id) {

            for(Alarm i:RecyclerAdapter.alarmArrayList){
                if(i.alarm_id==id){
                    deleteList.remove(i);
                }
            }
            counter--;
        }

        @Override
        public void toggleOnOff(int id){

            if(pendingExists(id)){
                switchOff(id);
            }
            else{
                switchOn(id);
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.activity_base,container,false);
        //Setting up the recycler view
        mRecyclerView=(RecyclerView)v.findViewById(R.id.recview);
        mfab=(FloatingActionButton)v.findViewById(R.id.fabbt);
        this.v=v;
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initialising the database
        dbAdapter=new DBAdapter(getActivity());
        //Opening the database
        dbAdapter.open();

        int last=dbAdapter.getLastId();
        MainActivity.alarm_id=last+1;

        mRecyclerAdapter=new RecyclerAdapter(getActivity(),callback);
        mLayoutManager=new LinearLayoutManager(getActivity());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation()));//Lines in between rows
        if(mRecyclerView!=null) {
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mRecyclerAdapter);
        }

        alarmManager=(AlarmManager)getActivity().getSystemService(getActivity().ALARM_SERVICE);

        //Set the previous data
        setData();

        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(),MainActivity.class);
                startActivityForResult(i,REQUEST_CODE);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                //Sending the Pending Intent For The Alarm
                int count = data.getExtras().getInt(SHAKE_COUNT);
                int tCount = data.getExtras().getInt(TAP_COUNT);
                int aid = data.getExtras().getInt(ALARM_ID);
                int hour = data.getExtras().getInt(ALARM_HOUR);
                int min = data.getExtras().getInt(ALARM_MIN);
                boolean astate = data.getExtras().getBoolean(ALARM_STATE);
                String time = data.getExtras().getString(ALARM_TIME);
                String aLabel = data.getExtras().getString(ALARM_LABEL);
                String aRing = data.getExtras().getString(RINGTONE);
                String method = data.getExtras().getString(OFF_METHOD);
                boolean exists = data.getExtras().getBoolean(MainActivity.EXISTING_CALL);
                ArrayList<Integer> wList = data.getIntegerArrayListExtra(WEEKDAYS);

                Intent call = new Intent(getActivity(), AlarmReceiver.class);
                //Adding the Alarm State to the Intent
                call.putExtra(ALARM_ID, aid);

                if (wList.size() != 0) {

                    Calendar calendar = Calendar.getInstance();

                    int a, b, c, d, e, f, g;
                    a = b = c = d = e = f = g = 0;

                    for (int i : wList) {
                        calendar.set(Calendar.DAY_OF_WEEK, i);
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, min);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);

                        long currentTime = Calendar.getInstance().getTimeInMillis();
                        long alarmTime = calendar.getTimeInMillis();

                        if (currentTime >= alarmTime) {
                            alarmTime += 604800000;
                        }
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), aid * 10 + i, call,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime,
                                pendingIntent);

                        if (i == 1)
                            a = 1;
                        else if (i == 2)
                            b = 1;
                        else if (i == 3)
                            c = 1;
                        else if (i == 4)
                            d = 1;
                        else if (i == 5)
                            e = 1;
                        else if (i == 6)
                            f = 1;
                        else if (i == 7)
                            g = 1;
                    }

                    if (exists == false) {
                        dbAdapter.insertRow(aid, aLabel, hour, min, astate, aRing, method, count, tCount,
                                a, b, c, d, e, f, g,snooze_time,math_diff,shake_force);
                        //Inform the Adapter about the new Alarm
                        mRecyclerAdapter.newAlarmInserted(hour, min, time, astate, aid, aLabel, aRing, method, count, tCount, wList);
                    } else {
                        dbAdapter.updateRow((long) aid, aid, aLabel, hour, min,
                                astate, aRing, method, count,tCount, a, b, c, d, e, f, g,snooze_time,math_diff,shake_force);
                        //Inform the Adapter about the new Alarm
                        mRecyclerAdapter.updateAlarm(hour, min, time, astate, aid, aLabel, aRing, method, count, tCount, wList);
                    }
                } else {
                    //No repeat days selected

                    Calendar calendar = Calendar.getInstance();

                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, min);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    long diff = Calendar.getInstance().getTimeInMillis() - calendar.getTimeInMillis();

                    if (diff >= 0) {
                        calendar.add(Calendar.DAY_OF_WEEK, 1);
                    }

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), aid, call,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            pendingIntent);

                    if (exists == false) {
                        dbAdapter.insertRow(aid, aLabel, hour, min, astate, aRing, method, count, tCount,
                                0, 0, 0, 0, 0, 0, 0,snooze_time,math_diff,shake_force);
                        //Inform the Adapter about the new Alarm
                        mRecyclerAdapter.newAlarmInserted(hour, min, time, astate, aid, aLabel, aRing, method, count, tCount, wList);
                    } else {
                        dbAdapter.updateRow((long) aid, aid, aLabel, hour, min,
                                astate, aRing, method, count, tCount, 0, 0, 0, 0, 0, 0, 0,snooze_time,math_diff,shake_force);
                        //Inform the Adapter about the new Alarm
                        mRecyclerAdapter.updateAlarm(hour, min, time, astate, aid, aLabel, aRing, method, count, tCount, wList);
                    }
                }
            }

        }
    }

    public void switchOff(int id){
        Intent i=new Intent(getActivity(),AlarmReceiver.class);
        i.putExtra(ALARM_ID,id);

        Log.e("Switched","OFF");
        for(Alarm al:mRecyclerAdapter.alarmArrayList){
            if(al.alarm_id==id) {
                al.state = false;

                int a, b, c, d, e, f, g;
                a = b = c = d = e = f = g = 0;

                if (al.weekList.size() != 0) {
                    for (int k : al.weekList) {
                        if (k == 1)
                            a = 1;
                        else if (k == 2)
                            b = 1;
                        else if (k == 3)
                            c = 1;
                        else if (k == 4)
                            d = 1;
                        else if (k == 5)
                            e = 1;
                        else if (k == 6)
                            f = 1;
                        else if (k == 7)
                            g = 1;

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), id * 10 + k, i, PendingIntent.FLAG_UPDATE_CURRENT);
                        pendingIntent.cancel();
                        alarmManager.cancel(pendingIntent);
                    }

                    dbAdapter.updateRow((long) id, al.alarm_id, al.label, al.hour, al.minutes,
                            al.state, al.ringtone, al.off_method, al.shake_count, al.tap_count,
                            a, b, c, d, e, f, g,snooze_time,math_diff,shake_force);
                }else{
                    //No repeat
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),id,i,PendingIntent.FLAG_UPDATE_CURRENT);
                    pendingIntent.cancel();
                    alarmManager.cancel(pendingIntent);
                    dbAdapter.updateRow((long) id, al.alarm_id, al.label, al.hour, al.minutes,
                            al.state, al.ringtone, al.off_method, al.shake_count, al.tap_count,0,0,0,0,0,0,0,snooze_time,math_diff,shake_force);

                }
                break;
            }
        }

    }

    public void switchOn(int id){
        Intent i=new Intent(getActivity(),AlarmReceiver.class);
        i.putExtra(ALARM_ID,id);

        Log.e("Switched","ON");
        Alarm al=null;
        for(Alarm aa:mRecyclerAdapter.alarmArrayList){
            if(aa.alarm_id==id) {
                al = aa;
                al.state = true;

                int a, b, c, d, e, f, g;
                a = b = c = d = e = f = g = 0;

                if (al.weekList.size() != 0) {
                    for (int k : al.weekList) {
                        if (k == 1)
                            a = 1;
                        else if (k == 2)
                            b = 1;
                        else if (k == 3)
                            c = 1;
                        else if (k == 4)
                            d = 1;
                        else if (k == 5)
                            e = 1;
                        else if (k == 6)
                            f = 1;
                        else if (k == 7)
                            g = 1;

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), id * 10 + k, i, PendingIntent.FLAG_UPDATE_CURRENT);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_WEEK, k);
                        calendar.set(Calendar.HOUR_OF_DAY, al.hour);
                        calendar.set(Calendar.MINUTE, al.minutes);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);

                        long currentTime = Calendar.getInstance().getTimeInMillis();
                        long alarmTime = calendar.getTimeInMillis();

                        if (currentTime >= alarmTime) {
                            alarmTime += 604800000;
                        }

                        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime,
                                pendingIntent);
                    }

                    dbAdapter.updateRow((long) id, al.alarm_id, al.label, al.hour, al.minutes,
                            al.state, al.ringtone, al.off_method, al.shake_count, al.tap_count, a,
                            b, c, d, e, f, g,snooze_time,math_diff,shake_force);
                }else{
                    //No repeat
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),id,i,PendingIntent.FLAG_UPDATE_CURRENT);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, al.hour);
                    calendar.set(Calendar.MINUTE, al.minutes);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    long diff=Calendar.getInstance().getTimeInMillis()-calendar.getTimeInMillis();

                    if(diff >= 0){
                        calendar.add(Calendar.DAY_OF_WEEK,1);
                    }
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            pendingIntent);

                    dbAdapter.updateRow((long) id, al.alarm_id, al.label, al.hour, al.minutes,
                            al.state, al.ringtone, al.off_method, al.shake_count, al.tap_count,0,0,0,
                            0,0,0,0,snooze_time,math_diff,shake_force);
                }
                break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onDestroy() {
        dbAdapter.close();
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setData(){

        mRecyclerAdapter.alarmArrayList.clear();
        Cursor cr=dbAdapter.getAllRows();
        if(cr!=null && cr.getCount()>0)
        {
            if(cr.moveToFirst()) {
                do {

                    int a, b, c, d, e, f, g;
                    a = b = c = d = e = f = g = 0;
                    int id = cr.getInt(dbAdapter.COL_ALARMID);
                    String label = cr.getString(dbAdapter.COL_LABEL);
                    int hour = cr.getInt(dbAdapter.COL_HOUR);
                    int min = cr.getInt(dbAdapter.COL_MINUTES);
                    int st = cr.getInt(dbAdapter.COL_STATE);
                    boolean state;
                    if (st == 1) {
                        state = true;
                    } else {
                        state = false;
                    }
                    String ring = cr.getString(dbAdapter.COL_RINGTONE);
                    String meth = cr.getString(dbAdapter.COL_OFFMETHOD);
                    int shcnt = cr.getInt(dbAdapter.COL_SHAKECOUNT);
                    int tCount = cr.getInt(dbAdapter.COL_TAPCOUNT);
                    if (!cr.isNull(dbAdapter.COL_SUNDAY))
                        a = cr.getInt(dbAdapter.COL_SUNDAY);
                    if (!cr.isNull(dbAdapter.COL_MONDAY))
                        b = cr.getInt(dbAdapter.COL_MONDAY);
                    if (!cr.isNull(dbAdapter.COL_TUESDAY))
                        c = cr.getInt(dbAdapter.COL_TUESDAY);
                    if (!cr.isNull(dbAdapter.COL_WEDNESDAY))
                        d = cr.getInt(dbAdapter.COL_WEDNESDAY);
                    if (!cr.isNull(dbAdapter.COL_THURSDAY))
                        e = cr.getInt(dbAdapter.COL_THURSDAY);
                    if (!cr.isNull(dbAdapter.COL_FRIDAY))
                        f = cr.getInt(dbAdapter.COL_FRIDAY);
                    if (!cr.isNull(dbAdapter.COL_SATURDAY))
                        g = cr.getInt(dbAdapter.COL_SATURDAY);

                    snooze_time=cr.getInt(dbAdapter.COL_SNOOZE);
                    math_diff=cr.getInt(dbAdapter.COL_MATH);
                    shake_force=cr.getInt(dbAdapter.COL_FORCE);

                    ArrayList<Integer> wList = new ArrayList<>();

                    if (a == 1) {
                        wList.add(1);
                    }
                    if (b == 1) {
                        wList.add(2);
                    }
                    if (c == 1) {
                        wList.add(3);
                    }
                    if (d == 1) {
                        wList.add(4);
                    }
                    if (e == 1) {
                        wList.add(5);
                    }
                    if (f == 1) {
                        wList.add(6);
                    }
                    if (g == 1) {
                        wList.add(7);
                    }

                    String time = "";

                    if (min < 10) {
                        time += "0" + String.valueOf(min);
                    } else {
                        time += String.valueOf(min);
                    }

                    if (hour > 12) {
                        time = String.valueOf(hour - 12) + ":" + time + " pm";
                    } else {
                        if (hour == 0) {
                            time = "12:" + time + " am";
                        } else {
                            time = String.valueOf(hour) + ":" + time + " am";
                        }
                    }

                    //Setting in the RecyclerView
                    Log.e("New Alarm","Inserted");
                    mRecyclerAdapter.newAlarmInserted(hour, min, time, state, id, label, ring, meth, shcnt,tCount, wList);
                    if (state) {
                        Intent i = new Intent(getActivity(), AlarmReceiver.class);
                        i.putExtra(ALARM_ID, id);
                        if (wList.size() != 0) {
                            for (int k : wList) {

                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), id * 10 + k, i, PendingIntent.FLAG_UPDATE_CURRENT);
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.DAY_OF_WEEK, k);
                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, min);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);

                                long currentTime = Calendar.getInstance().getTimeInMillis();
                                long alarmTime = calendar.getTimeInMillis();

                                if (currentTime >= alarmTime) {
                                    alarmTime += 604800000;
                                }

                                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime,
                                        pendingIntent);
                            }
                        }else{
                            //No repeat
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),id,i,PendingIntent.FLAG_UPDATE_CURRENT);
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, min);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);

                            long diff=Calendar.getInstance().getTimeInMillis()-calendar.getTimeInMillis();

                            if(diff >= 0){
                                calendar.add(Calendar.DAY_OF_WEEK,1);
                            }

                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                    pendingIntent);
                        }
                    }
                }while (cr.moveToNext()) ;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.item_delete){
            for(Alarm i:deleteList){
                if(pendingExists(i.alarm_id)) {
                    switchOff(i.alarm_id);
                }
                dbAdapter.deleteRow(i.alarm_id);
                RecyclerAdapter.alarmArrayList.remove(i);
            }
            ParentActivity.toolbar.getMenu().clear();
            mRecyclerAdapter.notifyDataSetChanged();
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            in_del_action_mode=false;
            counter=0;
            ParentActivity.toolbar.inflateMenu(R.menu.menu_action_main);
            mRecyclerAdapter.notifyDataSetChanged();
        }else if(item.getItemId()==android.R.id.home){
            back();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean backPressed() {

        if(in_del_action_mode){
            back();
            return true;
        }else {
            in_del_action_mode=false;
            return false;
        }
    }

    public void back(){
        ParentActivity.toolbar.getMenu().clear();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        in_del_action_mode=false;
        counter=0;
        ParentActivity.toolbar.getMenu().clear();
        ParentActivity.toolbar.inflateMenu(R.menu.menu_action_main);
        mRecyclerAdapter.notifyDataSetChanged();
    }

    public boolean pendingExists(int id){
        Intent i=new Intent(getActivity(),AlarmReceiver.class);
        i.putExtra(ALARM_ID,id);

        for(Alarm aa:RecyclerAdapter.alarmArrayList){
            if(aa.alarm_id==id) {
                if (aa.weekList.size() != 0) {
                    for (int k : aa.weekList) {
                        if (PendingIntent.getBroadcast(getActivity(), id * 10 + k, i, PendingIntent.FLAG_NO_CREATE) != null) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }else{
                    //No Repeat
                    if (PendingIntent.getBroadcast(getActivity(),id,i,PendingIntent.FLAG_NO_CREATE) != null) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }



    @Override
    public void onPause() {
        super.onPause();
    }

}