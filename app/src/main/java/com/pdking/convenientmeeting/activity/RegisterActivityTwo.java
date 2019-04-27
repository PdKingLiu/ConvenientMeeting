package com.pdking.convenientmeeting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.ActivityContainer;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.RequestReturnBean;
import com.pdking.convenientmeeting.db.SMSSendStatusBean;
import com.pdking.convenientmeeting.utils.CountDownTimerUtils;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.TitleView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivityTwo extends AppCompatActivity implements TitleView.LeftClickListener {

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
    private String phoneNumber;
    private String password;
    private CountDownTimerUtils mCountDownTimerUtils;
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

    @OnClick(R.id.bt_register_two_new_verify)
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_register_two_new_verify:
                final Message msg2 = new Message();
                mCountDownTimerUtils = new CountDownTimerUtils(bt_Verify, 60000, 1000);
                mCountDownTimerUtils.start();
                OkHttpClient client = new OkHttpClient();
                FormBody.Builder formBody = new FormBody.Builder();
                formBody.add(Api.SMSSendBody[0], phoneNumber);
                Request request = new Request.Builder()
                        .url(Api.SMSSendApi)
                        .header(Api.SMSSendHeader[0], Api.SMSSendHeader[1])
                        .post(formBody.build())
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        msg2.what = 3;
                        msg2.obj = "发送验证码失败";
                        mHandler.sendMessage(msg2);
                        Log.d("Lpp", "onFailure: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        SMSSendStatusBean smsSendStatusBean = new Gson().fromJson(response.body()
                                        .string(),
                                SMSSendStatusBean
                                        .class);
                        if (smsSendStatusBean.status == 0) {
                            msg2.what = 2;
                            mHandler.sendMessage(msg2);
                        } else {
                            msg2.what = 3;
                            msg2.obj = "发送验证码失败";
                            mHandler.sendMessage(msg2);
                        }
                    }
                });
                break;
        }
    }

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
        mCountDownTimerUtils = new CountDownTimerUtils(bt_Verify, 60000, 1000);
        mCountDownTimerUtils.start();
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add(Api.SMSSendBody[0], phoneNumber);
        Request request = new Request.Builder()
                .url(Api.SMSSendApi)
                .post(formBody.build())
                .header(Api.SMSSendHeader[0], Api.SMSSendHeader[1])
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                msg2.what = 3;
                msg2.obj = "发送验证码失败";
                mHandler.sendMessage(msg2);
                Log.d("Lpp", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SMSSendStatusBean smsSendStatusBean = new Gson().fromJson(response.body().string
                        (), SMSSendStatusBean.class);
                Log.d("Lpp", "onResponse: " + smsSendStatusBean.data);
                if (smsSendStatusBean.status == 0) {
                    msg2.what = 2;
                    mHandler.sendMessage(msg2);
                } else {
                    msg2.what = 3;
                    msg2.obj = "发送验证码失败";
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
                OkHttpClient client = new OkHttpClient();
                FormBody.Builder formBody = new FormBody.Builder();
                formBody.add(Api.SMSVerificationBody[0], phoneNumber);
                formBody.add(Api.SMSVerificationBody[1], s.toString());
                final Request request = new Request.Builder()
                        .header(Api.SMSVerificationHeader[0], Api.SMSVerificationHeader[1])
                        .url(Api.SMSVerificationApi)
                        .post(formBody.build())
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        setToast("验证失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        RequestReturnBean statusBean = new Gson().fromJson(response.body()
                                .string(), RequestReturnBean.class);
                        if (statusBean.status == 0) {
                            setToast("验证成功");
                            Intent intent = new Intent(RegisterActivityTwo.this,
                                    RegisterActivityThree.class);
                            intent.putExtra("phone_number", phoneNumber);
                            intent.putExtra("password", password);
                            startActivity(intent);
                        } else {
                            setToast("验证码错误");
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

    public void setToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegisterActivityTwo.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
