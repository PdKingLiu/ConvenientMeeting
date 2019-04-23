package com.pdking.convenientmeeting.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.adapter.VoteAdapter;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.db.VoteListBean;
import com.pdking.convenientmeeting.db.VoteTest;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.OkHttpUtils;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.utils.UIUtils;
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
import okhttp3.Request;
import okhttp3.Response;

public class VoteActivity extends AppCompatActivity implements TitleView.LeftClickListener,
        TitleView.RightClickListener {

    @BindView(R.id.title)
    TitleView title;
    @BindView(R.id.srl_flush)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.rl_vote_list)
    RecyclerView recyclerView;

    private VoteAdapter adapter;
    private List<VoteListBean.VoteBean> voteList;

    private String meetingId;
    private String userId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_vote);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        meetingId = getIntent().getStringExtra("meetingId");
        userId = getIntent().getStringExtra("userId");
        token = getIntent().getStringExtra("token");
        title.setLeftClickListener(this);
        title.setRightClickListener(this);
        initPate();
        smartRefreshLayout.autoRefresh();
    }

    private void initPate() {
        voteList = new ArrayList<>();
        adapter = new VoteAdapter(voteList, this);
        adapter.setItemClickListener(new VoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(VoteActivity.this, VoteDetailsActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                dataRefresh();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getIntExtra("result", -1) == 1) {
                    smartRefreshLayout.autoRefresh();
                }
            }
        }
    }

    private void dataRefresh() {
        Request request = new Request.Builder()
                .url(Api.GetVoteListApi + "?" + Api.GetVoteListBody[0] + "=" + meetingId + "&" + Api
                        .GetVoteListBody[1] + "=" + userId)
                .header("token", token)
                .get()
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtils.showToast(VoteActivity.this, "加载失败");
                smartRefreshLayout.finishRefresh(false);
                Log.d("Lpp", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Log.d("Lpp", "onResponse: " + msg);
                if (msg.contains("token过期")) {
                    LoginStatusUtils.stateFailure(VoteActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            token = newToken.getToken();
                        }
                    });
                    smartRefreshLayout.finishRefresh(false);
                    return;
                }
                VoteListBean bean = new Gson().fromJson(msg, VoteListBean.class);
                if (bean == null || bean.status != 0) {
                    smartRefreshLayout.finishRefresh(false);
                } else {
                    smartRefreshLayout.finishRefresh(true);
                    voteList.clear();
                    voteList.addAll(bean.data);
                    Log.d("Lpp", "onResponse: " + voteList.size());
                    notifyDataChanged();
                }
            }
        });
    }

    private void notifyDataChanged() {
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

    @Override
    public void OnRightButtonClick() {
        Intent intent = new Intent(this, ReleaseVoteActivity.class);
        intent.putExtra("meetingId", meetingId);
        intent.putExtra("userId", userId);
        intent.putExtra("token", token);
        startActivityForResult(intent, 1);
    }
}
