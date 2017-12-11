package com.example.lenovo.droidalarm;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,TurnOff.Selection,Counter {

    private TimePicker timePicker;
    private EditText label;
    private Spinner ringtone_spinner;
    private TextView off_method;
    Toolbar toolbarM;
    Button repButton;

    public static ArrayList<String> list=new ArrayList(){{
        add("kudooku");
        add("amazing");
        add("believer");
        add("bell");
        add("faded");
        add("fairy");
        add("flute");
        add("friends");
        add("gta");
        add("numb");
        add("ring");
        add("tring");
        add("twilight");
    }};

    private Alarm a;
    public static int alarm_id;
    public String alarm_ring;
    public String method="Default";
    public int shake_cnt=-1;
    public int tap_cnt=-1;
    public static final String EXISTING_CALL="existing";
    public boolean exists=false;
    public ArrayList<Integer> weeks;
    private TextView offTv;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repButton=(Button)findViewById(R.id.btRep);

        final CheckBox sunday=(CheckBox) findViewById(R.id.sun);
        final CheckBox monday=(CheckBox) findViewById(R.id.mon);
        final CheckBox tuesday=(CheckBox) findViewById(R.id.tue);
        final CheckBox wednesday=(CheckBox) findViewById(R.id.wed);
        final CheckBox thursday=(CheckBox) findViewById(R.id.thu);
        final CheckBox friday=(CheckBox) findViewById(R.id.fri);
        final CheckBox saturday=(CheckBox) findViewById(R.id.sat);

        sunday.setVisibility(View.GONE);
        monday.setVisibility(View.GONE);
        tuesday.setVisibility(View.GONE);
        wednesday.setVisibility(View.GONE);
        thursday.setVisibility(View.GONE);
        friday.setVisibility(View.GONE);
        saturday.setVisibility(View.GONE);

        weeks=new ArrayList<Integer>();

        Intent intent=getIntent();

        offTv=(TextView)findViewById(R.id.offMethod);

        toolbarM=(Toolbar)findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbarM);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        timePicker = (TimePicker)findViewById(R.id.timepicker);
        label=(EditText)findViewById(R.id.label);
        ringtone_spinner=(Spinner)findViewById(R.id.spinner_ringtone);
        off_method=(TextView)findViewById(R.id.offMethod);

        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ringtone_spinner.setAdapter(arrayAdapter);
        ringtone_spinner.setOnItemSelectedListener(this);

        Button set_alarm=(Button)findViewById(R.id.btSet);
        Button cancel_alarm=(Button)findViewById(R.id.btCancel);

        if(intent!=null && intent.hasExtra(EXISTING_CALL)){
            exists=true;
            int id=intent.getExtras().getInt(EXISTING_CALL);
            for(Alarm i:RecyclerAdapter.alarmArrayList){
                if(i.alarm_id==id){
                    a=i;
                    method=a.off_method;
                    shake_cnt=a.shake_count;
                    tap_cnt=a.tap_count;
                    break;
                }
            }

            offTv.setText(a.off_method);

            if(a.weekList.size()!=0) {
                sunday.setVisibility(View.VISIBLE);
                monday.setVisibility(View.VISIBLE);
                tuesday.setVisibility(View.VISIBLE);
                wednesday.setVisibility(View.VISIBLE);
                thursday.setVisibility(View.VISIBLE);
                friday.setVisibility(View.VISIBLE);
                saturday.setVisibility(View.VISIBLE);

                weeks=a.weekList;

                for (int i : a.weekList) {
                    if (i == 1)
                        sunday.setChecked(true);
                    else if (i == 2)
                        monday.setChecked(true);
                    else if (i == 3)
                        tuesday.setChecked(true);
                    else if (i == 4)
                        wednesday.setChecked(true);
                    else if (i == 5)
                        thursday.setChecked(true);
                    else if (i == 6)
                        friday.setChecked(true);
                    else if (i == 7)
                        saturday.setChecked(true);
                }
            }else {
                sunday.setVisibility(View.GONE);
                monday.setVisibility(View.GONE);
                tuesday.setVisibility(View.GONE);
                wednesday.setVisibility(View.GONE);
                thursday.setVisibility(View.GONE);
                friday.setVisibility(View.GONE);
                saturday.setVisibility(View.GONE);
            }

            timePicker.setCurrentHour(a.hour);
            timePicker.setCurrentMinute(a.minutes);

            int pos=-1;
            for(int i=0;i<list.size();++i){
                if(list.get(i).equals(a.ringtone)){
                    alarm_ring=a.ringtone;
                    pos=i;
                    break;
                }
            }

            if(pos!=-1)
                ringtone_spinner.setSelection(pos);

            label.setText(a.label);
        }

        //Selecting the Method to turn off the Alarm
        off_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager=getFragmentManager();
                TurnOff dialog=new TurnOff();
                dialog.show(fragmentManager,"show");
            }
        });

        repButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sunday.setVisibility(View.VISIBLE);
                monday.setVisibility(View.VISIBLE);
                tuesday.setVisibility(View.VISIBLE);
                wednesday.setVisibility(View.VISIBLE);
                thursday.setVisibility(View.VISIBLE);
                friday.setVisibility(View.VISIBLE);
                saturday.setVisibility(View.VISIBLE);
            }
        });


        set_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(sunday.isChecked()){
                    weeks.add(1);
                }if(monday.isChecked()) {
                    weeks.add(2);
                }if(tuesday.isChecked()) {
                    weeks.add(3);
                }if(wednesday.isChecked()) {
                    weeks.add(4);
                }if(thursday.isChecked()) {
                    weeks.add(5);
                }if(friday.isChecked()) {
                    weeks.add(6);
                }if(saturday.isChecked()) {
                    weeks.add(7);
                }

                String aLabel=label.getText().toString();

                String time="";
                int mHour;
                int mMinutes;

                mHour = timePicker.getCurrentHour();
                mMinutes = timePicker.getCurrentMinute();

                if(mMinutes<10){
                    time+="0"+String.valueOf(mMinutes);

                }else{
                    time+=String.valueOf(mMinutes);
                }

                if(mHour>=12){
                    if(mHour>12)
                        time=String.valueOf(mHour-12)+":"+time+" pm";
                    else
                        time=String.valueOf(12)+":"+time+" pm";
                }else{
                    if(mHour==0){
                        time="12:"+time+" am";
                    }
                    else{
                        time=String.valueOf(mHour)+":"+time+" am";
                    }
                }

                if(weeks.size()!=0)
                Toast.makeText(MainActivity.this,time,Toast.LENGTH_LONG).show();

                //Return Intent to the Base Activity with the details of the Alarm to be displayed in the list.
                Intent returnIntent=new Intent();

                returnIntent.putExtra(AlarmFragment.ALARM_HOUR,mHour);
                returnIntent.putExtra(AlarmFragment.ALARM_MIN,mMinutes);
                if(exists==false) {
                    returnIntent.putExtra(AlarmFragment.ALARM_ID, alarm_id);
                    returnIntent.putExtra(EXISTING_CALL,false);
                }else {
                    returnIntent.putExtra(AlarmFragment.ALARM_ID, a.alarm_id);
                    returnIntent.putExtra(EXISTING_CALL,true);
                }
                returnIntent.putExtra(AlarmFragment.RINGTONE,alarm_ring);
                returnIntent.putExtra(AlarmFragment.ALARM_TIME,time);
                returnIntent.putExtra(AlarmFragment.ALARM_STATE,true);
                returnIntent.putExtra(AlarmFragment.ALARM_LABEL,aLabel);
                returnIntent.putExtra(AlarmFragment.OFF_METHOD,method);
                returnIntent.putExtra(AlarmFragment.SHAKE_COUNT,shake_cnt);
                returnIntent.putExtra(AlarmFragment.TAP_COUNT,tap_cnt);
                returnIntent.putExtra(AlarmFragment.WEEKDAYS,weeks);
                setResult(Activity.RESULT_OK,returnIntent);
                //Return Intent
                if(exists==false)
                alarm_id++;

                finish();
            }
        });

        cancel_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Just Return to the  base Activity
               finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        alarm_ring=(String)adapterView.getItemAtPosition(i);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

        alarm_ring="kudooku";
    }

    @Override
    public void onMethodSelected(String m) {
        method=m;
        offTv.setText(method);
        switch(m){
            case "Shake":
                FragmentManager fragmentManager=getFragmentManager();
                ShakeCount shakeCount=new ShakeCount();
                shakeCount.show(fragmentManager,"Count");
                break;
            case "Tap Game":
                FragmentManager fragmentManagers=getFragmentManager();
                TapCount tapCount=new TapCount();
                tapCount.show(fragmentManagers,"Counts");
                break;

        }
    }

    @Override
    public void countSelected(int n, int id) {
        if(n!=-1){
            if(id==1) {
                shake_cnt = n;
            }else if(id==2){
                tap_cnt=n;
            }
        }else{
            method="Default";
        }
    }
}
