package com.pdking.convenientmeeting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.ActivityContainer;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.RequestReturnBean;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.TitleView;

import org.litepal.LitePal;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdatePasswordActivity extends AppCompatActivity {

    @BindView(R.id.title)
    TitleView title;
    @BindView(R.id.ed_old_password)
    EditText edOldPassword;
    @BindView(R.id.ed_new_password)
    EditText edNewPassword;
    @BindView(R.id.btn_update)
    Button btnUpDate;
    @BindView(R.id.cb_password_visibility)
    CheckBox cbPasswordVisibility;

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_update_password);
        SystemUtil.setTitleMode(getWindow());
        ActivityContainer.addActivity(this);
        ButterKnife.bind(this);
        title.setLeftClickListener(new TitleView.LeftClickListener() {
            @Override
            public void OnLeftButtonClick() {
                finish();
            }
        });
        cbPasswordVisibility.setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edOldPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    edNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    edOldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType
                            .TYPE_TEXT_VARIATION_PASSWORD);
                    edNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType
                            .TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    @OnClick(R.id.btn_update)
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update:
                startLogin();
                break;
        }
    }

    private void startLogin() {
        String oldPassword;
        String newPassword;
        oldPassword = edOldPassword.getText().toString();
        newPassword = edNewPassword.getText().toString();
        if (oldPassword.length() < 6 || newPassword.length() < 6) {
            showToast("请输入正确位数的密码（6~16位）");
            return;
        }
        dialog = new AlertDialog.Builder(this)
                .setView(new ProgressBar(this))
                .setCancelable(false)
                .create();
        showProgressBar();

        UserInfo userInfo = LitePal.findAll(UserInfo.class).get(0);
        UserToken userToken = LitePal.findAll(UserToken.class).get(0);

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.UpDateUserPasswordBody[0], userInfo.getUserId() + "");
        body.add(Api.UpDateUserPasswordBody[1], oldPassword);
        body.add(Api.UpDateUserPasswordBody[2], newPassword);
        Request request = new Request.Builder()
                .header(Api.UpDateUserPasswordHeader[0], Api.UpDateUserPasswordHeader[1])
                .addHeader("token", userToken.getToken())
                .post(body.build())
                .url(Api.UpDateUserPasswordApi)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideProgressBar();
                showToast("修改失败");
                Log.d("Lpp", "修改失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideProgressBar();
                String msg = response.body().string();
                Log.d("Lpp", "onResponse: " + msg);
                RequestReturnBean bean = new Gson().fromJson(msg, RequestReturnBean.class);
                if (bean.status == 0) {
                    showToast("修改成功");
                    startActivity(new Intent(UpdatePasswordActivity.this, LoginActivity.class));
                    ActivityContainer.removeAllActivity();
                } else {
                    showToast("修改失败，密码有误");
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
                Toast.makeText(UpdatePasswordActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
