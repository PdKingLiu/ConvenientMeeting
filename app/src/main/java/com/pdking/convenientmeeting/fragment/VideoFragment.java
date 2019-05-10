package com.pdking.convenientmeeting.fragment;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.utils.OkHttpUtils;
import com.pdking.convenientmeeting.utils.UIUtils;
import com.pdking.convenientmeeting.utils.UserAccountUtils;
import com.pdking.convenientmeeting.weight.AddVideoDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment implements View.OnClickListener {

    private static VideoFragment INSTANCE;

    private TabLayout mTabLayout;

    private ViewPager mViewPager;

    private String[] strings = {"正在进行", "历史会议"};

    private List<Fragment> fragmentList;

    private FragmentPagerAdapter pagerAdapter;

    private FloatingActionButton fabAddVideo;

    private boolean[] isFirst = {false, true};

    public static VideoFragment getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new VideoFragment();
        }
        return INSTANCE;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_video, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPagerAndTabLayout();
    }

    private void initView(View view) {
        mTabLayout = view.findViewById(R.id.tl_title);
        mViewPager = view.findViewById(R.id.vp_video_content);
        fabAddVideo = view.findViewById(R.id.fab_add_video);
        fabAddVideo.setOnClickListener(this);
    }

    private void initPagerAndTabLayout() {
        fragmentList = new ArrayList<>();
        fragmentList.add(VideoNowFragment.newInstance());
        fragmentList.add(VideoHistoryFragment.newInstance());
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
            if (i == 0) {
                tab.select();
                tab.getCustomView().findViewById(R.id.tv_tab).setSelected(true);
                ((TextView) tab.getCustomView().findViewById(R.id.tv_tab)).setTextSize(22);
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
                    ((VideoNowFragment) fragmentList.get(0)).autoRefresh();
                    isFirst[0] = false;
                }
                break;
            case 1:
                if (isFirst[1]) {
                    ((VideoHistoryFragment) fragmentList.get(1)).autoRefresh();
                    isFirst[1] = false;
                }
                break;
        }
    }


    private void updateTabView(TabLayout.Tab tab, boolean b) {
        if (b) {
            if (tab != null) {
                tab.select();
                ((TextView) tab.getCustomView().findViewById(R.id.tv_tab)).setTextSize(22);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_video:
                addVideoMeeting(v);
                break;
        }
    }

    private void addVideoMeeting(View v) {
        AddVideoDialog dialog = new AddVideoDialog(getContext(), R.style.DialogTheme);
        dialog.setListener(new AddVideoDialog.OnClickListener() {
            @Override
            public void onClick(String room, String password) {
                addRoom(room, password);
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }

    private void addRoom(String room, String password) {
        if (room.equals("") || password.equals("")) {
            UIUtils.showToast(getActivity(), "输入有误");
            return;
        }
        if (room.length() < 2 || password.length() < 6) {
            UIUtils.showToast(getActivity(), "输入长度有误");
            return;
        }
        if (UserAccountUtils.getUserInfo(getActivity().getApplication()) == null
                || UserAccountUtils.getUserToken(getActivity().getApplication()) == null) {
            UIUtils.showToast(getActivity(), "未知错误");
            return;
        }
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.AddVideoBody[0], room);
        body.add(Api.AddVideoBody[1], password);
        body.add(Api.AddVideoBody[2], String.valueOf(UserAccountUtils.getUserInfo(getActivity()
                .getApplication()).getUserId()));
        Request request = new Request.Builder()
                .url(Api.AddVideoApi)
                .post(body.build())
                .header(Api.AddVideoHeader[0], Api.AddVideoHeader[1])
                .addHeader("token", UserAccountUtils.getUserToken(getActivity().getApplication())
                        .getToken())
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtils.showToast(getActivity(), "网络错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Log.d("Lpp", "onResponse: " + msg);
            }
        });
    }
}
