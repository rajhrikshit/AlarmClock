package com.example.lenovo.droidalarm;

import android.app.Fragment;
import android.graphics.Color;
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

import java.util.Random;

/**
 * Created by Lenovo on 12-Oct-17.
 */

public class TapMethod extends Fragment {

    private int c=0;
    private boolean next=false;
    private int rNum=0;
    private final Button bt[]=new Button[9];
    Alarm a;


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

    AlarmOff shut;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.tap_off,container,false);

        bt[0]=(Button)v.findViewById(R.id.bt1);
        bt[1]=(Button)v.findViewById(R.id.bt2);
        bt[2]=(Button)v.findViewById(R.id.bt3);
        bt[3]=(Button)v.findViewById(R.id.bt4);
        bt[4]=(Button)v.findViewById(R.id.bt5);
        bt[5]=(Button)v.findViewById(R.id.bt6);
        bt[6]=(Button)v.findViewById(R.id.bt7);
        bt[7]=(Button)v.findViewById(R.id.bt8);
        bt[8]=(Button)v.findViewById(R.id.bt9);

        try{
            shut=(AlarmOff)getActivity();
        }catch (Exception e){
            e.printStackTrace();
        }

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

        int id=getArguments().getInt(AlarmFragment.ALARM_ID);
        for(Alarm i:RecyclerAdapter.alarmArrayList){
            if(i.alarm_id==id){
                a=i;
                break;
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        Random rn=new Random();

        for(int i=0;i<9;++i){
            bt[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (c < a.tap_count-1) {
                        for (int j = 0; j < 9; ++j) {
                            if (bt[j].isPressed()) {
                                if (j == rNum) {
                                    c++;
                                    final Toast toast = Toast.makeText(getActivity(), String.valueOf(c), Toast.LENGTH_SHORT);
                                    toast.show();

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            toast.cancel();
                                        }
                                    }, 500);
                                    next = true;
                                    break;
                                }
                            }
                        }
                        if (next) {
                            for (int i = 0; i < 9; ++i) {
                                bt[i].setBackgroundColor(Color.parseColor("#18FFFF"));
                            }
                            rNum = new Random().nextInt(9);
                            bt[rNum].setBackgroundColor(Color.BLACK);
                            next = false;
                        }
                    } else {
                        shut.onDismissClicked(a.alarm_id);
                    }
                }
            });
        }

        next=true;

        rNum=rn.nextInt(9);
        bt[rNum].setBackgroundColor(Color.BLACK);
        next=false;
    }
}
