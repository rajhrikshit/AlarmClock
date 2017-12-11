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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Lenovo on 29-Sep-17.
 */

public class MathMethod extends Fragment {

    private TextView problem;
    private EditText answer;
    private Button submit;
    private int ans;
    private int numProb;
    private int counter;
    private int thresh;
    private int opTh;

    private AlarmOff shut;
    private Alarm a;

    public MathMethod() {
        this.counter=0;
    }

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.math_off,container,false);
        problem=(TextView)v.findViewById(R.id.problem);
        answer=(EditText)v.findViewById(R.id.answer);
        submit=(Button)v.findViewById(R.id.submitbt);
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

        switch (AlarmFragment.math_diff){
            case 0:numProb=(int)(Math.random()*(3-1)+1);
                thresh=20;
                opTh=3;
                break;
            case 1:numProb=(int)(Math.random()*(4-2)+2);
                thresh=100;
                opTh=3;
                break;
            case 2:numProb=(int)(Math.random()*(6-2)+2);
                thresh=250;
                opTh=4;
                break;
            case 3:numProb=(int)(Math.random()*(10-2)+2);
                thresh=500;
                opTh=5;
                break;
        }


        Bundle args=getArguments();
        int id=args.getInt(AlarmFragment.ALARM_ID);
        for (Alarm i:RecyclerAdapter.alarmArrayList){
            if(i.alarm_id==id){
                a=i;
                break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ans=setProblem();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int sol = Integer.parseInt(answer.getText().toString());
                    if (sol == ans) {
                        ans = setProblem();
                        counter++;
                        if(counter !=numProb)
                            Toast.makeText(getActivity(),String.valueOf(numProb-counter)+" left",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity(),"TRY AGAIN",Toast.LENGTH_SHORT).show();
                    }
                    if (counter == numProb) {
                        try {
                            shut = (AlarmOff) getActivity();
                            shut.onDismissClicked(a.alarm_id);
                            problem.setText("DONE");
                            answer.setText("");
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }
                    }
                }catch(Exception e){
                    Toast.makeText(getActivity().getApplicationContext(),"TRY AGAIN",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int setProblem(){
        //Generating a random number for the number of operands

        int ans=0;
        String prob="";
        problem.setText("");
        answer.setText("");

        int fNum = (int) (Math.random() * (thresh - 10) + 10);
        prob += fNum;
        ans=fNum;

        for(int i=0;i<opTh/2;++i) {
            int operatorId = 3;
            prob += opId(operatorId);
            int sNum = (int) (Math.random() * (thresh - 10) + 1);
            prob += sNum;
            ans=perform(ans,3,sNum);
        }

        for(int i=opTh/2;i<opTh-1;++i){
            int opeatorId1=(int)(Math.random()*(3-1)+1);
            prob+=opId(opeatorId1);
            fNum=(int)(Math.random()*(thresh-1)+1);
            prob+=fNum;
            ans=perform(ans,opeatorId1,fNum);
        }
        problem.setText(prob);

        //Toast.makeText(getActivity(),String.valueOf(ans),Toast.LENGTH_SHORT).show();
        return ans;
    }

    private char opId(int id){
        switch(id){
            case 1:return '+';
            case 2:return '-';
            case 3:return '*';
            default:return ' ';
        }
    }

    private int perform(int a,int id,int b){
        switch (id){
            case 1:return a+b;
            case 2:return a-b;
            case 3:return a*b;
            default:return -1;
        }
    }

}
