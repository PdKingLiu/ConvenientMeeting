package com.pdking.convenientmeeting.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.adapter.MeetingMemberAdapter;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.MeetingByIdMessage;
import com.pdking.convenientmeeting.db.MeetingByIdMessageBean;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.utils.UIUtils;
import com.pdking.convenientmeeting.utils.UserAccountUtils;
import com.pdking.convenientmeeting.weight.TitleView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ScanMeetingMemberListActivity extends AppCompatActivity implements TitleView
        .LeftClickListener {

    @BindView(R.id.title)
    TitleView title;
    @BindView(R.id.srl_flush)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.rv_member_list)
    RecyclerView recyclerView;
    private String meetingId;
    private String masterId;
    private List<MeetingByIdMessage.MemberStatusBean> list;
    private MeetingMemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_scan_meeting_member_list);
        ButterKnife.bind(this);
        SystemUtil.setTitleMode(getWindow());
        title.setLeftClickListener(this);
        meetingId = getIntent().getStringExtra("meetingId");
        masterId = getIntent().getStringExtra("masterId");
        initData();
    }

    private void initData() {
        list = new ArrayList<>();
        adapter = new MeetingMemberAdapter(this, list, Integer.parseInt(masterId));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refresh();
            }
        });
        smartRefreshLayout.autoRefresh();
    }

    private void refresh() {
        OkHttpClient client = new OkHttpClient();
        final FormBody.Builder body = new FormBody.Builder();
        body.add(Api.GetMeetingByIdBody[0], meetingId);
        Request request = new Request.Builder()
                .header(Api.GetMeetingByIdHeader[0], Api.GetMeetingByIdHeader[1])
                .addHeader("token", UserAccountUtils.getUserToken(getApplication()).getToken())
                .url(Api.GetMeetingByIdApi)
                .post(body.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                smartRefreshLayout.finishRefresh(false);
                UIUtils.showToast(ScanMeetingMemberListActivity.this, "加载失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(ScanMeetingMemberListActivity.this, new
                            LoginCallBack() {
                                @Override
                                public void newMessageCallBack(UserInfo newInfo,
                                                               UserToken newToken) {
                                    UserAccountUtils.setUserInfo(newInfo, getApplication());
                                    UserAccountUtils.setUserToken(newToken, getApplication());
                                }
                            });
                    smartRefreshLayout.finishRefresh(false);
                    return;
                }
                MeetingByIdMessageBean bean = new Gson().fromJson(msg, MeetingByIdMessageBean
                        .class);
                if (bean != null && bean.status == 0 && bean.data != null && bean.data
                        .memberStatus != null && bean.data.memberStatus.size() != 0) {
                    smartRefreshLayout.finishRefresh(true);
                    list.clear();
                    list.addAll(bean.data.memberStatus);
                    notifyDataSetChanged();
                } else {
                    smartRefreshLayout.finishRefresh(false);
                }
            }
        });

    }

    private void notifyDataSetChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void OnLeftButtonClick() {
        finish();
    }
}
