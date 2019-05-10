package com.pdking.convenientmeeting.weight;

import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.pdking.convenientmeeting.R;

/**
 * @author liupeidong
 * Created on 2019/5/10 22:29
 */
public class EnterLiveMeetingDialog extends Dialog implements View.OnClickListener, CompoundButton
        .OnCheckedChangeListener {

    private View view;
    private EditText edRoomPassword;
    private CheckBox cbShowPassword;
    private Button btnCancel;
    private Button btnAdd;
    private OnClickListener listener;

    public EnterLiveMeetingDialog(Context context, int style) {
        super(context, style);
        this.view = LayoutInflater.from(context).inflate(R.layout.layout_enter_video, null);
        setContentView(view);
        init(view);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }


    private void init(View view) {
        edRoomPassword = view.findViewById(R.id.ed_room_password);
        cbShowPassword = view.findViewById(R.id.cb_show_password);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnAdd = view.findViewById(R.id.btn_enter);
        btnCancel.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        cbShowPassword.setOnCheckedChangeListener(this);
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                this.hide();
                this.dismiss();
                break;
            case R.id.btn_enter:
                String password = edRoomPassword.getText().toString();
                if (listener != null) {
                    listener.onClick(password);
                }
                this.hide();
                this.dismiss();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            edRoomPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            edRoomPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType
                    .TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    public interface OnClickListener {
        void onClick(String password);
    }

}
