package com.example.lenovo.droidalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Lenovo on 27-Aug-17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        boolean dismissed=false;
        int id=intent.getExtras().getInt(AlarmFragment.ALARM_ID);
        Intent ring=new Intent(context,RingtoneService.class);
        ring.putExtra(AlarmFragment.ALARM_ID,id);

        if(intent.hasExtra(AlarmOn.DISMISSED)){
            dismissed =intent.getExtras().getBoolean(AlarmOn.DISMISSED);
            ring.putExtra(AlarmOn.DISMISSED,dismissed);
        }
        context.startService(ring);

    }
}
