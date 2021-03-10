package com.uniquext.android.application;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;

import com.uniquext.android.videotrimmer.VideoTrimmerActivity;

public class MainActivity extends AppCompatActivity {

    private final String[] permission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions(permission, 1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int grantResult : grantResults) {
            if (grantResult != PermissionChecker.PERMISSION_GRANTED) {
                break;
            }
            VideoTrimmerActivity.startVideoTrimmerActivity(this, "/storage/emulated/0/DCIM/Camera/VID_20210308_141406.mp4", 2);
        }
    }
}
