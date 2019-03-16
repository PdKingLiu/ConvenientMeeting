package com.pdking.convenientmeeting.activity;

import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.LoginBean;
import com.pdking.convenientmeeting.db.UserDataBean;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.weight.TitleView;

import org.litepal.LitePal;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ModificationUserDataActivity extends AppCompatActivity {

    private AlertDialog dialog;

    private UserDataBean dataBean;

    private UserInfo userInfo;

    private UserToken token;

    @BindView(R.id.tv_net_error)
    TextView tvNetError;

    @BindView(R.id.title)
    TitleView title;

    @BindView(R.id.nsv_data_view)
    NestedScrollView nsvDataView;

    @BindView(R.id.civ_user_icon)
    CircleImageView civUserIcon;

    @BindView(R.id.ed_modification_user_name)
    TextView tvUserName;

    @BindView(R.id.tv_modification_sex)
    TextView tvUserSex;

    @BindView(R.id.tv_modification_phone_number)
    TextView tvUserPhone;

    @BindView(R.id.ed_modification_email)
    EditText edUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_modification_user_data);
        ButterKnife.bind(this);
        LitePal.getDatabase();
        dialog = new AlertDialog.Builder(this)
                .setView(new ProgressBar(this))
                .setCancelable(false)
                .create();
        title.setRightClickListener(new TitleView.RightClickListener() {
            @Override
            public void OnRightButtonClick() {
                Toast.makeText(ModificationUserDataActivity.this, "保存", Toast.LENGTH_SHORT).show();
            }
        });
        requestUserData();
    }

    private void requestUserData() {
        userInfo = LitePal.findAll(UserInfo.class).get(0);
        token = LitePal.findAll(UserToken.class).get(0);
        Log.d("Lpp", "requestUserData: " + userInfo);
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.GetUserInfoBody[0], userInfo.getPhone());
        Request request = new Request.Builder()
                .url(Api.GetUserInfoApi)
                .addHeader("token", token.getToken())
                .post(body.build())
                .build();
        showProgressBar();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Lpp", "onFailure: " + e.getMessage());
                hideProgressBar();
                setErrorPage(true);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideProgressBar();
                String msg = response.body().string();
                Log.d("Lpp", "onResponse: " + msg);
                dataBean = new Gson().fromJson(msg, UserDataBean.class);
                if (dataBean.status == 1) {
                    setErrorPage(true);
                } else {
                    setErrorPage(false);
                    setPageData();
                }
            }
        });
    }

    private void setPageData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(ModificationUserDataActivity.this).load(dataBean.data.avatarUrl).into
                        (civUserIcon);
                tvUserName.setText(dataBean.data.username);
                if (dataBean.data.sex.equals("man")) {
                    tvUserSex.setText("男");
                } else {
                    tvUserSex.setText("女");
                }
                tvUserPhone.setText(dataBean.data.phone);
                edUserEmail.setHint(dataBean.data.email);
            }
        });
    }

    private void setErrorPage(final boolean isError) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isError) {
                    title.setRightMenuTextVisible(false);
                    tvNetError.setVisibility(View.VISIBLE);
                    nsvDataView.setVisibility(View.GONE);
                } else {
                    title.setRightMenuTextVisible(true);
                    tvNetError.setVisibility(View.GONE);
                    nsvDataView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ModificationUserDataActivity.this, text, Toast.LENGTH_SHORT).show();
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


}
