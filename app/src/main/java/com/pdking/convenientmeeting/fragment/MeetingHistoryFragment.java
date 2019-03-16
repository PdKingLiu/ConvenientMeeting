package com.pdking.convenientmeeting.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.adapter.MeetingHistoryAdapter;
import com.pdking.convenientmeeting.db.MeetingBean;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeetingHistoryFragment extends Fragment {

    private static MeetingHistoryFragment meetingHistoryFragment;

    RecyclerView mRecyclerView;

    SmartRefreshLayout mSmartRefreshLayout;

    private List<MeetingBean> meetingBeanList;

    private MeetingHistoryAdapter mAdapter;


    public MeetingHistoryFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public SmartRefreshLayout getRefreshLayout() {
        return mSmartRefreshLayout;
    }

    public static MeetingHistoryFragment newInstance() {
        if (meetingHistoryFragment == null) {
            meetingHistoryFragment = new MeetingHistoryFragment();
        }
        return meetingHistoryFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initList();
        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new MeetingHistoryAdapter(meetingBeanList, getContext());
        mAdapter.setClickListener(new MeetingHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getContext(), meetingBeanList.get(position).getTv_meeting_name(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        initFlush();
    }

    private void initFlush() {
        mSmartRefreshLayout.setEnableAutoLoadMore(false);
        mSmartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMore();
                mSmartRefreshLayout.finishLoadMore();
            }
        });
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refresh();
                mSmartRefreshLayout.finishRefresh();
            }
        });
    }

    private void refresh() {
        meetingBeanList.clear();
        initList();
        mAdapter.notifyDataSetChanged();
    }

    private void loadMore() {
        initList();
        mAdapter.notifyDataSetChanged();
    }

    private void initList() {
        MeetingBean meet = new MeetingBean();
        meet.setTv_meeting_name("便捷会议");
        meet.setTv_user_kind("组织者");
        meet.setTv_people_number("20");
        meet.setTv_user_status("缺勤");
        meet.setTv_place("FZ155");
        meet.setTv_meeting_time_length("90分钟");
        meet.setTv_meeting_master("刘沛栋");
        meet.setTv_meeting_data("2018年11月17日");
        meet.setTv_meeting_time("18:00:00 - 20:00:00");
        meet.setTv_meeting_introduce("关于纳新的会议");
        MeetingBean meet2 = new MeetingBean();
        meet2.setTv_meeting_name("迅速会议");
        meet2.setTv_user_kind("参与者");
        meet2.setTv_people_number("50");
        meet2.setTv_user_status("迟到");
        meet2.setTv_place("FZ156");
        meet2.setTv_meeting_time_length("120分钟");
        meet2.setTv_meeting_master("王舜");
        meet2.setTv_meeting_data("2018年11月12日");
        meet2.setTv_meeting_time("15:00:00 - 23:00:00");
        meet2.setTv_meeting_introduce("谈论、分享会");
        meetingBeanList.add(meet);
        meetingBeanList.add(meet2);
        for (int i = 0; i < 10; i++) {
            MeetingBean meetingBean = new MeetingBean();
            meetingBean.setTv_meeting_name(getRandomString(1));
            meetingBean.setTv_user_kind(getRandomString(1));
            meetingBean.setTv_people_number(getRandomString(1));
            meetingBean.setTv_user_status(getRandomString(1));
            meetingBean.setTv_place(getRandomString(1));
            meetingBean.setTv_meeting_time_length(getRandomString(1));
            meetingBean.setTv_meeting_master(getRandomString(1));
            meetingBean.setTv_meeting_data(getRandomString(1));
            meetingBean.setTv_meeting_time(getRandomString(1));
            meetingBean.setTv_meeting_introduce(getRandomString(1));
            meetingBeanList.add(meetingBean);
        }
    }

    public static String getRandomString(int length) {
        String str = "甲乙丙丁戊abcde";
        Random random = new Random();
        length = random.nextInt(8);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(10);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        meetingBeanList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_meeting_history, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mSmartRefreshLayout = view.findViewById(R.id.srl_flush);
        mRecyclerView = view.findViewById(R.id.rv_history);
    }


}
