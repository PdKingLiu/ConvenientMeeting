package com.pdking.convenientmeeting.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.adapter.MeetingMineAdapter;
import com.pdking.convenientmeeting.adapter.MeetingRoomAdapter;
import com.pdking.convenientmeeting.db.MeetingRoomBean;
import com.pdking.convenientmeeting.db.MineMeetingBean;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class MeetingMineFragment extends Fragment {

    private static MeetingMineFragment meetingMineFragment;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private MeetingMineAdapter mineAdapter;
    private List<MineMeetingBean> beanList;

    public MeetingMineFragment() {
    }

    public static MeetingMineFragment newInstance() {
        if (meetingMineFragment == null) {
            meetingMineFragment = new MeetingMineFragment();
        }
        return meetingMineFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initList();
        initRecyclerAndFlush();
    }

    private void initRecyclerAndFlush() {
        mineAdapter = new MeetingMineAdapter(beanList, getContext());
        mineAdapter.setListener(new MeetingMineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
            }
        });
        mineAdapter.setMoreListener(new MeetingMineAdapter.OnMoreClickListener() {
            @Override
            public void onMoreClick(View view, int position) {
                Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mineAdapter);
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(3000);
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(3000);
            }
        });
    }

    private void initList() {
        beanList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MineMeetingBean mineMeetingBean = new MineMeetingBean();
            beanList.add(mineMeetingBean);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_meeting_mine, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        refreshLayout = view.findViewById(R.id.srl_flush);
        recyclerView = view.findViewById(R.id.rv_mine);
    }

}
