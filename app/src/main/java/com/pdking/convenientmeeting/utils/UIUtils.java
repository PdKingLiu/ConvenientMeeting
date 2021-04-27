package com.pdking.convenientmeeting.utils;

import android.app.Activity;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

/**
 * @author liupeidong
 * Created on 2019/4/18 22:18
 */
public class UIUtils {
    public static void hideProgressBar(Activity activity, final AlertDialog dialog) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.hide();
                }
            }
        });
    }

    public static void showProgressBar(Activity activity, final AlertDialog dialog) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.show();
                }
            }
        });
    }

    public static void showToast(final Activity activity, final String text) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
