package com.pdking.convenientmeeting.common;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/2/10 19:07
 */
public class ActivityContainer {

    private static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeAllActivity() {
        for (Activity activity:activities) {
            activity.finish();
        }
    }

}
