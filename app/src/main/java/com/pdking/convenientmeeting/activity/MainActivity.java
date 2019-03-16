package com.pdking.convenientmeeting.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.ActivityContainer;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.LoginBean;
import com.pdking.convenientmeeting.db.UserAccount;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.fragment.MeetingFragment;
import com.pdking.convenientmeeting.fragment.MineFragment;
import com.pdking.convenientmeeting.fragment.RecordFragment;
import com.pdking.convenientmeeting.utils.SystemUtil;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private String TAG = "Lpp";

    @BindView(R.id.bnv)
    BottomNavigationView mBottomNavigationView;

    private int bottomFlag = -1;

    private FragmentManager mFragmentManager;

    private MeetingFragment mMeetingFragment;

    private RecordFragment mRecordFragment;

    private MineFragment mMineFragment;

    private AlertDialog dialog;

    private LoginBean loginBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        SystemUtil.setTitleMode(getWindow());
        applyPermission();
        LitePal.getDatabase();
        ButterKnife.bind(this);
        mFragmentManager = getSupportFragmentManager();
        bottomNavigationViewListener();
        initFragment();
        initUser();
    }


    private void initUser() {
        loginBean = getIntent().getParcelableExtra("userBean");
        if (loginBean == null) {
            List<UserAccount> accountList = LitePal.findAll(UserAccount.class);
            UserAccount account;
            if (accountList == null || accountList.size() == 0) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                dialog = new AlertDialog.Builder(this)
                        .setView(new ProgressBar(this))
                        .setCancelable(false)
                        .create();
                account = accountList.get(0);
                final String phone = account.getPhone();
                final String password = account.getPassword();
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
                        showToast("连接登录失败,请重新登录");
                        finish();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        hideProgressBar();
                        String message = response.body().string();
                        loginBean = new Gson().fromJson(message, LoginBean.class);
                        if (loginBean != null && loginBean.status == 1) {
                            showToast("密码错误,请重新登录");
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            loadDate();
                        }
                    }
                });
            }
        } else {
            loadDate();
        }
    }

    private void loadDate() {
        LitePal.deleteAll(UserToken.class);
        LitePal.deleteAll(UserInfo.class);
        loginBean.data.save();
        UserToken token = new UserToken();
        token.setToken(loginBean.msg);
        token.save();
    }


    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
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


    private void bottomNavigationViewListener() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView
                .OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bnv_meet:
                        if (bottomFlag == R.id.bnv_meet) {
                            break;
                        }
                        setFragmentPage(R.id.bnv_meet);
                        bottomFlag = R.id.bnv_meet;
                        break;
                    case R.id.bnv_record:
                        if (bottomFlag == R.id.bnv_record) {
                            break;
                        }
                        setFragmentPage(R.id.bnv_record);
                        bottomFlag = R.id.bnv_record;
                        break;
                    case R.id.bnv_mine:
                        if (bottomFlag == R.id.bnv_mine) {
                            break;
                        }
                        setFragmentPage(R.id.bnv_mine);
                        bottomFlag = R.id.bnv_mine;
                        break;
                }
                return true;
            }
        });
    }

    private void initFragment() {
        mMeetingFragment = MeetingFragment.getINSTANCE();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.fl_main, mMeetingFragment);
        bottomFlag = R.id.bnv_meet;
        mFragmentTransaction.commit();
    }

    /**
     * 设置当前的页面
     */
    public void setFragmentPage(int fragmentPage) {
        hideFragmentPage();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        switch (fragmentPage) {
            case R.id.bnv_meet:
                if (mMeetingFragment == null) {
                    mMeetingFragment = MeetingFragment.getINSTANCE();
                    fragmentTransaction.add(R.id.fl_main, mMeetingFragment);
                } else {
                    fragmentTransaction.show(mMeetingFragment);
                }
                break;
            case R.id.bnv_record:
                if (mRecordFragment == null) {
                    mRecordFragment = RecordFragment.getINSTANCE();
                    fragmentTransaction.add(R.id.fl_main, mRecordFragment);
                } else {
                    fragmentTransaction.show(mRecordFragment);
                }
                break;
            case R.id.bnv_mine:
                if (mMineFragment == null) {
                    mMineFragment = MineFragment.getINSTANCE();
                    fragmentTransaction.add(R.id.fl_main, mMineFragment);
                } else {
                    fragmentTransaction.show(mMineFragment);
                }
                break;
        }
        fragmentTransaction.commit();
    }


    /**
     * 隐藏之前的Fragment
     */
    private void hideFragmentPage() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        switch (bottomFlag) {
            case R.id.bnv_meet:
                if (mMeetingFragment != null && !mMeetingFragment.isHidden()) {
                    fragmentTransaction.hide(mMeetingFragment);
                    Log.d(TAG, "hideFragmentPage: ");
                }
                break;
            case R.id.bnv_record:
                if (mRecordFragment != null && !mRecordFragment.isHidden()) {
                    fragmentTransaction.hide(mRecordFragment);
                }
                break;
            case R.id.bnv_mine:
                if (mMineFragment != null && !mMineFragment.isHidden()) {
                    fragmentTransaction.hide(mMineFragment);
                }
                break;
        }
        fragmentTransaction.commit();
    }

    @Override
    public Window getWindow() {
        return super.getWindow();
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
}
