package com.example.lenovo.droidalarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Lenovo on 27-Sep-17.
 */

public class TurnOff extends DialogFragment {

    public interface Selection{
        public void onMethodSelected(String m);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String methods[]={"Default","Shake","Solve Math Problems","Tap Game"};
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Turn OFF Method")
                .setItems(methods, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try{
                            Selection activity=(Selection)getActivity();
                            activity.onMethodSelected(methods[i]);
                        }catch (ClassCastException e){
                            e.printStackTrace();
                        }
                    }
                });
        return builder.create();
    }
}