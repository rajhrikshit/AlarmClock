package com.example.lenovo.droidalarm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Lenovo on 03-Sep-17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<AlarmRow> {

    public static ArrayList<Alarm> alarmArrayList=new ArrayList<>();
    Context mContext;
    AlarmRow.OnItemClickListener mCall;

    public RecyclerAdapter(Context mContext,AlarmRow.OnItemClickListener call) {
        this.mContext = mContext;
        this.mCall=call;
    }

    @Override
    public AlarmRow onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_item,parent,false);
        return new AlarmRow(inflatedView,mContext,mCall);
    }

    @Override
    public void onBindViewHolder(AlarmRow holder, int position) {
        if(alarmArrayList.size()!=0) {
            Alarm alarmobj = alarmArrayList.get(position);
            holder.bindRow(alarmobj);
        }
    }

    @Override
    public int getItemCount() {
        return alarmArrayList.size();
    }

    public void newAlarmInserted(int hour, int min,String time, boolean alarmState, int alarmId,
                                 String alarmLabel, String ring, String method, int count,int tcount, ArrayList<Integer> list){
        alarmArrayList.add(new Alarm(hour,min,time,alarmState,alarmId,alarmLabel,ring,method,count,tcount,list));
        notifyDataSetChanged();
    }

    public void updateAlarm(int hour, int min,String time, boolean alarmState, int alarmId,
                            String alarmLabel, String ring, String method, int count,int tcount, ArrayList<Integer> list){
        for(int i=0;i<alarmArrayList.size();++i){
            if(alarmArrayList.get(i).alarm_id==alarmId){
                alarmArrayList.set(i,new Alarm(hour,min,time,alarmState,alarmId,alarmLabel,ring,method,count,tcount,list));
            }
        }
        notifyDataSetChanged();
    }

}
