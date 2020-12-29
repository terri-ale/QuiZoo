package com.example.quizoo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BatteryReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if(action.equals(Intent.ACTION_BATTERY_LOW)){
            Toast.makeText(context, "BAttery's dying!!", Toast.LENGTH_LONG).show();
        }

    }


    //UnaLinea
}