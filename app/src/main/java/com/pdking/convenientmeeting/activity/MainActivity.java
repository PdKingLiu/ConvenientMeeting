package com.pdking.convenientmeeting.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.fragment.MeetingFragment;
import com.pdking.convenientmeeting.fragment.MineFragment;
import com.pdking.convenientmeeting.fragment.RecordFragment;
import com.pdking.convenientmeeting.utils.SystemUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private String TAG = "Lpp";

    @BindView(R.id.bnv)
    BottomNavigationView mBottomNavigationView;

    private int bottomFlag = -1;

    private FragmentManager mFragmentManager;

    private MeetingFragment mMeetingFragment;

    private RecordFragment mRecordFragment;

    private MineFragment mMineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        mFragmentManager = getSupportFragmentManager();
        initFragment();
        bottomNavigationViewListener();
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
                if (mMeetingFragment!=null&&!mMeetingFragment.isHidden()) {
                    fragmentTransaction.hide(mMeetingFragment);
                    Log.d(TAG, "hideFragmentPage: ");
                }
                break;
            case R.id.bnv_record:
                if (mRecordFragment!=null&&!mRecordFragment.isHidden()) {
                    fragmentTransaction.hide(mRecordFragment);
                }
                break;
            case R.id.bnv_mine:
                if (mMineFragment!=null&&!mMineFragment.isHidden()) {
                    fragmentTransaction.hide(mMineFragment);
                }
                break;
        }
        fragmentTransaction.commit();
    }
}
