package com.example.quizoo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.quizoo.receiver.BatteryReceiver;
import com.example.quizoo.viewmodel.ViewModelActivity;

public class HostActivity extends AppCompatActivity {

    private BatteryReceiver batteryReceiver;
    private ViewModelActivity viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        batteryReceiver = new BatteryReceiver();


        /*
        *  Debemos comprobar que fragmento esta siendo siendo visualizado en el navhostfragment para ocultar
        * o mostrar el drawer a nuestro gusto

        * */

        viewModel = new ViewModelProvider(this).get(ViewModelActivity.class);

        viewModel.getCurrentFragment().observe(this, new Observer<Fragment>() {
            @Override
            public void onChanged(Fragment fragment) {
                Log.v("xyzyx", "EL FRAGMENTO ACTUAL ES: "+fragment.getClass());
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_LOW);
        registerReceiver(batteryReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(batteryReceiver);
    }


}