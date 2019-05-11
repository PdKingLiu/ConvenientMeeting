package com.pdking.convenientmeeting.weight;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.pdking.convenientmeeting.R;

/**
 * @author liupeidong
 * Created on 2019/5/11 16:39
 */
public class AddMultiMemberDialog extends Dialog {

    public AddMultiMemberDialog(Context context, int style) {
        super(context, style);
        this.view = LayoutInflater.from(context).inflate(R.layout.layout_add_video, null);
        setContentView(view);
        init(view);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }
}
