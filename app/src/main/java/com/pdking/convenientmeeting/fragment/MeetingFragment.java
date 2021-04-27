package com.pdking.convenientmeeting.fragment;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.activity.BookRoomActivity;
import com.pdking.convenientmeeting.activity.MeetingRoomDetailsActivity;
import com.pdking.convenientmeeting.activity.ScanQRActivity;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.OneMeetingRoomMessageBean;
import com.pdking.convenientmeeting.utils.DesUtil;
import com.pdking.convenientmeeting.utils.UIUtils;
import com.pdking.convenientmeeting.utils.UserAccountUtils;
import com.pdking.convenientmeeting.weight.PopMenu;
import com.pdking.convenientmeeting.weight.PopMenuItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author liupeidong
 * Created on 2019/1/30 17:49
 */
public class MeetingFragment extends Fragment {

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
        return new MeetingFragment();
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isFirst1", isFirst[0]);
        outState.putBoolean("isFirst2", isFirst[1]);
        outState.putBoolean("isFirst3", isFirst[2]);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            isFirst[0] = savedInstanceState.getBoolean("isFirst1");
            isFirst[1] = savedInstanceState.getBoolean("isFirst2");
            isFirst[2] = savedInstanceState.getBoolean("isFirst3");
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentList = new ArrayList<>();
        if (savedInstanceState == null) {
            fragmentList.add(MeetingMineFragment.newInstance());
            fragmentList.add(MeetingRoomFragment.newInstance());
            fragmentList.add(MeetingHistoryFragment.newInstance());
        } else {
            List<Fragment> list = getFragmentManager().getFragments();
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j) instanceof MeetingMineFragment) {
                    fragmentList.add(list.get(j));
                }
            }
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j) instanceof MeetingRoomFragment) {
                    fragmentList.add(list.get(j));
                }
            }
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j) instanceof MeetingHistoryFragment) {
                    fragmentList.add(list.get(j));
                }
            }
        }
        initMenu();
        initPagerAndTabLayout();
    }

    private void initPagerAndTabLayout() {
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
        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            tab.setCustomView(R.layout.item_table_item);
            if (i == 1) {
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
                    ((MeetingMineFragment) fragmentList.get(0)).autoRefresh();
                    isFirst[0] = false;
                }
                break;
            case 1:
                if (isFirst[1]) {
                    ((MeetingRoomFragment) fragmentList.get(1)).autoRefresh();
                    isFirst[1] = false;
                }
                break;
            case 2:
                if (isFirst[2]) {
                    ((MeetingHistoryFragment) fragmentList.get(2)).autoRefresh();
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
        IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String roomId;
                try {
                    roomId = DesUtil.talker.decrypt(result.getContents());
                    queryRoomById(roomId);
                } catch (Exception e) {
                    UIUtils.showToast(getActivity(), "二维码异常，请扫描正确的会议室二维码");
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void queryRoomById(String data) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.GetOneMeetingRoomMessageBody[0], data);
        Request request = new Request.Builder()
                .header("token", String.valueOf(UserAccountUtils.getUserToken(getActivity()
                        .getApplication())
                        .getToken()))
                .url(Api.GetOneMeetingRoomMessageApi)
                .post(body.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtils.showToast(getActivity(), "网络错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                OneMeetingRoomMessageBean bean = new Gson().fromJson(msg,
                        OneMeetingRoomMessageBean.class);
                Intent intent = new Intent(getContext(), MeetingRoomDetailsActivity.class);
                intent.putExtra("roomNumber", bean.data.roomNumber);
                intent.putExtra("content", bean.data.content);
                intent.putExtra("status", bean.data.status);
                intent.putExtra("meetingRoomId", bean.data.meetingRoomId);
                startActivity(intent);
            }
        });
    }


}
