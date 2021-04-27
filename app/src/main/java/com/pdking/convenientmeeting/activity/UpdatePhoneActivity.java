package com.pdking.convenientmeeting.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.RequestReturnBean;
import com.pdking.convenientmeeting.db.SMSSendStatusBean;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.ActivityUtils;
import com.pdking.convenientmeeting.utils.CountDownTimerUtils;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.utils.UserAccountUtils;
import com.pdking.convenientmeeting.weight.TitleView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdatePhoneActivity extends AppCompatActivity {

    @BindView(R.id.title)
    TitleView titleView;
    @BindView(R.id.tv_old_phone)
    TextView tvOldPhone;
    @BindView(R.id.ed_old_phone_code)
    EditText edOldPhoneCode;
    @BindView(R.id.tv_old_phone_send_status)
    TextView tvOldPhoneSendStatus;
    @BindView(R.id.btn_old_phone_send_code)
    Button btnOldPhoneSendCode;
    @BindView(R.id.ed_new_phone)
    EditText edNewPhone;
    @BindView(R.id.tv_new_phone)
    TextView tvNewPhone;
    @BindView(R.id.ed_new_phone_code)
    EditText edNewPhoneCode;
    @BindView(R.id.tv_new_phone_send_status)
    TextView tvNewPhoneSendStatus;
    @BindView(R.id.btn_new_phone_send_code)
    Button btnNewPhoneSendCode;
    @BindView(R.id.btn_update_phone)
    Button btnUpdatePhone;

    private CountDownTimerUtils timerUtils;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_update_phone);
        ButterKnife.bind(this);
        SystemUtil.setTitleMode(getWindow());
        titleView.setLeftClickListener(new TitleView.LeftClickListener() {
            @Override
            public void OnLeftButtonClick() {
                finish();
            }
        });
        getLocalData();
    }

    @OnTextChanged(R.id.ed_new_phone)
    void newPhoneTextChange(CharSequence charSequence) {
        changSendStatus("新手机号：" + charSequence, tvNewPhone);
    }

    private void changSendStatus(final String text, final TextView textView) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }

    private void getLocalData() {
        tvOldPhone.setText("原手机号：" + UserAccountUtils.getUserInfo(getApplication()).getPhone());
    }

    @OnClick({R.id.btn_update_phone, R.id.btn_old_phone_send_code, R.id.btn_new_phone_send_code})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_old_phone_send_code:
                sendPhoneCode(UserAccountUtils.getUserInfo(getApplication()).getPhone(),
                        btnOldPhoneSendCode, tvOldPhoneSendStatus);
                break;
            case R.id.btn_new_phone_send_code:
                if (edNewPhone.getText().toString().length() != 11) {
                    showToast("新手机号有误");
                    break;
                }
                sendPhoneCode(edNewPhone.getText().toString(), btnNewPhoneSendCode,
                        tvNewPhoneSendStatus);
                break;
            case R.id.btn_update_phone:
                if (edOldPhoneCode.getText().toString().length() != 6
                        || edNewPhone.getText().toString().length() != 11
                        || edNewPhoneCode.getText().toString().length() != 6) {
                    showToast("验证码或手机位数有误");
                    break;
                }
                startUpdatePhone();
                break;
            default:
                break;
        }
    }

    private void startUpdatePhone() {
        dialog = new AlertDialog.Builder(this)
                .setView(new ProgressBar(this))
                .setCancelable(false)
                .create();
        showProgressBar();

        String oldPhone = UserAccountUtils.getUserInfo(getApplication()).getPhone();
        final String newPhone = edNewPhone.getText().toString();
        String oldCode = edOldPhoneCode.getText().toString();
        final String newCode = edNewPhoneCode.getText().toString();

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add(Api.SMSVerificationBody[0], oldPhone);
        formBody.add(Api.SMSVerificationBody[1], oldCode);
        final Request request = new Request.Builder()
                .header(Api.SMSVerificationHeader[0], Api.SMSVerificationHeader[1])
                .url(Api.SMSVerificationApi)
                .post(formBody.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideProgressBar();
                showToast("验证失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                RequestReturnBean statusBean = new Gson().fromJson(msg, RequestReturnBean.class);
                if (statusBean.status == 0) {
                    checkCodeOnce(newPhone, newCode);
                } else {
                    hideProgressBar();
                    showToast("验证码错误");
                }
            }
        });
    }

    private void checkCodeOnce(final String newPhone, final String newCode) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add(Api.SMSVerificationBody[0], newPhone);
        formBody.add(Api.SMSVerificationBody[1], newCode);
        final Request request = new Request.Builder()
                .header(Api.SMSVerificationHeader[0], Api.SMSVerificationHeader[1])
                .url(Api.SMSVerificationApi)
                .post(formBody.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideProgressBar();
                showToast("验证失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                RequestReturnBean statusBean = new Gson().fromJson(msg, RequestReturnBean.class);
                if (statusBean.status == 0) {
                    requestChange(newPhone);
                } else {
                    hideProgressBar();
                    showToast("验证码错误");
                }
            }
        });
    }

    private void requestChange(String newPhone) {
        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder body = new MultipartBody.Builder();
        body.addFormDataPart(Api.UpDateUserInfoBody[0], UserAccountUtils.getUserInfo
                (getApplication()).getUserId() + "");
        body.addFormDataPart(Api.UpDateUserInfoBody[1], newPhone);
        body.addFormDataPart(Api.UpDateUserInfoBody[2], UserAccountUtils.getUserInfo
                (getApplication()).getEmail());
        body.addFormDataPart(Api.UpDateUserInfoBody[3], UserAccountUtils.getUserInfo
                (getApplication()).getSex());
        final Request request = new Request.Builder()
                .url(Api.UpDateUserInfoApi)
                .post(body.build())
                .header("token", UserAccountUtils.getUserToken(getApplication()).getToken())
                .addHeader(Api.UpDateUserInfoHeader[0], Api.UpDateUserInfoHeader[1])
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideProgressBar();
                showToast("修改失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(UpdatePhoneActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            UserAccountUtils.setUserInfo(newInfo, getApplication());
                            UserAccountUtils.setUserToken(newToken, getApplication());
                        }
                    });
                    return;
                }
                RequestReturnBean bean = new Gson().fromJson(msg, RequestReturnBean.class);
                if (bean.status == 1) {
                    hideProgressBar();
                    showToast("修改失败");
                } else {
                    hideProgressBar();
                    showToast("修改成功");
                    ActivityUtils.removeAllActivity(UpdatePhoneActivity.this, LoginActivity.class);
                }
            }
        });
    }

    private void hideProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.hide();
                }
            }
        });
    }

    private void showProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.show();
                }
            }
        });
    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UpdatePhoneActivity.this, text, Toast.LENGTH_SHORT).show();
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
