package com.pdking.convenientmeeting.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.ActivityContainer;
import com.pdking.convenientmeeting.common.Constant;
import com.pdking.convenientmeeting.utils.CountDownTimerUtils;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.TitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class FindPasswordTwoActivity extends AppCompatActivity implements TitleView.LeftClickListener {


    private String phoneNumber;

    private String password;

    private CountDownTimerUtils mCountDownTimerUtils;

    @BindView(R.id.title)
    TitleView mTitleView;

    @BindView(R.id.tv_message_verify)
    TextView tv_Message;

    @BindView(R.id.tv_phone_number)
    TextView tv_Phone;

    @BindView(R.id.bt_find_two_new_verify)
    Button bt_Verify;

    @BindView((R.id.ed_find_two_verify))
    TextInputEditText ed_Verify;

    private boolean verifyFlag = true;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mCountDownTimerUtils = new CountDownTimerUtils(bt_Verify, 60000, 1000);
            mCountDownTimerUtils.start();
            switch (msg.what) {
                case 1:
                    tv_Phone.setText((String) msg.obj);
                    break;
                case 2:
                    tv_Message.setText("发送验证码成功");
                    break;
                case 3:
                    tv_Message.setText((String) msg.obj);
                    verifyFlag = false;
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_find_password_two);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        ActivityContainer.addActivity(this);
        mTitleView.setLeftClickListener(this);
        Message msg = new Message();
        final Message msg2 = new Message();
        phoneNumber = getIntent().getStringExtra("phone_number");
        password = getIntent().getStringExtra("password");
        msg.what = 1;
        msg.obj = phoneNumber;
        mHandler.sendMessage(msg);
        mCountDownTimerUtils = new CountDownTimerUtils(bt_Verify, 60000, 1000);
        mCountDownTimerUtils.start();
        BmobSMS.requestSMSCode(phoneNumber, "", new QueryListener<Integer>() {
            @Override
            public void done(Integer smsId, BmobException e) {
                if (e == null) {
                    msg2.what = 2;
                    mHandler.sendMessage(msg2);
                } else {
                    msg2.what = 3;
                    msg2.obj = e.getMessage();
                    mHandler.sendMessage(msg2);
                }
            }
        });
    }
    @OnClick(R.id.bt_find_two_new_verify)
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_find_two_new_verify:
                final Message msg2 = new Message();
                mCountDownTimerUtils = new CountDownTimerUtils(bt_Verify, 60000, 1000);
                mCountDownTimerUtils.start();
                BmobSMS.requestSMSCode(phoneNumber, "", new QueryListener<Integer>() {
                    @Override
                    public void done(Integer smsId, BmobException e) {
                        if (e == null) {
                            msg2.what = 2;
                            mHandler.sendMessage(msg2);
                        } else {
                            msg2.what = 3;
                            msg2.obj = e.getMessage();
                            mHandler.sendMessage(msg2);
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void OnLeftButtonClick() {
        finish();
    }

    @OnTextChanged(R.id.ed_find_two_verify)
    void onTextChanged(CharSequence s) {
        if (s.length() == 6) {
            if (!verifyFlag) {
                Toast.makeText(this, "验证失败", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(FindPasswordTwoActivity.this,
                        LoginActivity.class);
                startActivity(intent);
                ActivityContainer.removeAllActivity();

            } else {
                BmobSMS.verifySmsCode(phoneNumber, s.toString(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            if (resetPassword(phoneNumber, password)) {
                                Toast.makeText(FindPasswordTwoActivity.this, "设置成功", Toast
                                        .LENGTH_SHORT)
                                        .show();
                                Intent intent = new Intent(FindPasswordTwoActivity.this,
                                        LoginActivity.class);
                                startActivity(intent);
                                ActivityContainer.removeAllActivity();
                            } else {
                                Toast.makeText(FindPasswordTwoActivity.this, "设置失败", Toast
                                        .LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(FindPasswordTwoActivity.this, "验证码错误", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
            }
        }
    }

    private boolean resetPassword(String phoneNumber, String password) {
        return true;
    }


}
