package com.pdking.convenientmeeting.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.ActivityContainer;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.TitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class RegisterActivityTwo extends AppCompatActivity implements TitleView.LeftClickListener{

    private String phoneNumber;

    private String password;

    @BindView(R.id.title)
    TitleView mTitleView;

    @BindView(R.id.tv_message_verify)
    TextView tv_Message;

    @BindView(R.id.tv_phone_number)
    TextView tv_Phone;

    @BindView(R.id.bt_register_two_new_verify)
    Button bt_Verify;

    @BindView((R.id.ed_register_two_verify))
    TextInputEditText ed_Verify;

    private boolean verifyFlag = true;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
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
        setContentView(R.layout.layout_register_two);
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

    @OnTextChanged(R.id.ed_register_two_verify)
    void onTextChanged(CharSequence s) {
        if (s.length() == 6) {
            if (!verifyFlag) {
                Toast.makeText(this, "验证失败", Toast.LENGTH_SHORT).show();
            } else {
                BmobSMS.verifySmsCode(phoneNumber, s.toString(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Intent intent = new Intent(RegisterActivityTwo.this,
                                    RegisterActivityThree.class);
                            intent.putExtra("phone_number", phoneNumber);
                            intent.putExtra("password", password);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterActivityTwo.this, "验证码错误", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void OnLeftButtonClick() {
        finish();
    }
}
