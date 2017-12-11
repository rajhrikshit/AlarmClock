package com.example.lenovo.droidalarm;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Lenovo on 28-Sep-17.
 */

public class AlarmOn extends AppCompatActivity implements AlarmOff {

    public static final String DISMISSED="dismiss clicked";
    public static int snooze_id=0;
    AlarmManager alarmy;
    String method;
    Alarm a;
    PowerManager.WakeLock fWakeLock,pWakeLock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dumb_parent);

        if(findViewById(R.id.dumb)!=null) {

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

            createWakeLocks();
            wakeDevice();

            alarmy = (AlarmManager) getSystemService(ALARM_SERVICE);

            int id = getIntent().getExtras().getInt(AlarmFragment.ALARM_ID);
            for (Alarm i : RecyclerAdapter.alarmArrayList) {
                if (i.alarm_id == id) {
                    a = i;
                    method = i.off_method;
                    break;
                }
            }

            Bundle args = new Bundle();
            args.putInt(AlarmFragment.ALARM_ID, id);

            switch (method) {
                //Destroy the fragments from within themselves once the dismiss/snooze choice is made
                case "Default":
                    DefaultMethod defaultMethod = new DefaultMethod();
                    defaultMethod.setArguments(args);
                    getFragmentManager().beginTransaction()
                            .add(R.id.dumb, defaultMethod)
                            .commit();
                    break;
                case "Shake":
                    ShakerMethod shakerMethod = new ShakerMethod();
                    shakerMethod.setArguments(args);
                    getFragmentManager().beginTransaction()
                            .add(R.id.dumb, shakerMethod)
                            .commit();
                    break;
                case "Solve Math Problems":
                    MathMethod mathMethod=new MathMethod();
                    mathMethod.setArguments(args);
                    getFragmentManager().beginTransaction()
                            .add(R.id.dumb, mathMethod)
                            .commit();
                    break;
                case "Tap Game":
                    TapMethod tapMethod=new TapMethod();
                    tapMethod.setArguments(args);
                    getFragmentManager().beginTransaction()
                            .add(R.id.dumb, tapMethod)
                            .commit();
                    break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onDismissClicked(int id) {

        Toast.makeText(getApplicationContext(),"DISMISSED",Toast.LENGTH_LONG).show();
        Intent i=new Intent(AlarmOn.this,AlarmReceiver.class);
        i.putExtra(AlarmFragment.ALARM_ID,id);
        i.putExtra(DISMISSED,true);
        sendBroadcast(i);
        finish();
    }

    @Override
    public void onSnoozeClicked(int id) {

        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MINUTE, AlarmFragment.snooze_time);
        Intent snoozecall=new Intent(AlarmOn.this,AlarmReceiver.class);
        snoozecall.putExtra(AlarmFragment.ALARM_ID,id);
        PendingIntent pIntent=PendingIntent.getBroadcast(AlarmOn.this,snooze_id,snoozecall,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmy.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pIntent);
        snooze_id++;
        Toast.makeText(getApplicationContext(),"SNOOZED",Toast.LENGTH_LONG).show();
        //Cancelling the present Alarm
        Intent i=new Intent(AlarmOn.this,AlarmReceiver.class);
        i.putExtra(AlarmFragment.ALARM_ID,id);
        i.putExtra(DISMISSED,true);
        sendBroadcast(i);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pWakeLock.acquire();
        wakeDevice();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(fWakeLock.isHeld()){
            fWakeLock.release();
        }
        if(pWakeLock.isHeld()){
            pWakeLock.release();
        }
    }

    @Override
    public void onBackPressed() {
        //Do Nothing
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
            onSnoozeClicked(a.alarm_id);
        }else if(keyCode==KeyEvent.KEYCODE_VOLUME_UP && method.equals("Default")){
            onDismissClicked(a.alarm_id);
        }else if(keyCode==KeyEvent.KEYCODE_HOME){
            //Do Nothing
        }
        return true;
    }

    protected void createWakeLocks(){
        PowerManager powerManager=(PowerManager)getSystemService(Context.POWER_SERVICE);
        fWakeLock=powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK |
        PowerManager.ACQUIRE_CAUSES_WAKEUP), "Wake Up Boy");
        pWakeLock=powerManager.newWakeLock((PowerManager.PARTIAL_WAKE_LOCK),"Its's Timw");
    }

    public void wakeDevice(){
        fWakeLock.acquire();

        KeyguardManager keyguardManager=(KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock=keyguardManager.newKeyguardLock("Hey");
        keyguardLock.disableKeyguard();
    }
}
