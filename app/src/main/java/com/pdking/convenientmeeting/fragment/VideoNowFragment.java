package com.pdking.convenientmeeting.fragment;


import android.content.Intent;
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
import android.widget.RelativeLayout;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.activity.MeetingDetailsActivity;
import com.pdking.convenientmeeting.adapter.MeetingMineAdapter;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.MeetingMessage;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.OkHttpUtils;
import com.pdking.convenientmeeting.utils.UserAccountUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

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
public class VideoNowFragment extends Fragment implements View.OnClickListener {

    private static VideoNowFragment videoNowFragment;

    private SmartRefreshLayout refreshLayout;
    private RelativeLayout rlHaveNothing;
    private RecyclerView recyclerView;

    private List<MeetingMessage> beanList;

    private MeetingMineAdapter mineAdapter;


    public static VideoNowFragment newInstance() {
        if (videoNowFragment == null) {
            videoNowFragment = new VideoNowFragment();
        }
        return videoNowFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_video_now, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        refreshLayout = view.findViewById(R.id.srl_flush);
        recyclerView = view.findViewById(R.id.rv_now_video);
        rlHaveNothing = view.findViewById(R.id.rl_have_nothing);
        rlHaveNothing.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initList();
        initRecyclerAndFlush();
        refreshLayout.autoRefresh();
    }

    private void initList() {
        beanList = new ArrayList<>();
        if (beanList.size() == 0) {
            rlHaveNothing.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            rlHaveNothing.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_have_nothing:
                autoRefresh();
                break;
        }
    }

    public void autoRefresh() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshLayout.autoRefresh();
            }
        });
    }

    private void initRecyclerAndFlush() {
        mineAdapter = new MeetingMineAdapter(beanList, getContext());
        mineAdapter.setListener(new MeetingMineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getContext(), MeetingDetailsActivity.class);
                intent.putExtra("meetingId", beanList.get(position).meetingId + "");
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mineAdapter);
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refresh();
            }
        });
    }

    private void refresh() {
        if (UserAccountUtils.getUserToken(getActivity().getApplication()) == null) {
            refreshLayout.finishRefresh(false);
            return;
        }
        final Request request = new Request.Builder()
                .header(Api.GetVideoListHeader[0], Api.GetVideoListHeader[1])
                .addHeader("token", UserAccountUtils.getUserToken(getActivity().getApplication())
                        .getToken())
                .url(Api.GetVideoListApi)
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                refreshLayout.finishRefresh(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Log.d("Lpp", "VideoList: " + msg);
                if (msg.contains("token过期")) {
                    LoginStatusUtils.stateFailure(getActivity(), new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            UserAccountUtils.getUserToken(getActivity().getApplication())
                                    .setToken(newToken.getToken());
                            UserAccountUtils.setUserInfo(newInfo, getActivity().getApplication());
                        }
                    });
                    refreshLayout.finishRefresh(false);
                    return;
                }
                refreshLayout.finishRefresh(true);
            }
        });
    }

    private void notifyDataChanged() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mineAdapter != null && beanList != null) {
                    if (beanList.size() == 0) {
                        rlHaveNothing.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        rlHaveNothing.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        mineAdapter.notifyDataSetChanged();
                    }
                } else {
                    rlHaveNothing.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

}
