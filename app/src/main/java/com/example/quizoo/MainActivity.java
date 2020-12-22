package com.example.quizoo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.example.quizoo.receiver.BatteryReceiver;

public class MainActivity extends AppCompatActivity {

    private IntentFilter batteryIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        batteryIntentFilter=new IntentFilter();
        batteryIntentFilter.addAction(Intent.ACTION_BATTERY_LOW);
    }



    @Override
    protected void onResume(){
        super.onResume();

        registerReceiver(new BatteryReceiver(), batteryIntentFilter);

    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(broadcast);
    }

}