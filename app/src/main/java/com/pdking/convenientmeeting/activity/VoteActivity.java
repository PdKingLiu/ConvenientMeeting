package com.pdking.convenientmeeting.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.adapter.VoteAdapter;
import com.pdking.convenientmeeting.db.VoteTest;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.TitleView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VoteActivity extends AppCompatActivity implements TitleView.LeftClickListener,
        TitleView.RightClickListener {

    @BindView(R.id.title)
    TitleView title;
    @BindView(R.id.srl_flush)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.rl_vote_list)
    RecyclerView recyclerView;

    private VoteAdapter adapter;
    private List<VoteTest> voteList;

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
        initData();
    }

    private void initData() {
        voteList.add(new VoteTest());
        voteList.add(new VoteTest());
        voteList.add(new VoteTest());
        adapter.notifyDataSetChanged();
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
        smartRefreshLayout.finishRefresh();
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
