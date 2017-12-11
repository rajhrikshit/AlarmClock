package com.example.lenovo.droidalarm;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Lenovo on 28-Aug-17.
 *
 * If you return true from an ACTION_DOWN event you are interested in the rest of the events in that
 * gesture. A "gesture" in this case means all events until the final ACTION_UP or ACTION_CANCEL.
 * Returning false from an ACTION_DOWN means you do not want the event and other views will have the
 * opportunity to handle it. If you have overlapping views this can be a sibling view. If not it will
 * bubble up to the parent.
 */

public class DefaultMethod extends Fragment {
    Button dismiss=null;
    TextView showLabel=null;
    TextView showTime=null;
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
        View v= inflater.inflate(R.layout.default_off,container,false);
        dismiss=(Button)v.findViewById(R.id.dismiss);
        showLabel=(TextView)v.findViewById(R.id.showlabel);
        showTime=(TextView)v.findViewById(R.id.timer);
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

        showLabel.setText(a.label);
        showTime.setText(a.time);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    shut=(AlarmOff)getActivity();
                    shut.onDismissClicked(a.alarm_id);
                }catch (ClassCastException e){
                    e.printStackTrace();
                }
            }
        });
    }


}
