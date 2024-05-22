package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                finish();
            }
        }, 5000);

        int result= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO);
        if (result == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.READ_MEDIA_AUDIO}, 2);
       }
        int permissionState = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.POST_NOTIFICATIONS);
        if (permissionState == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.POST_NOTIFICATIONS}, 1);}

    }






}