package com.pdking.convenientmeeting.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.fragment.DayMeetingListFragment;
import com.pdking.convenientmeeting.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeetingRoomDetailsActivity extends AppCompatActivity {

    @BindView(R.id.vp_day_list)
    ViewPager vpDayList;
    @BindView(R.id.tl_tab)
    TabLayout tlTab;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private List<DayMeetingListFragment> fragmentList;
    private String[] titles = {"前天", "昨天", "今天", "明天", "后天"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_meeting_room_details);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        initPagerAndTab();
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
