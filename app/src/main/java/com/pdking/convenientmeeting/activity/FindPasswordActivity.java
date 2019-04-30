package com.pdking.convenientmeeting.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
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

public class FindPasswordActivity extends AppCompatActivity implements TitleView.LeftClickListener {

    @BindView(R.id.ed_phone)
    EditText edPhoneNumber;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.ed_new_password)
    EditText edNewPassword;
    @BindView(R.id.ed_new_password_again)
    EditText edNewPasswordAgain;
    @BindView(R.id.tv_phone_send_status)
    TextView tvCodeSendStatus;
    @BindView(R.id.btn_phone_send_code)
    Button btnSendCode;
    @BindView(R.id.ed_phone_code)
    EditText edPhoneCode;
    @BindView(R.id.title)
    TitleView title;
    @BindView(R.id.btn_start_find)
    Button btnStart;
    @BindView(R.id.ll_scroll)
    LinearLayout linearLayout;

    private CountDownTimerUtils timerUtils;

    private boolean flag[] = {false, false, false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_find_password);
        ButterKnife.bind(this);
        SystemUtil.setTitleMode(getWindow());
        title.setLeftClickListener(this);
        btnStart.setEnabled(false);
        addLayoutListener(linearLayout, edPhoneCode);
        edPhoneCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("lpp", "onFocusChange: " + hasFocus);
            }
        });
    }

    public void addLayoutListener(final View main, final View scroll) {
        main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                //1、获取main在窗体的可视区域
                scroll.getWindowVisibleDisplayFrame(rect);
                int mainInvisibleHeight = scroll.getRootView().getHeight() - rect.bottom;
                int screenHeight = scroll.getRootView().getHeight();//屏幕高度
                //3、不可见区域大于屏幕本身高度的1/4：说明键盘弹起了
                if (mainInvisibleHeight > screenHeight / 4) {
                    int[] location = new int[2];
                    scroll.getLocationInWindow(location);
                    // 4､获取Scroll的窗体坐标，算出main需要滚动的高度
                    int srollHeight = (location[1] + scroll.getHeight()) - rect.bottom;
                    //5､让界面整体上移键盘的高度
                    main.scrollTo(0, srollHeight);
                } else {
                    //3、不可见区域小于屏幕高度1/4时,说明键盘隐藏了，把界面下移，移回到原有高度
                    main.scrollTo(0, 0);
                }
            }
        });
    }

    @Override
    public void OnLeftButtonClick() {
        finish();
    }


    @OnClick({R.id.btn_phone_send_code, R.id.btn_start_find})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_find:
                startFind();
                break;
            case R.id.btn_phone_send_code:
                if (edPhoneNumber.getText().toString().length() != 11) {
                    Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                    break;
                }
                sendPhoneCode(edPhoneNumber.getText().toString(), btnSendCode, tvCodeSendStatus);
                break;
        }
    }

    @OnTextChanged(R.id.ed_phone)
    void onPhoneNumberTextChanged(CharSequence s) {
        if (s.length() == 11) {
            flag[0] = true;
        } else {
            flag[0] = false;
        }
        setButtonStatus();
    }

    private void setButtonStatus() {
        if (flag[0] && flag[1] && flag[2] && flag[3]) {
            btnStart.setEnabled(true);
        } else {
            btnStart.setEnabled(false);
        }
    }

    @OnTextChanged(R.id.ed_new_password)
    void onPasswordTextChanged(CharSequence s) {
        if (s.length() >= 6 && s.length() <= 16) {
            flag[1] = true;
        } else {
            flag[1] = false;
        }
        setButtonStatus();
    }

    @OnTextChanged(R.id.ed_new_password_again)
    void onPasswordAgainTextChanged(CharSequence s) {
        if (s.length() >= 6 && s.length() <= 16) {
            flag[2] = true;
        } else {
            flag[2] = false;
        }
        setButtonStatus();
    }

    @OnTextChanged(R.id.ed_phone_code)
    void onCodeTextTextChanged(CharSequence s) {
        if (s.length() == 6) {
            flag[3] = true;
        } else {
            flag[3] = false;
        }
        setButtonStatus();
    }

    private void startFind() {
        String phone = edPhoneNumber.getText().toString();
        String password = edNewPassword.getText().toString();
        String passwordAgain = edNewPasswordAgain.getText().toString();
        String code = edPhoneCode.getText().toString();
        if (!password.equals(passwordAgain)) {
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.FindPasswordBody[0], code);
        body.add(Api.FindPasswordBody[1], phone);
        body.add(Api.FindPasswordBody[2], password);
        final Request request = new Request.Builder()
                .url(Api.FindPasswordApi)
                .header(Api.FindPasswordHeader[0], Api.FindPasswordHeader[1])
                .post(body.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Lpp", "onFailure: " + e.getMessage());
                showToast("修改失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Log.d("Lpp", "onResponse: " + msg);
                RequestReturnBean bean = new Gson().fromJson(msg, RequestReturnBean.class);
                if (bean.status == 1) {
                    showToast("修改失败，验证码或其他其他信息有误");
                } else {
                    showToast("修改成功，请重新登录");
                    startActivity(new Intent(FindPasswordActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });
    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FindPasswordActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changSendStatus(final String text, final TextView textView) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }

    private void sendPhoneCode(String phone, Button btnSendCode, final TextView tvSendStatus) {
        changSendStatus("发送中", tvSendStatus);
        timerUtils = new CountDownTimerUtils(btnSendCode, 60000, 1000);
        timerUtils.start();
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add(Api.SMSSendBody[0], phone);
        Request request = new Request.Builder()
                .url(Api.SMSSendApi)
                .post(formBody.build())
                .header(Api.SMSSendHeader[0], Api.SMSSendHeader[1])
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                changSendStatus("发送验证码失败", tvSendStatus);
                Log.d("Lpp", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SMSSendStatusBean smsSendStatusBean = new Gson().fromJson(response.body().string
                        (), SMSSendStatusBean.class);
                Log.d("Lpp", "onResponse: " + smsSendStatusBean.data);
                if (smsSendStatusBean.status == 0) {
                    changSendStatus("发送成功", tvSendStatus);
                } else {
                    changSendStatus("发送验证码失败", tvSendStatus);
                }
            }
        });
    }
}
