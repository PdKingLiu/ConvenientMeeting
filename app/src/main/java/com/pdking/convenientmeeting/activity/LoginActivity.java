package com.pdking.convenientmeeting.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.LoginBean;
import com.pdking.convenientmeeting.db.UserAccount;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.utils.UserAccountUtils;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

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

    private ProgressDialog dialog;
    private AlertDialog dia;
    private boolean[] flag = {false, false};

    private String[] permissicns = new
            String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
            .CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission
            .WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        ButterKnife.bind(this);
        SystemUtil.setTitleMode(getWindow());
        btLogin.setEnabled(false);
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在登录...");
        dialog.setTitle("登录中");
        dialog.setCancelable(false);
        if (!checkPermission()) {
            applyPermission();
        }
        changeText();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0) {
            boolean[] flag = {false, false, false, false, false};
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    flag[i] = true;
                }
            }
            if (!(flag[0] && flag[1] && flag[2] && flag[3] && flag[4])) {
                dia = new AlertDialog.Builder(this)
                        .setTitle("警告")
                        .setMessage("拒绝权限软件将无法使用")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create();
                dia.show();
            }
        }
    }

    public boolean checkPermission() {
        boolean[] flag = {false, false, false, false, false};
        for (int i = 0; i < permissicns.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissicns[i])
                    != PackageManager.PERMISSION_GRANTED) {
                flag[i] = false;
            } else {
                flag[i] = true;
            }
        }
        return flag[0] && flag[1] && flag[2] && flag[3] && flag[4];
    }

    @OnClick({R.id.bt_login_register, R.id.bt_login_find_password, R.id.btn_login})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                showProgressBar();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startLogin();
                    }
                }, 2000);
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
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideProgressBar();
                showToast("登录失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideProgressBar();
                String message = response.body().string();
                LoginBean loginBean = new Gson().fromJson(message, LoginBean.class);
                if (loginBean != null && loginBean.status == 1) {
                    showToast("账号或密码错误");
                } else {
                    LitePal.deleteAll(UserAccount.class);
                    UserAccount account = new UserAccount(phone, password);
                    account.save();

                    LitePal.deleteAll(UserToken.class);
                    UserToken userToken = new UserToken(loginBean.msg);
                    UserAccountUtils.setUserToken(userToken, getApplication());
                    userToken.save();

                    LitePal.deleteAll(UserInfo.class);
                    UserInfo userInfo = loginBean.data;
                    UserAccountUtils.setUserInfo(userInfo, getApplication());
                    userInfo.save();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("where", 1);
                    startActivity(intent);
                    finish();
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
        if (dia != null) {
            if (dia.isShowing()) {
                dia.hide();
            }
            dia.dismiss();
        }
    }

    private void applyPermission() {
        ActivityCompat.requestPermissions(this, permissicns, 1);
    }

    public void changeText() {
        List<UserAccount> list = LitePal.findAll(UserAccount.class);
        if (list.size() != 0) {
            UserAccount account = list.get(0);
            edPhone.setText(account.getPhone());
            edPassword.setText(account.getPassword());
        }
    }
}
