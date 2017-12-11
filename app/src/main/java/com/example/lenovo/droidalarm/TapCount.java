package com.example.lenovo.droidalarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by Lenovo on 12-Oct-17.
 */

public class TapCount extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View v=inflater.inflate(R.layout.pick_number,null);
        builder.setTitle("Number Of Taps")
                .setView(v);
        final NumberPicker numberPicker=(NumberPicker)v.findViewById(R.id.picknum);
        numberPicker.setMaxValue(500);
        numberPicker.setMinValue(1);
        numberPicker.setWrapSelectorWheel(true);

        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try{
                    Counter activity=(Counter)getActivity();
                    activity.countSelected(numberPicker.getValue(),2);
                }catch (ClassCastException e){
                    e.printStackTrace();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try{
                    Counter activity=(Counter)getActivity();
                    activity.countSelected(-1,2);
                }catch (ClassCastException e){
                    e.printStackTrace();
                }
            }
        });

        return builder.create();
    }

}
