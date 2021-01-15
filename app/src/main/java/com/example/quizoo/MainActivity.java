package com.example.quizoo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.quizoo.receiver.BatteryReceiver;

import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;

public class MainActivity extends Activity {
    private static final int PERMISSION_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(checkPermission()){
        }else {
            requestPermission();
        }

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this,HostActivity.class);
                startActivity(intent);
                finish();
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 1500);



    }

    //----- PEDIR PERMISOS -----
    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                READ_CONTACTS
        }, PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_CODE:
                if (grantResults.length > 0) {

                    boolean RealCallLogPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (RealCallLogPermission) {
                        Toast.makeText(this, "Los permisos estan concedidos", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    //Me comprueba que tengo todos los permisos, a la hora de realizar una acción
    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CONTACTS);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED;
    }
}