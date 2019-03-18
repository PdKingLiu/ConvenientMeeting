package com.pdking.convenientmeeting.fragment;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.activity.BookRoomActivity;
import com.pdking.convenientmeeting.activity.ScanQRActivity;
import com.pdking.convenientmeeting.activity.ScanResultActivity;
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

    private boolean[] isFirst = {true, false, true};

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
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                loadFirstDate(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }


    private void loadFirstDate(int i) {
        switch (i) {
            case 0:
                if (isFirst[0]) {
                    ((MeetingMineFragment) fragmentList.get(0)).getRefreshLayout()
                            .autoRefresh();
                    isFirst[0] = false;
                }
                break;
            case 1:
                if (isFirst[1]) {
                    ((MeetingRoomFragment) fragmentList.get(1)).getRefreshLayout()
                            .autoRefresh();
                    isFirst[1] = false;
                }
                break;
            case 2:
                if (isFirst[2]) {
                    ((MeetingHistoryFragment) fragmentList.get(2)).getRefreshLayout()
                            .autoRefresh();
                    isFirst[2] = false;
                }
                break;
        }
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
                        IntentIntegrator.forSupportFragment(MeetingFragment.this)
                                .setCaptureActivity(ScanQRActivity.class)
                                .setPrompt("")
                                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)// 扫码的类型,
                                // 可选：一维码，二维码，一/二维码
                                .setCameraId(0)// 选择摄像头,可使用前置或者后置
                                .setBeepEnabled(true)// 是否开启声音,扫完码之后会"哔"的一声
                                .initiateScan();// 初始化扫码
                        break;
                    case 1:
                        startActivity(new Intent(getContext(), BookRoomActivity.class));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Lpp", "onActivityResult: " + "MeetingFragment" + requestCode + data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                Toast.makeText(getContext(), "" + result.getContents(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), ScanResultActivity.class);
                intent.putExtra("data", result.getContents());
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
