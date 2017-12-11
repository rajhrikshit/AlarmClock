package com.example.lenovo.droidalarm;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by Lenovo on 03-Sep-17.
 */

public class AlarmRow extends RecyclerView.ViewHolder{

    public interface OnItemClickListener{
        public void OnItemClick(int id);
        public void OnItemLongClick(int id);
        public void OnItemChecked(int id);
        public void OnItemUnchecked(int id);
        public void toggleOnOff(int id);
    }

    OnItemClickListener callback;

    GestureDetector gDetector;
    GestureDetector.OnGestureListener gListener=new GestureDetector.OnGestureListener(){

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            if(AlarmFragment.in_del_action_mode==false) {
                callback.OnItemClick(a.alarm_id);
            }else {
                if(!checkBox.isChecked()) {
                    checkBox.setChecked(true);
                    callback.OnItemChecked(a.alarm_id);
                }else if(checkBox.isChecked()){
                    checkBox.setChecked(false);
                    callback.OnItemUnchecked(a.alarm_id);
                }
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
            if(AlarmFragment.in_del_action_mode==false) {
                callback.OnItemLongClick(a.alarm_id);
            }
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }
    };

    View v;
    TextView vtextClock;
    Switch switchButton;
    Context context;
    CheckBox checkBox;
    Alarm a;

    public AlarmRow(View itemView,Context context,OnItemClickListener c) {
        super(itemView);
        this.v=itemView;
        gDetector=new GestureDetector(context,gListener);
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gDetector.onTouchEvent(motionEvent);
            }
        });
        try {
            callback=c;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        this.vtextClock = (TextView) itemView.findViewById(R.id.alarmtime);
        this.switchButton = (Switch) itemView.findViewById(R.id.onoff);
        this.checkBox=(CheckBox)itemView.findViewById(R.id.checkbox);
        this.context=context;
    }

    public void bindRow(final Alarm alarm){
        a=alarm;

        this.vtextClock.setText(alarm.time);
        if(AlarmFragment.in_del_action_mode==false){
            checkBox.setVisibility(View.GONE);
            switchButton.setVisibility(View.VISIBLE);
        }else{
            checkBox.setVisibility(View.VISIBLE);
            switchButton.setVisibility(View.GONE);
            checkBox.setChecked(false);
        }

        //listener for check box
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox.isChecked()) {
                    //checkBox.setChecked(true);
                    callback.OnItemChecked(a.alarm_id);
                }else if(!checkBox.isChecked()){
                    //checkBox.setChecked(false);
                    callback.OnItemUnchecked(a.alarm_id);
                }
            }
        });

        final boolean astate=alarm.state;
        final int mid=alarm.alarm_id;
        if(astate){
            switchButton.setChecked(true);
        }else{
            switchButton.setChecked(false);
        }
        switchButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {

                if(alarm.state){
                    switchButton.setChecked(false);
                    callback.toggleOnOff(mid);
                }else{
                    switchButton.setChecked(true);
                    callback.toggleOnOff(mid);
                }
            }
        });
    }
}
