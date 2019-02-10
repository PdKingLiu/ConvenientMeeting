package com.pdking.convenientmeeting.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.ActivityContainer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class RegisterActivityOne extends AppCompatActivity {

    @BindView(R.id.ed_register_phone_number)
    TextInputEditText ed_PhoneNumber;
    @BindView(R.id.ed_register_password)
    TextInputEditText ed_Password;
    @BindView(R.id.ed_register_password_again)
    TextInputEditText ed_Password_Again;
    @BindView(R.id.bt_register_next)
    Button bt_Next;

    String s1 = "";
    String s2 = "";
    String s3 = "";

    boolean flag1 = false;
    boolean flag2 = false;
    boolean flag3 = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register_one);
        ButterKnife.bind(this);
        bt_Next.setEnabled(false);
        ActivityContainer.addActivity(this);

    }

    @OnClick(R.id.bt_register_next)
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_register_next:
                enterSMS();
                break;
        }
    }

    @OnTextChanged(R.id.ed_register_phone_number)
    void onPhoneNumberTextChanged(CharSequence s) {
        if (s.length() == 11) {
            flag1 = true;
        } else {
            flag1 = false;
        }
        setButtonStatus();
    }

    private void setButtonStatus() {
        if (flag1 && flag2 && flag3) {
            bt_Next.setEnabled(true);
        } else {
            bt_Next.setEnabled(false);
        }
    }

    @OnTextChanged(R.id.ed_register_password)
    void onPasswordTextChanged(CharSequence s) {
        if (s.length() >= 6 && s.length() <= 16) {
            flag2 = true;
        } else {
            flag2 = false;
        }
        setButtonStatus();
    }

    @OnTextChanged(R.id.ed_register_password_again)
    void onPasswordAgainTextChanged(CharSequence s) {
        if (s.length() >= 6 && s.length() <= 16) {
            flag3 = true;
        } else {
            flag3 = false;
        }
        setButtonStatus();
    }

    void enterSMS() {
        s1 = ed_PhoneNumber.getText().toString();
        s2 = ed_Password.getText().toString();
        s3 = ed_Password_Again.getText().toString();
        if (!s2.equals(s3)) {
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, RegisterActivityTwo.class);
            intent.putExtra("phone_number", s1);
            intent.putExtra("password", s2);
            startActivity(intent);
        }
    }

}
