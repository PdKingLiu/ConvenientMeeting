package com.pdking.convenientmeeting.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.MeetingByIdMessage;
import com.pdking.convenientmeeting.db.MeetingByIdMessageBean;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.weight.TitleView;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MeetingDetailsActivity extends AppCompatActivity {

    @BindView(R.id.title)
    TitleView titleView;
    @BindView(R.id.iv_background)
    ImageView ivBackGround;
    @BindView(R.id.tv_people_sum)
    TextView tvPeopleSum;
    @BindView(R.id.tv_meeting_status)
    TextView tvMeetingStatus;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.tv_place)
    TextView tvPlace;
    @BindView(R.id.tv_introduce)
    TextView tvIntroduce;
    @BindView(R.id.btn_add_member)
    Button btnAddMember;
    @BindView(R.id.ll_member_list)
    LinearLayout llMemberList;
    @BindView(R.id.fab_start_or_end)
    FloatingActionButton fabStartOrEnd;

    private String meetingId;
    private UserInfo userInfo;
    private UserToken userToken;
    private MeetingByIdMessageBean bean;
    private String TAG = "Lpp";

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_meeting_details);
        ButterKnife.bind(this);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("加载中");
        dialog.setMessage("正在加载...");
        userInfo = LitePal.findAll(UserInfo.class).get(0);
        userToken = LitePal.findAll(UserToken.class).get(0);
        titleView.setViewUpLineVisible(false);
        titleView.setLeftClickListener(new TitleView.LeftClickListener() {
            @Override
            public void OnLeftButtonClick() {
                finish();
            }
        });
        loadBackground();
        loadPage();
    }

    private void hideProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.show();
                }
            }
        });
    }

    private void showProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.show();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.hide();
            }
            dialog.dismiss();
        }
        super.onDestroy();
    }

    private void loadPage() {
        showProgressBar();
        meetingId = getIntent().getStringExtra("meetingId");
        OkHttpClient client = new OkHttpClient();
        final FormBody.Builder body = new FormBody.Builder();
        body.add(Api.GetMeetingByIdBody[0], meetingId);
        Request request = new Request.Builder()
                .url(Api.GetMeetingByIdApi)
                .header(Api.GetMeetingByIdHeader[0], Api.GetMeetingByIdHeader[1])
                .post(body.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideProgressBar();
                showToast("加载失败");
                Log.d(TAG, "加载失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideProgressBar();
                String msg = response.body().string();
                Log.d(TAG, "onResponse: " + msg);
                bean = new Gson().fromJson(msg, MeetingByIdMessageBean.class);
                if (bean.status == 1) {
                    showToast("加载失败");
                } else {
                    changePage(bean);
                }
            }
        });
    }

    private void changePage(final MeetingByIdMessageBean bean) {
        runOnUiThread(new Runnable() {
            @SuppressLint("RestrictedApi")
            @Override
            public void run() {
                titleView.setTitleText(bean.data.meetingName);
                tvPeopleSum.setText(bean.data.peopleNum + " 人");
                switch (bean.data.status) {
                    case 1:
                        tvMeetingStatus.setText("结束");
                        break;
                    case 2:
                        tvMeetingStatus.setText("正在进行");
                        break;
                    case 3:
                        tvMeetingStatus.setText("未开始");
                        break;
                }
                tvStartTime.setText("开始时间：" + bean.data.startTime);
                tvEndTime.setText("结束时间：" + bean.data.endTime);
                tvPlace.setText(bean.data.roomName);
                tvIntroduce.setText(bean.data.meetingIntro);
                if (userInfo.userId != bean.data.masterId) {
                    btnAddMember.setVisibility(View.GONE);
                    fabStartOrEnd.setVisibility(View.GONE);
                } else {
                    btnAddMember.setVisibility(View.VISIBLE);
                    if (bean.data.status == 3) {
                        fabStartOrEnd.setBackgroundResource(R.mipmap.icon_meeting_start);
                    } else if (bean.data.status == 2) {
                        fabStartOrEnd.setBackgroundResource(R.mipmap.icon_meeting_ending);
                    } else {
                        fabStartOrEnd.setBackgroundResource(R.mipmap.icon_meeting_finish);
                    }
                }
                TextView tv = new TextView(MeetingDetailsActivity.this);
                tv.setText(bean.data.masterName + "（组织者）");
                tv.setTextSize(18);
                tv.setTextColor(Color.WHITE);
                llMemberList.addView(tv);
                for (int i = 0; i < bean.data.memberStatus.size(); i++) {
                    MeetingByIdMessage.MemberStatusBean memberStatusBean = bean.data.memberStatus
                            .get(i);
                    if (memberStatusBean.userId == bean.data.masterId) {
                        continue;
                    }
                    TextView textView = new TextView(MeetingDetailsActivity.this);
                    textView.setText(memberStatusBean.username);
                    textView.setTextSize(18);
                    textView.setTextColor(Color.WHITE);
                    llMemberList.addView(tv);
                }
            }
        });
    }

    private void loadBackground() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://guolin.tech/api/bing_pic")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast("图片加载失败");
                Log.d("Lpp", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String msg = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MeetingDetailsActivity.this).load(msg).into(ivBackGround);
                    }
                });
            }
        });
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
