package com.pdking.convenientmeeting.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * @author liupeidong
 * Created on 2019/3/16 10:03
 */
public class PermissionUtil {
    public static void applyPermission(AppCompatActivity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new
                    String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
                    .CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
}
