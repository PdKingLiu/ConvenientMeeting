package com.pdking.convenientmeeting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.TitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class RegisterActivityOne extends AppCompatActivity implements TitleView.LeftClickListener {

    @BindView(R.id.ed_register_phone_number)
    TextInputEditText ed_PhoneNumber;
    @BindView(R.id.ed_register_password)
    TextInputEditText ed_Password;
    @BindView(R.id.ed_register_password_again)
    TextInputEditText ed_Password_Again;
    @BindView(R.id.bt_register_next)
    Button bt_Next;
    @BindView(R.id.title)
    TitleView mTitleView;

    private String s1 = "";
    private String s2 = "";
    private String s3 = "";

    private boolean flag1 = false;
    private boolean flag2 = false;
    private boolean flag3 = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register_one);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        bt_Next.setEnabled(false);
        mTitleView.setLeftClickListener(this);
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

    private void enterSMS() {
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

    @Override
    public void OnLeftButtonClick() {
        finish();
    }
}
