package com.pdking.convenientmeeting.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.db.RoomOfMeetingMessage;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.fragment.DayMeetingListFragment;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.PopMenu;
import com.pdking.convenientmeeting.weight.PopMenuItem;
import com.pdking.convenientmeeting.weight.TitleView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MeetingRoomDetailsActivity extends AppCompatActivity {

    @BindView(R.id.vp_day_list)
    ViewPager vpDayList;
    @BindView(R.id.tl_tab)
    TabLayout tlTab;
    @BindView(R.id.title)
    TitleView titleView;
    @BindView(R.id.iv_room_text_background)
    ImageView ivRoomTextBackground;
    @BindView(R.id.tv_room_name)
    TextView tvRoomName;
    @BindView(R.id.tv_room_status)
    TextView tvRoomStatus;
    @BindView(R.id.rv_room_capacity)
    TextView tvRoomCapacity;
    @BindView(R.id.tv_recent_time)
    TextView tvRoomRecentTime;
    @BindView(R.id.fab_book)
    FloatingActionButton fabBook;

    private FragmentPagerAdapter fragmentPagerAdapter;
    private List<DayMeetingListFragment> fragmentList;
    private String[] titles = {"前天", "昨天", "今天", "明天", "后天"};
    private PopMenu mPopMenu;
    private int meetingRoomId;
    private ProgressDialog dialog;
    private UserToken userToken;
    private UserInfo userInfo;
    private String roomNumber;
    private int roomStatus;
    private int roomContent;
    private String roomId;
    private List<RoomOfMeetingMessage> allMeetingList;
    private Bitmap newBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_meeting_room_details);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        roomNumber = getIntent().getStringExtra("roomNumber");
        roomContent = getIntent().getIntExtra("content", -1);
        roomStatus = getIntent().getIntExtra("status", -1);
        meetingRoomId = getIntent().getIntExtra("meetingRoomId", -1);
        Log.d("Lpp", "onCreate:meetingRoomId " + meetingRoomId);
        Log.d("Lpp", "onCreate:roomStatus " + roomStatus);

        switch (roomStatus) {
            case 1:
                changeTextViewText(tvRoomStatus, "状态：空闲");
                break;
            case 2:
                changeTextViewText(tvRoomStatus, "状态：正在使用");
                break;
            case 3:
                changeTextViewText(tvRoomStatus, "状态：维护");
                break;
        }
        changeTextViewText(tvRoomCapacity, "可容纳人数：" + roomContent);
        changeTextViewText(tvRoomRecentTime, "最近一次使用：昨天");
        titleView.setTitleText(roomNumber);
        tvRoomName.setText(roomNumber);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("加载中");
        dialog.setMessage("正在加载...");
        init();
        initPagerAndTab();
        requestData();
    }

    private void requestData() {
        userInfo = LitePal.findAll(UserInfo.class).get(0);
        userToken = LitePal.findAll(UserToken.class).get(0);
        allMeetingList = LitePal.findAll(RoomOfMeetingMessage.class);
    }

    private void changeTextViewText(final TextView tv, final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(text);
            }
        });
    }

    private int getRelativeData(long startTime) {
        int today;
        int startDay;
        Calendar cale = Calendar.getInstance();
        cale.setTime(new Date(System.currentTimeMillis()));
        today = cale.get(Calendar.DAY_OF_YEAR);
        cale.setTime(new Date(startTime));
        startDay = cale.get(Calendar.DAY_OF_YEAR);
        return startDay - today;
    }


    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MeetingRoomDetailsActivity.this, text, Toast.LENGTH_SHORT).show();
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

    public Bitmap zoomImg() {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.room_background);
        float newHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200f,
                getResources().getDisplayMetrics()) + 2;
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int newWidth = outMetrics.widthPixels;
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        newBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        bm.recycle();
        bm = null;
        return newBitmap;
    }

    private void init() {
        Glide.with(this).load(zoomImg())
                .apply(bitmapTransform(new BlurTransformation(15, 3)))
                .into(ivRoomTextBackground);
        final WindowManager.LayoutParams wl = getWindow().getAttributes();
        mPopMenu = new PopMenu(this);
        ArrayList<PopMenuItem> items = new ArrayList<>();
        items.add(new PopMenuItem(0, 0, "报修"));
        items.add(new PopMenuItem(1, 0, "历史会议"));
        mPopMenu.setCornerVisible(true);
        mPopMenu.addItems(items);
        mPopMenu.getmPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                wl.alpha = 1f;
                getWindow().setAttributes(wl);
            }
        });
        mPopMenu.setOnItemSelectedListener(new PopMenu.OnItemSelectedListener() {
            @Override
            public void selected(View view, PopMenuItem item, int position) {
                switch (item.id) {
                    case 0:
                        Toast.makeText(MeetingRoomDetailsActivity.this, "报修", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case 1:
                        Toast.makeText(MeetingRoomDetailsActivity.this, "历史会议", Toast
                                .LENGTH_SHORT).show();
                        break;
                }
            }
        });
        titleView.setLeftClickListener(new TitleView.LeftClickListener() {
            @Override
            public void OnLeftButtonClick() {
                finish();
            }
        });
        titleView.setRightClickListener(new TitleView.RightClickListener() {
            @Override
            public void OnRightButtonClick() {
                mPopMenu.showAsDropDown(titleView.getLayoutRight());
                WindowManager.LayoutParams wl = getWindow().getAttributes();
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                wl.alpha = 0.6f;
                getWindow().setAttributes(wl);
            }
        });
    }

    private void initPagerAndTab() {
        fragmentList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            DayMeetingListFragment fragment = DayMeetingListFragment.newInstance("暂无数据", i + "");
            fragmentList.add(fragment);
        }
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragmentList.get(i);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        };
        vpDayList.setOffscreenPageLimit(4);
        vpDayList.setAdapter(fragmentPagerAdapter);
        tlTab.setupWithViewPager(vpDayList);
        tlTab.getTabAt(2).select();
    }

    @OnClick(R.id.fab_book)
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_book:
                Intent intent = new Intent(this, BookRoomDetailActivity.class);
                intent.putExtra("roomNumber", roomNumber);
                intent.putExtra("meetingRoomId", meetingRoomId);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.hide();
            }
            dialog.dismiss();
        }
        if (newBitmap != null) {
            newBitmap.recycle();
            newBitmap = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK && data != null) {
                    int len = data.getIntExtra("dateLen", -1);
                    if (len != -1) {
                        fragmentList.get(len + 1).notifyDataChanged();
                    }
                }
        }
    }
}
