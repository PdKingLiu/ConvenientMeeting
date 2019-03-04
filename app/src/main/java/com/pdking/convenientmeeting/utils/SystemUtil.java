package com.pdking.convenientmeeting.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.view.View;
import android.view.Window;

/**
 * @author liupeidong
 * Created on 2019/2/14 16:09
 */
public class SystemUtil {

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen","android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public static void setTitleMode(Window window) {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.setNavigationBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

}
