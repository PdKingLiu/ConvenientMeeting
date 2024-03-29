package com.pdking.convenientmeeting.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.activity.MeetingRoomDetailsActivity;
import com.pdking.convenientmeeting.adapter.MeetingRoomAdapter;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.AllMeetingRoomMessageBean;
import com.pdking.convenientmeeting.db.OneMeetingRoomMessage;
import com.pdking.convenientmeeting.db.OneMeetingRoomMessageBean;
import com.pdking.convenientmeeting.db.RoomOfMeetingMessage;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.UserAccountUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MeetingRoomFragment extends Fragment implements View.OnClickListener {

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private MeetingRoomAdapter roomAdapter;
    private RelativeLayout rlHaveNothing;
    private List<OneMeetingRoomMessage> roomMessageList;
    private AllMeetingRoomMessageBean roomMessageBean;
    private ProgressDialog dialog;
    private List<RoomOfMeetingMessage> allMeetingList;
    private OneMeetingRoomMessageBean meetingRoomMessageBean;

    public MeetingRoomFragment() {
    }

    public static MeetingRoomFragment newInstance() {
        return new MeetingRoomFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("正在加载...");
        dialog.setTitle("加载中");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initDataAndList();
        initRecycleViewAndRefresh();
    }

    private void initDataAndList() {
        roomMessageList = LitePal.findAll(OneMeetingRoomMessage.class);
        if (roomMessageList.size() == 0) {
            rlHaveNothing.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            rlHaveNothing.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void initRecycleViewAndRefresh() {
        roomAdapter = new MeetingRoomAdapter(roomMessageList);
        roomAdapter.setItemClickListener(new MeetingRoomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                OneMeetingRoomMessage meetingRoomMessage = roomMessageList.get(position);
                enterRoomDetails(meetingRoomMessage);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(roomAdapter);
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                requestRefresh();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                requestLoadMore();
            }
        });
    }

    private void enterRoomDetails(OneMeetingRoomMessage meetingRoomMessage) {
        showProgressBar();
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.GetOneMeetingRoomMessageBody[0], meetingRoomMessage.meetingRoomId + "");
        Request request = new Request.Builder()
                .url(Api.GetOneMeetingRoomMessageApi)
                .header("token", UserAccountUtils.getUserToken(getActivity().getApplication())
                        .getToken())
                .post(body.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideProgressBar();
                showToast("加载失败，请重新尝试");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideProgressBar();
                String msg = response.body().string();
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(getActivity(), new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            UserAccountUtils.setUserInfo(newInfo, getActivity().getApplication());
                            UserAccountUtils.setUserToken(newToken, getActivity().getApplication());
                        }
                    });
                    return;
                }
                meetingRoomMessageBean = new Gson().fromJson(msg, OneMeetingRoomMessageBean.class);
                if (meetingRoomMessageBean.status == 1) {
                    showToast("加载失败，请重新尝试");
                } else {
                    if (meetingRoomMessageBean.data != null && meetingRoomMessageBean.data
                            .recentlyMeetings != null) {
                        allMeetingList = meetingRoomMessageBean.data.recentlyMeetings;
                        LitePal.deleteAll(RoomOfMeetingMessage.class);
                        LitePal.saveAll(allMeetingList);
                        Intent intent = new Intent(getContext(), MeetingRoomDetailsActivity.class);
                        intent.putExtra("status", meetingRoomMessageBean.data.status);
                        intent.putExtra("meetingRoomId", meetingRoomMessageBean.data.meetingRoomId);
                        intent.putExtra("roomNumber", meetingRoomMessageBean.data.roomNumber);
                        intent.putExtra("content", meetingRoomMessageBean.data.content);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void hideProgressBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.hide();
                }
            }
        });
    }

    private void showProgressBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.show();
                }
            }
        });
    }

    private void requestLoadMore() {
        refreshLayout.finishLoadMoreWithNoMoreData();
    }

    private void requestRefresh() {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder body = new FormBody.Builder();
        Request request = new Request.Builder()
                .header("token", UserAccountUtils.getUserToken(getActivity().getApplication())
                        .getToken())
                .url(Api.GetMeetingRoomApi)
                .post(body.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                refreshLayout.finishRefresh(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(getActivity(), new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            UserAccountUtils.setUserInfo(newInfo, getActivity().getApplication());
                            UserAccountUtils.setUserToken(newToken, getActivity().getApplication());
                        }
                    });
                    return;
                }
                roomMessageBean = new Gson().fromJson(msg, AllMeetingRoomMessageBean.class);
                if (roomMessageBean != null && roomMessageBean.status == 0) {
                    refreshLayout.finishRefresh(true);
                    roomMessageList.clear();
                    roomMessageList.addAll(roomMessageBean.data);
                    LitePal.deleteAll(OneMeetingRoomMessage.class);
                    LitePal.saveAll(roomMessageList);
                    notifyDataChanged();
                } else {
                    refreshLayout.finishRefresh(false);
                }
            }
        });
    }

    private void notifyDataChanged() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (roomAdapter != null && roomMessageList != null) {
                    if (roomMessageList.size() == 0) {
                        rlHaveNothing.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        rlHaveNothing.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        roomAdapter.notifyDataSetChanged();
                    }
                } else {
                    rlHaveNothing.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showToast(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void autoRefresh() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.autoRefresh();
                }
            });
        }
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
        rlHaveNothing = view.findViewById(R.id.rl_have_nothing);
        initListener();
    }

    private void initListener() {
        rlHaveNothing.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.hide();
            }
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_have_nothing:
                refreshLayout.autoRefresh();
                break;
        }
    }
}
