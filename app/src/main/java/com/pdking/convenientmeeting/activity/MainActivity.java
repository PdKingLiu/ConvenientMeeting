package com.pdking.convenientmeeting.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.utils.ActivityUtils;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.LoginBean;
import com.pdking.convenientmeeting.db.UserAccount;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.fragment.MeetingFragment;
import com.pdking.convenientmeeting.fragment.MeetingRoomFragment;
import com.pdking.convenientmeeting.fragment.MineFragment;
import com.pdking.convenientmeeting.fragment.RecordFragment;
import com.pdking.convenientmeeting.fragment.VideoFragment;
import com.pdking.convenientmeeting.service.RemindMeetingStartService;
import com.pdking.convenientmeeting.service.VoteRemindService;
import com.pdking.convenientmeeting.utils.OkHttpUtils;
import com.pdking.convenientmeeting.utils.PollUtils;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.utils.UserAccountUtils;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    final private int UPDATE_USER_DATA = 1;
    @BindView(R.id.bnv)
    BottomNavigationView mBottomNavigationView;
    private String TAG = "Lpp";
    private int bottomFlag = -1;

    private FragmentManager mFragmentManager;

    private MeetingFragment mMeetingFragment;

    private VideoFragment mVideoFragment;

    private RecordFragment mRecordFragment;

    private MineFragment mMineFragment;

    private ProgressDialog dialog;

    private LoginBean loginInfo;

    private Snackbar snackbar;

    private File iconFile;

    private long exitTime = 0;

    private int where = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        where = getIntent().getIntExtra("where", -1);
        mFragmentManager = getSupportFragmentManager();
        bottomNavigationViewListener();
        init();
        initFragment();
        initUser();
    }

    private void initPoll() {
        PollUtils.startPoll(this, RemindMeetingStartService.class, 10);
    }

    private void init() {
        snackbar = Snackbar.make(mBottomNavigationView, "再按一次退出", Snackbar.LENGTH_SHORT);
        snackbar.setDuration(2000);
    }

    private void initUser() {
        List<UserAccount> accounts = LitePal.findAll(UserAccount.class);
        if (where == 1) {
            loadDate();
        } else if (accounts.size() != 0) {
            UserAccount account = accounts.get(0);
            dialog = new ProgressDialog(this);
            dialog.setMessage("正在登录...");
            dialog.setTitle("登录中");
            dialog.setCancelable(false);
            OkHttpClient okHttpClient = new OkHttpClient();
            FormBody.Builder body = new FormBody.Builder();
            body.add(Api.LoginBody[0], account.getPhone());
            body.add(Api.LoginBody[1], account.getPassword());
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
                    showToast("连接登录失败,请重新登录");
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    hideProgressBar();
                    String message = response.body().string();
                    loginInfo = new Gson().fromJson(message, LoginBean.class);
                    if (loginInfo != null && loginInfo.status == 1) {
                        showToast("密码错误,请重新登录");
                        ActivityUtils.removeAllActivity(MainActivity.this, LoginActivity.class);
                    } else {
                        showToast("登录成功");
                        UserToken userToken = new UserToken(loginInfo.msg);
                        userToken.save();
                        loginInfo.data.save();
                        UserAccountUtils.setUserToken(userToken, getApplication());
                        UserAccountUtils.setUserInfo(loginInfo.data, getApplication());
                        loadDate();
                        initPoll();
                        initWebSocket();
                    }
                }
            });
        } else {
            ActivityUtils.removeAllActivity(MainActivity.this, LoginActivity.class);
        }
    }

    private void loadDate() {
        File file = new File(getExternalFilesDir(null), "/user/userIcon");
        if (!file.exists()) {
            file.mkdirs();
        }
        iconFile = new File(file, "user_icon_clip_"
                + UserAccountUtils.getUserInfo(getApplication()).getPhone() + ".jpg");
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof MeetingRoomFragment) {
                Log.d(TAG, "autoRefresh: ");
                ((MeetingRoomFragment) fragment).autoRefresh();
            }
        }
        Request request = new Request.Builder()
                .url(UserAccountUtils.getUserInfo(getApplication()).avatarUrl)
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Lpp", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = null;
                byte[] bytes = new byte[1024];
                FileOutputStream fileOutputStream = null;
                long current = 0;
                int len;
                try {
                    long total = response.body().contentLength();
                    inputStream = response.body().byteStream();
                    fileOutputStream = new FileOutputStream(iconFile);
                    while ((len = inputStream.read(bytes)) != -1) {
                        current += len;
                        fileOutputStream.write(bytes, 0, len);
                    }
                    fileOutputStream.flush();
                    inputStream.close();
                    fileOutputStream.close();
                } catch (Exception e) {
                    Log.d("Lpp", "onResponse: " + e.getMessage());
                }
            }
        });
        if (getIntent().getIntExtra("isComePoll", -1) == 1) {
            Intent intent = new Intent(this, MeetingDetailsActivity.class);
            intent.putExtra("meetingId", getIntent().getStringExtra("meetingId"));
            startActivity(intent);
        }
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
                    case R.id.bnv_video:
                        if (bottomFlag == R.id.bnv_video) {
                            break;
                        }
                        setFragmentPage(R.id.bnv_video);
                        bottomFlag = R.id.bnv_video;
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
            case R.id.bnv_video:
                if (mVideoFragment == null) {
                    mVideoFragment = VideoFragment.getINSTANCE();
                    fragmentTransaction.add(R.id.fl_main, mVideoFragment);
                } else {
                    fragmentTransaction.show(mVideoFragment);
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

    private void hideFragmentPage() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        switch (bottomFlag) {
            case R.id.bnv_meet:
                if (mMeetingFragment != null && !mMeetingFragment.isHidden()) {
                    fragmentTransaction.hide(mMeetingFragment);
                }
                break;
            case R.id.bnv_video:
                if (mVideoFragment != null && !mVideoFragment.isHidden()) {
                    fragmentTransaction.hide(mVideoFragment);
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
    protected void onDestroy() {
        super.onDestroy();
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            fragment = null;
        }
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.hide();
            }
            dialog.dismiss();
        }
        if (snackbar != null) {
            snackbar.dismiss();
        }
        snackbar = null;
    }

    private void initWebSocket() {
        Intent intent = new Intent(this, VoteRemindService.class);
        startService(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent
            data) {
        IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
        if (result != null) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment f : fragments) {
                if (f instanceof MeetingFragment) {
                    ((MeetingFragment) f).onActivityResult(requestCode, resultCode,
                            data);
                }
            }
        }
        switch (requestCode) {
            case UPDATE_USER_DATA:
                if (resultCode == RESULT_OK && data != null) {
                    List<Fragment> fragments = getSupportFragmentManager().getFragments();
                    for (Fragment f : fragments) {
                        if (f instanceof MineFragment) {
                            ((MineFragment) f).onActivityResult(requestCode, resultCode,
                                    data);
                        }
                        if (f instanceof RecordFragment) {
                            ((RecordFragment) f).onActivityResult(requestCode, resultCode,
                                    data);
                        }
                    }
                }
        }
    }

    @Override
    public void onBackPressed() {
        long len = System.currentTimeMillis() - exitTime;
        if (len < 2000) {
            finish();
        } else {
            snackbar.show();
            exitTime = System.currentTimeMillis();
        }
    }

}
