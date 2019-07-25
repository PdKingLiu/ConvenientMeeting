package com.pdking.convenientmeeting.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.adapter.LiveMemberAdapter;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.LiveDetailBean;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.OkHttpUtils;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.utils.UIUtils;
import com.pdking.convenientmeeting.utils.UserAccountUtils;
import com.pdking.convenientmeeting.weight.TitleView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class LiveMeetingDetailActivity extends AppCompatActivity implements TitleView
        .LeftClickListener {

    @BindView(R.id.tv_live_name)
    TextView tvName;
    @BindView(R.id.tv_member_count)
    TextView tvCount;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.rv_member_list)
    RecyclerView recyclerView;
    @BindView(R.id.title)
    TitleView title;
    private String liveId;
    private LiveMemberAdapter adapter;
    private List<LiveDetailBean.DataBean.MeetingMembersBean> list;
    private LiveDetailBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_live_meeting_detail);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        liveId = getIntent().getStringExtra("liveId");
        title.setLeftClickListener(this);
        initList();
        loadData();
    }

    private void initList() {
        list = new ArrayList<>();
        adapter = new LiveMemberAdapter(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadData() {
        Log.d("Lpp", "liveId: " + liveId);
        Request request = new Request.Builder()
                .header(Api.GetLiveMessageHeader[0], Api.GetLiveMessageHeader[1])
                .addHeader("token", UserAccountUtils.getUserToken(getApplication()).getToken())
                .get()
                .url(Api.GetLiveMessageApi + "?" + "liveId=" + liveId)
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtils.showToast(LiveMeetingDetailActivity.this, "网络错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Log.d("Lpp", "onResponse: " + msg);
                if (msg.contains("token")) {
                    LoginStatusUtils.stateFailure(LiveMeetingDetailActivity.this, new
                            LoginCallBack() {
                                @Override
                                public void newMessageCallBack(UserInfo newInfo, UserToken
                                        newToken) {
                                    UserAccountUtils.setUserToken(newToken, getApplication());
                                }
                            });
                    return;
                }
                try {
                    bean = new Gson().fromJson(msg, LiveDetailBean.class);
                    if (bean == null || bean.status != 0
                            || bean.data == null || bean.data.meetingMembers == null) {
                        UIUtils.showToast(LiveMeetingDetailActivity.this, "未知错误");
                    } else {
                        changePage();
                    }
                } catch (Exception e) {
                    UIUtils.showToast(LiveMeetingDetailActivity.this, "未知错误");
                }
            }
        });
    }

    private void changePage() {
        list.clear();
        list.addAll(bean.data.meetingMembers);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        runOnUiThread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                tvName.setText(String.valueOf(bean.data.liveName));
                tvCount.setText(String.valueOf("在线人数：" + bean.data.onlineNum + "人"));
                tvStartTime.setText("开始时间：" + simpleDateFormat.format(new Date(bean.data
                        .startTime)));
                tvEndTime.setText("结束时间：" + simpleDateFormat.format(new Date(bean.data.endTime)));
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void OnLeftButtonClick() {
        finish();
    }
}
