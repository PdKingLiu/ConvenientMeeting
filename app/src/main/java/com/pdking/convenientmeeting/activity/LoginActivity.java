package com.pdking.convenientmeeting.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.ActivityContainer;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.LoginBean;
import com.pdking.convenientmeeting.db.UserAccount;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.utils.PermissionUtil;
import com.pdking.convenientmeeting.utils.SystemUtil;

import org.litepal.LitePal;

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

public class LoginActivity extends AppCompatActivity {

    /**
     * 注册按钮
     */
    @BindView(R.id.bt_login_register)
    Button btRegister;
    @BindView(R.id.bt_login_find_password)
    Button btFindPassword;
    @BindView(R.id.btn_login)
    Button btLogin;
    @BindView(R.id.ed_input_phone)
    TextInputEditText edPhone;
    @BindView(R.id.ed_input_password)
    TextInputEditText edPassword;

    private UserInfo userInfo;
    private AlertDialog dialog;
    private boolean[] flag = {false, false};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        ButterKnife.bind(this);
        SystemUtil.setTitleMode(getWindow());
        LitePal.getDatabase();
        btLogin.setEnabled(false);
        dialog = new AlertDialog.Builder(this)
                .setView(new ProgressBar(this))
                .create();
        ActivityContainer.addActivity(this);
        PermissionUtil.applyPermission(this);
        changeText();
    }

    @OnClick({R.id.bt_login_register, R.id.bt_login_find_password, R.id.btn_login})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                startLogin();
                break;
            case R.id.bt_login_register:
                startActivity(new Intent(this, RegisterActivityOne.class));
                break;
            case R.id.bt_login_find_password:
                startActivity(new Intent(this, FindPasswordActivity.class));
                break;
        }
    }

    @OnTextChanged(R.id.ed_input_phone)
    void onPhoneTextChange(CharSequence charSequence) {
        if (charSequence.length() >= 6) {
            flag[0] = true;
        } else {
            flag[0] = false;
        }
        checkButton();
    }

    private void checkButton() {
        if (flag[0] && flag[1]) {
            btLogin.setEnabled(true);
        } else {
            btLogin.setEnabled(false);
        }
    }

    @OnTextChanged(R.id.ed_input_password)
    void onPasswordTextChange(CharSequence charSequence) {
        if (charSequence.length() >= 6) {
            flag[1] = true;
        } else {
            flag[1] = false;
        }
        checkButton();
    }

    private void startLogin() {
        final String phone = edPhone.getText().toString();
        final String password = edPassword.getText().toString();
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.LoginBody[0], phone);
        body.add(Api.LoginBody[1], password);
        Request request = new Request.Builder()
                .url(Api.LoginApi)
                .post(body.build())
                .header(Api.LoginHeader[0], Api.LoginHeader[1])
                .build();
        showProgressBar();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideProgressBar();
                Log.d("Lpp", "onFailure: " + e.getMessage());
                showToast("登录失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideProgressBar();
                String message = response.body().string();
//                Log.d("Lpp", "onResponse: " + message);
                LoginBean loginBean = new Gson().fromJson(message, LoginBean.class);
//                Log.d("Lpp", "onResponse: " + loginBean);
                if (loginBean != null && loginBean.status == 1) {
                    showToast("账号或密码错误");
                } else {
                    LitePal.deleteAll(UserAccount.class);
                    UserAccount account = new UserAccount();
                    account.setPhone(phone);
                    account.setPassword(password);
                    account.save();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userBean", loginBean);
                    startActivity(intent);
                    ActivityContainer.removeAllActivity();
                }

            }
        });
    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, text, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.hide();
            }
            dialog.dismiss();
        }
    }

    private void applyPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager
                .PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
                    .CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    public void changeText() {
        if (LitePal.findAll(UserAccount.class).size() != 0) {
            UserAccount account = LitePal.findAll(UserAccount.class).get(0);
            edPhone.setText(account.getPhone());
            edPassword.setText(account.getPassword());
        }
    }
}
