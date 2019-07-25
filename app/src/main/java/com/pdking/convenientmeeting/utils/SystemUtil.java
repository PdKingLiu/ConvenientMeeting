package com.pdking.convenientmeeting.utils;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;

/**
 * @author liupeidong
 * Created on 2019/2/14 16:09
 */
public class SystemUtil {

    public static void setTitleMode(Window window) {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

}
