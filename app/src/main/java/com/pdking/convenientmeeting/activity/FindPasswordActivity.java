package com.pdking.convenientmeeting.activity;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.ActivityContainer;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.TitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FindPasswordActivity extends AppCompatActivity implements TitleView.LeftClickListener {

    @BindView(R.id.ed_find_phone_number)
    TextInputEditText ed_PhoneNumber;
    @BindView(R.id.ed_find_password)
    TextInputEditText ed_Password;
    @BindView(R.id.ed_find_password_again)
    TextInputEditText ed_Password_Again;
    @BindView(R.id.bt_find_next)
    Button bt_Next;
    @BindView(R.id.title)
    TitleView mTitleView;

    String s1 = "";
    String s2 = "";
    String s3 = "";

    boolean flag1 = false;
    boolean flag2 = false;
    boolean flag3 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_find_password);
        ButterKnife.bind(this);
        SystemUtil.setTitleMode(getWindow());
//        bt_Next.setEnabled(false);
        mTitleView.setLeftClickListener(this);
        ActivityContainer.addActivity(this);
    }

    @Override
    public void OnLeftButtonClick() {
        finish();
    }


    @OnClick(R.id.bt_find_next)
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_find_next:
                enterFindTwo();
                break;
        }
    }

    @OnTextChanged(R.id.ed_find_phone_number)
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

    @OnTextChanged(R.id.ed_find_password)
    void onPasswordTextChanged(CharSequence s) {
        if (s.length() >= 6 && s.length() <= 16) {
            flag2 = true;
        } else {
            flag2 = false;
        }
        setButtonStatus();
    }

    @OnTextChanged(R.id.ed_find_password_again)
    void onPasswordAgainTextChanged(CharSequence s) {
        if (s.length() >= 6 && s.length() <= 16) {
            flag3 = true;
        } else {
            flag3 = false;
        }
        setButtonStatus();
    }


    void enterFindTwo() {
        s1 = ed_PhoneNumber.getText().toString();
        s2 = ed_Password.getText().toString();
        s3 = ed_Password_Again.getText().toString();
        if (!s2.equals(s3)) {
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
        } else {
            if (confirmPhoneNumber()) {
                Intent intent = new Intent(this, FindPasswordTwoActivity.class);
                intent.putExtra("phone_number", s1);
                intent.putExtra("password", s2);
                startActivity(intent);
            } else {
                Toast.makeText(this, "账号不存在", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 确保账号存在
     * */
    private boolean confirmPhoneNumber() {
        return true;

    }

}
