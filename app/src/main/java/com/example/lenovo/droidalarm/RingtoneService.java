package com.example.lenovo.droidalarm;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Lenovo on 27-Aug-17.
 */

public class RingtoneService extends Service {


    private static boolean ringingstate=false;
    NotificationManager notificationManager=null;
    private String ring;
    private MediaPlayer ringtone;
    public static final int NOTIFICATION_ID=0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {

        Boolean playstate=false;
        int id=intent.getExtras().getInt(AlarmFragment.ALARM_ID);
        for(Alarm i:RecyclerAdapter.alarmArrayList){
            if(i.alarm_id==id){
                ring=i.ringtone;
                playstate=i.state;
                Log.e("Alarm state",String.valueOf(i.state));
                break;
            }
        }
        boolean dismissed=false;

        if(intent.hasExtra(AlarmOn.DISMISSED)){
            dismissed =intent.getExtras().getBoolean(AlarmOn.DISMISSED);
        }

        Notification notification;
        Vibrator v=(Vibrator)getSystemService(VIBRATOR_SERVICE);

        if(!(ringingstate) && playstate){
            AudioManager audio=(AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            int currentVol=audio.getStreamVolume(AudioManager.STREAM_RING);
            int max=audio.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
            audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            audio.setStreamVolume(AudioManager.STREAM_RING,max,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            int resId=getResources().getIdentifier(ring,"raw",this.getPackageName());
            ringtone=MediaPlayer.create(this,resId);
            ringtone.start();
            ringtone.setLooping(true);
            ringingstate=true;

            //Setting up the intent for the Notification
            Intent notify=new Intent(getApplicationContext(),AlarmOn.class);
            notify.putExtra(AlarmFragment.ALARM_ID,id);
            //Pending Intent
            PendingIntent pending_notify=PendingIntent.getActivity(this,0,notify,PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                pending_notify.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
            //Setting up the NotificationManager

            notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

            //Setting up the Notifiaction

            notification=new Notification.Builder(this)
                .setContentTitle("WAKE UP TIME")
                .setContentText("Click ME")
                .setContentIntent(pending_notify)
                .setOngoing(true)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.ic_alarm_white_24dp)
                .setLights(Color.GREEN,2000,2000)
                .build();

            notificationManager.notify(NOTIFICATION_ID,notification);
            long pattern[]={0,1000,1000};
            v.vibrate(pattern,0);

        }else if(ringingstate && !playstate){
            notificationManager.cancel(NOTIFICATION_ID);
            ringtone.stop();
            ringtone.reset();
            ringingstate=false;
            v.cancel();
            notificationManager.cancelAll();
        }else if(dismissed){
            notificationManager.cancel(NOTIFICATION_ID);
            ringtone.stop();
            ringtone.reset();
            ringingstate=false;
            v.cancel();
            notificationManager.cancelAll();
        }

        return START_NOT_STICKY;
    }
}
