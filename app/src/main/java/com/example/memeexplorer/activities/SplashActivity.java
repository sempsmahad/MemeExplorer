package com.example.memeexplorer.activities;


import android.Manifest;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.memeexplorer.R;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 1234;
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String MANAGE_EXTERNAL_STORAGE_PERMISSION = "android:manage_external_storage";

    private final Handler handler = new Handler();
    private ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progress_bar = findViewById(R.id.progress_bar);
        startProgressBar();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionDenied()) {

//             If Android 11 Request for Manage File Access Permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, REQUEST_PERMISSIONS);
                return;
            }

            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
        }

    }

    private void startProgressBar() {
        int progress = progress_bar.getProgress();
        progress = progress + 2;
        if (progress > progress_bar.getMax()) progress = 0;
        progress_bar.setProgress(progress);
        new Handler().postDelayed(this::startProgressBar, 50);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!arePermissionDenied()) {
            next();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    boolean checkStorageApi30() {
        AppOpsManager appOps = getApplicationContext().getSystemService(AppOpsManager.class);
        int mode = appOps.unsafeCheckOpNoThrow(
                MANAGE_EXTERNAL_STORAGE_PERMISSION,
                getApplicationContext().getApplicationInfo().uid,
                getApplicationContext().getPackageName()
        );
        return mode != AppOpsManager.MODE_ALLOWED;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS && grantResults.length > 0) {
            if (arePermissionDenied()) {
                // Clear Data of Application, So that it can request for permissions again
                ((ActivityManager) Objects.requireNonNull(this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
                recreate();
            } else {
                next();
            }
        }
    }

    private void next() {
        // Show splash screen for 2 seconds
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, DebugActivity.class));
                finish(); // kill current activity
            }
        };

        new Timer().schedule(task, 1000);

    }


    private boolean arePermissionDenied() {

        if (Build.VERSION.SDK_INT >= 30) {
            return checkStorageApi30();
        }

        for (String permissions : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), permissions) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }
}
