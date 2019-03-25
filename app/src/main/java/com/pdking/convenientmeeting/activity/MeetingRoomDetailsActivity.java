package com.pdking.convenientmeeting.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.fragment.DayMeetingListFragment;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.PopMenu;
import com.pdking.convenientmeeting.weight.PopMenuItem;
import com.pdking.convenientmeeting.weight.TitleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    private FragmentPagerAdapter fragmentPagerAdapter;
    private List<DayMeetingListFragment> fragmentList;
    private String[] titles = {"前天", "昨天", "今天", "明天", "后天"};
    private PopMenu mPopMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_meeting_room_details);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        init();
        initPagerAndTab();
    }

    private void init() {
        Glide.with(this).load(R.mipmap.room_background)
                .apply(bitmapTransform(new BlurTransformation(18, 3)))
                .into(ivRoomTextBackground);
        final WindowManager.LayoutParams wl =  getWindow().getAttributes();
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
                        Toast.makeText(MeetingRoomDetailsActivity.this, "报修", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(MeetingRoomDetailsActivity.this, "历史会议", Toast.LENGTH_SHORT).show();
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
                WindowManager.LayoutParams wl =  getWindow().getAttributes();
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                wl.alpha = 0.6f;
                getWindow().setAttributes(wl);
            }
        });
    }

    private void initPagerAndTab() {
        fragmentList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            DayMeetingListFragment fragment = DayMeetingListFragment.newInstance(titles[i]);
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
        vpDayList.setAdapter(fragmentPagerAdapter);
        tlTab.setupWithViewPager(vpDayList);
        tlTab.getTabAt(2).select();
    }
}
