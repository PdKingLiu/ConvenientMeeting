package com.pdking.convenientmeeting.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.adapter.RoomHistoryMeetingAdapter;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.RoomHistoryMeetingMessage;
import com.pdking.convenientmeeting.db.RoomHistoryMeetingMessageBean;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.OkHttpUtils;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.utils.UIUtils;
import com.pdking.convenientmeeting.weight.TitleView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MeetingRoomHistoryMeetingActivity extends AppCompatActivity {

    @BindView(R.id.title)
    TitleView title;
    @BindView(R.id.srl_flush)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.rv_history)
    RecyclerView recyclerView;
    RoomHistoryMeetingAdapter adapter;
    List<RoomHistoryMeetingMessage> list;
    private String token;
    private String roomId;
    private int page = 1;
    private RoomHistoryMeetingMessageBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_meeting_room_history_meeting);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        title.setLeftClickListener(new TitleView.LeftClickListener() {
            @Override
            public void OnLeftButtonClick() {
                finish();
            }
        });
        token = getIntent().getStringExtra("token");
        roomId = getIntent().getStringExtra("roomId");
        initRecyclerView();
    }

    private void initRecyclerView() {
        list = new ArrayList<>();
        adapter = new RoomHistoryMeetingAdapter(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        smartRefreshLayout.setEnableAutoLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refresh();
            }
        });
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMore();
            }
        });
        smartRefreshLayout.autoRefresh();
    }

    private void loadMore() {
        if (bean == null || !bean.data.hasNextPage) {
            smartRefreshLayout.finishLoadMoreWithNoMoreData();
            return;
        }
        page++;
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.GetRoomMeetingListBody[0], roomId);
        body.add("page", page + "");
        Request request = new Request.Builder()
                .url(Api.GetRoomMeetingListApi)
                .addHeader(Api.GetRoomMeetingListHeader[0], Api.GetRoomMeetingListHeader[1])
                .header("token", token)
                .post(body.build())
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                smartRefreshLayout.finishLoadMore(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Log.d("Lpp", "onResponse: " + msg);
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(MeetingRoomHistoryMeetingActivity.this, new
                            LoginCallBack() {
                                @Override
                                public void newMessageCallBack(UserInfo newInfo, UserToken
                                        newToken) {
                                    token = newToken.getToken();
                                }
                            });
                    smartRefreshLayout.finishLoadMore(false);
                    return;
                }
                bean = new Gson().fromJson(msg, RoomHistoryMeetingMessageBean.class);
                if (bean != null && bean.status == 0) {
                    Log.d("Lpp", "onResponse: " + bean.data.list.size());
                    list.addAll(bean.data.list);
                    notifyDataChanged();
                    smartRefreshLayout.finishLoadMore(true);
                } else {
                    smartRefreshLayout.finishLoadMore(false);
                }
            }
        });
    }

    private void refresh() {
        loadData();
    }

    private void loadData() {
        page = 1;
        Log.d("Lpp", "loadData: " + roomId + "-" + token);
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.GetRoomMeetingListBody[0], roomId);
        body.add("page", page + "");
        Request request = new Request.Builder()
                .url(Api.GetRoomMeetingListApi)
                .header(Api.GetRoomMeetingListHeader[0], Api.GetRoomMeetingListHeader[1])
                .addHeader("token", token)
                .post(body.build())
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                smartRefreshLayout.finishRefresh(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Log.d("Lpp", "onResponse: " + msg);
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(MeetingRoomHistoryMeetingActivity.this, new
                            LoginCallBack() {
                                @Override
                                public void newMessageCallBack(UserInfo newInfo, UserToken
                                        newToken) {
                                    token = newToken.getToken();
                                }
                            });
                    smartRefreshLayout.finishRefresh(false);
                    return;
                }
                bean = new Gson().fromJson(msg, RoomHistoryMeetingMessageBean.class);
                if (bean != null && bean.status == 0) {
                    Log.d("Lpp", "onResponse: " + bean.data.list.size());
                    list.clear();
                    list.addAll(bean.data.list);
                    notifyDataChanged();
                    smartRefreshLayout.finishRefresh(true);
                } else {
                    smartRefreshLayout.finishRefresh(false);
                }
            }
        });
    }

    public void notifyDataChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
