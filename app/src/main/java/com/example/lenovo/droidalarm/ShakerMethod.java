package com.example.lenovo.droidalarm;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Lenovo on 28-Sep-17.
 */

public class ShakerMethod extends Fragment{

    private Button btCount;
    private int counter=0;
    private int directioncounter=0;
    private Alarm a;
    private AlarmOff shut;
    private float lastX=0;
    private float lastY=0;
    private float lastZ=0;


    GestureDetector gestureDetector;
    GestureDetector.OnGestureListener gestureListener=new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            float deltaY=Math.abs(motionEvent.getY()-motionEvent1.getY());
            if((motionEvent1.getY() < motionEvent.getY()) && deltaY>250) {
                try {
                    shut = (AlarmOff) getActivity();
                    shut.onSnoozeClicked(a.alarm_id);
                    Toast.makeText(getActivity().getApplicationContext(),"SNOOZED",Toast.LENGTH_SHORT).show();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    };


            SensorManager sensorManager;

    private static  int MIN_DIRECTION_CHANGE=11; // Minimum number of direction changes to be counted as a proper shake
    private static  int MIN_FORCE=9;

    private float currentAcc;
    private float prevAcc;
    private float shake;

    SensorEventListener sensorEventListener=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            float x=sensorEvent.values[0];
            float y=sensorEvent.values[1];
            float z=sensorEvent.values[2];

            //Calculate Movement
            float change_in_position=Math.abs(x+y+z - (lastX+lastY+lastZ));

            if(change_in_position>MIN_FORCE){

                directioncounter++;

                lastX=x;
                lastY=y;
                lastZ=z;

                if(directioncounter>=MIN_DIRECTION_CHANGE){
                    counter++;
                    btCount.setText(String.valueOf(counter));
                    final Toast toast=Toast.makeText(getActivity(),String.valueOf(counter),Toast.LENGTH_SHORT);
                    toast.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 500);
                    resetShakeParameters();
                    if(counter==a.shake_count){
                        try{
                            shut=(AlarmOff)getActivity();
                            shut.onDismissClicked(a.alarm_id);
                        }catch (ClassCastException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.shake_off,container,false);
        btCount=(Button)v.findViewById(R.id.showcount);

        gestureDetector=new GestureDetector(getActivity().getApplicationContext(),gestureListener);
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args=getArguments();
        int id=args.getInt(AlarmFragment.ALARM_ID);
        for (Alarm i:RecyclerAdapter.alarmArrayList){
            if(i.alarm_id==id){
                a=i;
                break;
            }
        }

        switch (AlarmFragment.shake_force){
            case 0:MIN_FORCE=9;
                MIN_DIRECTION_CHANGE=11;
                break;
            case 1:MIN_FORCE=9;
                MIN_DIRECTION_CHANGE=12;
                break;
            case 2:MIN_FORCE=10;
                MIN_DIRECTION_CHANGE=12;
                break;
            case 3:MIN_FORCE=11;
                MIN_DIRECTION_CHANGE=13;
                break;

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        sensorManager=(SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);

        currentAcc=SensorManager.GRAVITY_EARTH;
        prevAcc=SensorManager.GRAVITY_EARTH;
        shake=0.00f;
    }

    @Override
    public void onPause() {
        sensorManager.unregisterListener(sensorEventListener);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }



    private void resetShakeParameters(){
        lastX=0;
        lastY=0;
        lastZ=0;
        directioncounter=0;
    }

}