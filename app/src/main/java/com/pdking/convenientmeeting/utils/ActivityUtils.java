package com.pdking.convenientmeeting.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.pdking.convenientmeeting.activity.FinishAllActivityActivity;

import java.util.ArrayList;

/**
 * @author liupeidong
 * Created on 2019/2/10 19:07
 */
public class ActivityUtils {

    public static void removeAllActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void removeAllActivity(Context context) {
        Intent intent = new Intent(context, FinishAllActivityActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
