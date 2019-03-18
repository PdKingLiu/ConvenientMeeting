package com.pdking.convenientmeeting.fragment;

import android.content.Context;
import android.content.Intent;
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
import com.pdking.convenientmeeting.activity.MeetingRoomDetailsActivity;
import com.pdking.convenientmeeting.adapter.MeetingRoomAdapter;
import com.pdking.convenientmeeting.db.MeetingRoomBean;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeetingRoomFragment extends Fragment {

    private static MeetingRoomFragment meetingRoomFragment;

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private MeetingRoomAdapter roomAdapter;
    private List<MeetingRoomBean> roomBeanList;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public MeetingRoomFragment() {
    }

    public static MeetingRoomFragment newInstance() {
        if (meetingRoomFragment == null) {
            meetingRoomFragment = new MeetingRoomFragment();
        }
        return meetingRoomFragment;
    }

    public SmartRefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initList();
        initRecycleViewAndRefresh();
    }

    private void initList() {
        roomBeanList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MeetingRoomBean meetingRoomBean = new MeetingRoomBean();
            roomBeanList.add(meetingRoomBean);
        }
    }

    private void initRecycleViewAndRefresh() {
        roomAdapter = new MeetingRoomAdapter(getContext(), roomBeanList);
        roomAdapter.setItemClickListener(new MeetingRoomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(getContext(), MeetingRoomDetailsActivity.class));
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(roomAdapter);
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore();
            }
        });
    }

    public void autoRefresh() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshLayout.autoRefresh();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_meeting_room, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        refreshLayout = view.findViewById(R.id.srl_flush);
        recyclerView = view.findViewById(R.id.rv_room);
    }
}
