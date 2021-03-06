package com.pdking.convenientmeeting.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.pdking.convenientmeeting.R;
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
import com.pdking.convenientmeeting.utils.ActivityUtils;
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
        if (savedInstanceState == null) {
            where = getIntent().getIntExtra("where", -1);
            if (where == 1) {
                init("加载中", "正在加载资源...");
                mFragmentManager = getSupportFragmentManager();
                initFragment();
                bottomNavigationViewListener();
                loadDate();
                showProgressBar();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                        List<Fragment> list = getSupportFragmentManager().getFragments();
                        for (int i = 0; i < list.size(); i++) {
                            Fragment fragment = list.get(i);
                            if (fragment instanceof MeetingRoomFragment) {
                                ((MeetingRoomFragment) fragment).autoRefresh();
                                break;
                            }
                        }
                    }
                }, 2500);
            } else {
                final List<UserAccount> accounts = LitePal.findAll(UserAccount.class);
                if (accounts.size() == 0) {
                    ActivityUtils.removeAllActivity(MainActivity.this, LoginActivity.class);
                } else {
                    init("登录中", "正在登录...");
                    mFragmentManager = getSupportFragmentManager();
                    initFragment();
                    bottomNavigationViewListener();
                    showProgressBar();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            initUser(accounts.get(0));
                        }
                    }, 2000);
                }
            }
        } else {
            mFragmentManager = getSupportFragmentManager();
            bottomNavigationViewListener();
            bottomFlag = savedInstanceState.getInt("bottomFlag", -1);
            mMeetingFragment = (MeetingFragment) getSupportFragmentManager().findFragmentByTag(
                    "meeting");
            mVideoFragment = (VideoFragment) getSupportFragmentManager().findFragmentByTag("video");
            mRecordFragment = (RecordFragment) getSupportFragmentManager().findFragmentByTag(
                    "record");
            mMineFragment = (MineFragment) getSupportFragmentManager().findFragmentByTag("mine");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("bottomFlag", bottomFlag);
    }

    private void initPoll() {
        PollUtils.startPoll(this, RemindMeetingStartService.class, 10);
    }

    private void init(String s1, String s2) {
        if (snackbar == null) {
            snackbar = Snackbar.make(mBottomNavigationView, "再按一次退出", Snackbar.LENGTH_SHORT);
            snackbar.setDuration(2000);
        }
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setMessage(s2);
            dialog.setTitle(s1);
            dialog.setCancelable(false);
        }
    }

    private void initUser(UserAccount account) {
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.LoginBody[0], account.getPhone());
        body.add(Api.LoginBody[1], account.getPassword());
        Request request = new Request.Builder()
                .url(Api.LoginApi)
                .post(body.build())
                .header(Api.LoginHeader[0], Api.LoginHeader[1])
                .build();
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
                    UserAccountUtils.setUserToken(userToken, getApplication());
                    UserAccountUtils.setUserInfo(loginInfo.data, getApplication());
                    List<Fragment> list = getSupportFragmentManager().getFragments();
                    for (int i = 0; i < list.size(); i++) {
                        Fragment fragment = list.get(i);
                        if (fragment instanceof MeetingRoomFragment) {
                            ((MeetingRoomFragment) fragment).autoRefresh();
                        }
                    }
                    loadDate();
                    initPoll();
                    initWebSocket();
                    userToken.save();
                    loginInfo.data.save();
                }
            }
        });
    }

    private void loadDate() {
        File file = new File(getExternalFilesDir(null), "/user/userIcon");
        if (!file.exists()) {
            file.mkdirs();
        }
        iconFile = new File(file, "user_icon_clip_"
                + UserAccountUtils.getUserInfo(getApplication()).getPhone() + ".jpg");
        Request request = new Request.Builder()
                .url(UserAccountUtils.getUserInfo(getApplication()).avatarUrl)
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = null;
                byte[] bytes = new byte[1024];
                FileOutputStream fileOutputStream = null;
                int len;
                try {
                    inputStream = response.body().byteStream();
                    fileOutputStream = new FileOutputStream(iconFile);
                    while ((len = inputStream.read(bytes)) != -1) {
                        fileOutputStream.write(bytes, 0, len);
                    }
                    fileOutputStream.flush();
                    inputStream.close();
                    fileOutputStream.close();
                } catch (Exception e) {
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
        mFragmentTransaction.add(R.id.fl_main, mMeetingFragment, "meeting");
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
                    fragmentTransaction.add(R.id.fl_main, mMeetingFragment, "meeting");
                } else {
                    fragmentTransaction.show(mMeetingFragment);
                }
                break;
            case R.id.bnv_video:
                if (mVideoFragment == null) {
                    mVideoFragment = VideoFragment.getINSTANCE();
                    fragmentTransaction.add(R.id.fl_main, mVideoFragment, "video");
                } else {
                    fragmentTransaction.show(mVideoFragment);
                }
                break;
            case R.id.bnv_record:
                if (mRecordFragment == null) {
                    mRecordFragment = RecordFragment.getINSTANCE();
                    fragmentTransaction.add(R.id.fl_main, mRecordFragment, "record");
                } else {
                    fragmentTransaction.show(mRecordFragment);
                }
                break;
            case R.id.bnv_mine:
                if (mMineFragment == null) {
                    mMineFragment = MineFragment.getINSTANCE();
                    fragmentTransaction.add(R.id.fl_main, mMineFragment, "mine");
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
        if (snackbar == null) {
            snackbar = Snackbar.make(mBottomNavigationView, "再按一次退出", Snackbar.LENGTH_SHORT);
            snackbar.setDuration(2000);
        }
        long len = System.currentTimeMillis() - exitTime;
        if (len < 2000) {
            finish();
        } else {
            snackbar.show();
            exitTime = System.currentTimeMillis();
        }
    }

}
