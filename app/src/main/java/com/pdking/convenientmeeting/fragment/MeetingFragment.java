package com.pdking.convenientmeeting.fragment;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.weight.PopMenu;
import com.pdking.convenientmeeting.weight.PopMenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/1/30 17:49
 */
public class MeetingFragment extends Fragment {

    private static MeetingFragment INSTANCE = null;

    private PopMenu mPopMenu;

    private LinearLayout mLinearLayout;

    private View mView;

    private TabLayout mTabLayout;

    private ViewPager mViewPager;

    private String[] strings = {"我的", "会议室", "历史"};

    private List<Fragment> fragmentList;

    private FragmentPagerAdapter pagerAdapter;

    public static MeetingFragment getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new MeetingFragment();
        }
        return INSTANCE;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.layout_meetingfragment, container, false);
        initView(mView);
        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initMenu();
        initPagerAndTabLayout();
    }

    private void initPagerAndTabLayout() {
        fragmentList = new ArrayList<>();
        fragmentList.add(MeetingMineFragment.newInstance());
        fragmentList.add(MeetingRoomFragment.newInstance());
        fragmentList.add(MeetingHistoryFragment.newInstance());
        pagerAdapter = new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragmentList.get(i);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        };
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            tab.setCustomView(R.layout.item_table_item);
            if (i == 1) {
                tab.select();
                tab.getCustomView().findViewById(R.id.tv_tab).setSelected(true);
                ((TextView) tab.getCustomView().findViewById(R.id.tv_tab)).setTextSize(21);
                ((TextView) tab.getCustomView().findViewById(R.id.tv_tab)).setTypeface(Typeface
                        .defaultFromStyle(Typeface.BOLD));
            }
            ((TextView) tab.getCustomView().findViewById(R.id.tv_tab)).setText(strings[i]);
        }
        mTabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabView(tab, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabView(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initView(View mView) {
        mLinearLayout = mView.findViewById(R.id.ll_pop);
        mTabLayout = mView.findViewById(R.id.tl_title);
        mViewPager = mView.findViewById(R.id.vp_meeting_content);
    }

    private void updateTabView(TabLayout.Tab tab, boolean b) {
        if (b) {
            if (tab != null) {
                tab.select();
                ((TextView) tab.getCustomView().findViewById(R.id.tv_tab)).setTextSize(21);
                ((TextView) tab.getCustomView().findViewById(R.id.tv_tab)).setTypeface(Typeface
                        .defaultFromStyle(Typeface.BOLD));
            }
        } else {
            if (tab != null) {
                ((TextView) tab.getCustomView().findViewById(R.id.tv_tab)).setTextSize(20);
                ((TextView) tab.getCustomView().findViewById(R.id.tv_tab)).setTypeface(Typeface
                        .defaultFromStyle(Typeface.NORMAL));
            }
        }
    }

    private void initMenu() {
        final WindowManager.LayoutParams wl = getActivity().getWindow().getAttributes();
        mPopMenu = new PopMenu(getContext());
        ArrayList<PopMenuItem> items = new ArrayList<>();
        items.add(new PopMenuItem(0, R.mipmap.pop_menu_scan, "扫一扫"));
        items.add(new PopMenuItem(1, R.mipmap.pop_menu_book, "预定会议室"));

        mPopMenu.addItems(items);
        mPopMenu.getmPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                wl.alpha = 1f;
                (getActivity()).getWindow().setAttributes(wl);
            }
        });
        mPopMenu.setOnItemSelectedListener(new PopMenu.OnItemSelectedListener() {
            @Override
            public void selected(View view, PopMenuItem item, int position) {
                switch (item.id) {
                    case 0:
                        Toast.makeText(getContext(), "0", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WindowManager.LayoutParams wl = getActivity().getWindow().getAttributes();
                mPopMenu.showAsDropDown(mLinearLayout);
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                wl.alpha = 0.6f;
                getActivity().getWindow().setAttributes(wl);
            }
        });

    }

}
