package com.pdking.convenientmeeting.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.MeetingByIdMessage;
import com.pdking.convenientmeeting.db.MeetingByIdMessageBean;
import com.pdking.convenientmeeting.db.RequestReturnBean;
import com.pdking.convenientmeeting.db.UserDataBean;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MeetingDetailsActivity extends AppCompatActivity {

    @BindView(R.id.title)
    TitleView titleView;
    @BindView(R.id.rl_vote)
    RelativeLayout rlVote;
    @BindView(R.id.rl_note)
    RelativeLayout rlNote;
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
    @BindView(R.id.tv_member_list)
    TextView tvMemberList;
    @BindView(R.id.fab_start_or_end)
    FloatingActionButton fabStartOrEnd;
    @BindView(R.id.ll_meeting_files)
    LinearLayout rlMeetingFiles;

    private FloatingActionMenu actionMenu;

    private String meetingId;
    private MeetingByIdMessageBean bean;
    private String TAG = "Lpp";
    private StringBuilder stringBuilder = new StringBuilder();

    private ProgressDialog dialog;

    private boolean networkFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_meeting_details);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("加载中");
        dialog.setMessage("正在加载...");
        titleView.setLeftClickListener(new TitleView.LeftClickListener() {
            @Override
            public void OnLeftButtonClick() {
                finish();
            }
        });
        titleView.setRightTextSize(20);
        initFloatingActionMenu();
        loadPage();
    }

    private void initFloatingActionMenu() {

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        ImageView itemIcon = new ImageView(this);
        itemIcon.setPadding(10, 10, 10, 10);
        itemIcon.setImageDrawable(getResources().getDrawable(R.mipmap.icon_meeting_start));
        SubActionButton button1 = itemBuilder.setContentView(itemIcon).setBackgroundDrawable
                (getResources().getDrawable(R.mipmap.circle_fab)).setLayoutParams(new FrameLayout
                .LayoutParams(170, 170)).build();

        ImageView itemIcon2 = new ImageView(this);
        itemIcon2.setPadding(10, 10, 10, 10);
        itemIcon2.setImageDrawable(getResources().getDrawable(R.mipmap.icon_meeting_ending));
        SubActionButton button2 = itemBuilder.setContentView(itemIcon2).setBackgroundDrawable
                (getResources().getDrawable(R.mipmap.circle_fab)).setLayoutParams(new FrameLayout
                .LayoutParams(170, 170)).build();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMeeting();
                if (actionMenu != null) {
                    actionMenu.close(true);
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishMeeting();
                if (actionMenu != null) {
                    actionMenu.close(true);
                }
            }
        });

        actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .attachTo(fabStartOrEnd)
                .build();
    }

    private void finishMeeting() {
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.FinishMeetingBody[0], meetingId);
        final Request request = new Request.Builder()
                .post(body.build())
                .header(Api.FinishMeetingHeader[0], Api.FinishMeetingHeader[1])
                .addHeader("token", UserAccountUtils.getUserToken(getApplication()).getToken())
                .url(Api.FinishMeetingApi)
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtils.showToast(MeetingDetailsActivity.this, "结束会议失败");
                Log.d("Lpp", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Log.d(TAG, "会议结束: " + msg);
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(MeetingDetailsActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            UserAccountUtils.setUserInfo(newInfo, getApplication());
                            UserAccountUtils.setUserToken(newToken, getApplication());
                        }
                    });
                    return;
                }
                RequestReturnBean bean = new Gson().fromJson(msg, RequestReturnBean.class);
                if (bean != null && bean.status == 0) {
                    UIUtils.showToast(MeetingDetailsActivity.this, "会议结束");
                } else {
                    UIUtils.showToast(MeetingDetailsActivity.this, "会议结束失败，可能已经结束过了");
                }
            }
        });
    }

    private void startMeeting() {
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.StartMeetingBody[0], meetingId);
        final Request request = new Request.Builder()
                .post(body.build())
                .header(Api.StartMeetingHeader[0], Api.StartMeetingHeader[1])
                .addHeader("token", UserAccountUtils.getUserToken(getApplication()).getToken())
                .url(Api.StartMeetingApi)
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtils.showToast(MeetingDetailsActivity.this, "开始会议失败");
                Log.d("Lpp", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Log.d("Lpp", "会议开启" + msg);
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(MeetingDetailsActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            UserAccountUtils.setUserInfo(newInfo, getApplication());
                            UserAccountUtils.setUserToken(newToken, getApplication());
                        }
                    });
                    return;
                }
                RequestReturnBean bean = new Gson().fromJson(msg, RequestReturnBean.class);
                if (bean != null && bean.status == 0) {
                    UIUtils.showToast(MeetingDetailsActivity.this, "会议开启");
                } else {
                    UIUtils.showToast(MeetingDetailsActivity.this, "会议开启失败，可能已经开启过了");
                }
            }
        });
    }

    private void hideProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.hide();
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
                .header(Api.GetMeetingByIdHeader[0], Api.GetMeetingByIdHeader[1])
                .addHeader("token", UserAccountUtils.getUserToken(getApplication()).getToken())
                .url(Api.GetMeetingByIdApi)
                .post(body.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideProgressBar();
                showToast("加载失败");
                networkFlag = false;
                Log.d(TAG, "加载失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideProgressBar();
                String msg = response.body().string();

                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(MeetingDetailsActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            UserAccountUtils.setUserInfo(newInfo, getApplication());
                            UserAccountUtils.setUserToken(newToken, getApplication());
                        }
                    });
                    return;
                }
                Log.d(TAG, "onResponse: " + msg);
                bean = new Gson().fromJson(msg, MeetingByIdMessageBean.class);
                if (bean.status == 1) {
                    networkFlag = false;
                    showToast("加载失败");
                } else {
                    networkFlag = true;
                    changePage(bean);
                }
            }
        });
    }

    @OnClick({R.id.btn_add_member, R.id.fab_start_or_end, R.id.ll_meeting_files, R.id.rl_vote, R
            .id.rl_note})
    void onClick(View view) {
        if (!networkFlag) {
            showToast("加载错误");
            return;
        }
        switch (view.getId()) {
            case R.id.btn_add_member:
                addMember();
                break;
            case R.id.fab_start_or_end:
                fabClick();
                break;
            case R.id.ll_meeting_files:
                Intent intent = new Intent(this, FileListActivity.class);
                intent.putExtra("meetingID", meetingId);
                intent.putExtra("userId", UserAccountUtils.getUserInfo(getApplication())
                        .getUserId() + "");
                intent.putExtra("token", UserAccountUtils.getUserToken(getApplication()).getToken
                        ());
                startActivity(intent);
                break;
            case R.id.rl_vote:
                enterVote();
                break;
            case R.id.rl_note:
                Intent intent1 = new Intent(MeetingDetailsActivity.this, MeetingNoteActivity.class);
                intent1.putExtra("userId", UserAccountUtils.getUserInfo(getApplication())
                        .getUserId() + "");
                intent1.putExtra("meetingId", meetingId);
                intent1.putExtra("token", UserAccountUtils.getUserToken(getApplication())
                        .getToken());
                startActivity(intent1);
                break;
        }
    }

    private void enterVote() {
        Intent intent = new Intent(this, VoteActivity.class);
        intent.putExtra("userId", UserAccountUtils.getUserInfo(getApplication()).getUserId() + "");
        intent.putExtra("meetingId", meetingId);
        intent.putExtra("token", UserAccountUtils.getUserToken(getApplication()).getToken());
        startActivity(intent);
    }

    private void fabClick() {

    }

    private void addMember() {
        final EditText editText = new EditText(this);
        editText.setGravity(Gravity.CENTER);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("输入成员电话")
                .setCancelable(true)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dealAdd(editText.getText().toString());
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setView(editText)
                .show();
    }

    private void dealAdd(String phone) {
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.GetUserInfoBody[0], phone);
        Request request = new Request.Builder()
                .header("token", UserAccountUtils.getUserToken(getApplication()).getToken())
                .post(body.build())
                .url(Api.GetUserInfoApi)
                .build();
        showProgressBar();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideProgressBar();
                showToast("添加失败");
                Log.d(TAG, "添加失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(MeetingDetailsActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            UserAccountUtils.setUserInfo(newInfo, getApplication());
                            UserAccountUtils.setUserToken(newToken, getApplication());
                        }
                    });
                    return;
                }
                Log.d(TAG, "onResponse: " + msg);
                UserDataBean userDataBean = new Gson().fromJson(msg, UserDataBean.class);
                if (userDataBean != null) {
                    if (userDataBean.status != 0) {
                        hideProgressBar();
                        showToast("添加失败，用户不存在");
                    } else {
                        inviteMember(userDataBean);
                    }
                }
            }
        });
    }

    private void inviteMember(final UserDataBean userDataBean) {
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.MeetingAddMemberBody[0], userDataBean.data.getUserId() + "");
        body.add(Api.MeetingAddMemberBody[1], meetingId);
        final Request request = new Request.Builder()
                .url(Api.MeetingAddMemberApi)
                .header(Api.MeetingAddMemberHeader[0], Api.MeetingAddMemberHeader[1])
                .post(body.build())
                .addHeader("token", UserAccountUtils.getUserToken(getApplication()).getToken())
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideProgressBar();
                showToast("添加失败");
                Log.d(TAG, "添加失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideProgressBar();
                String msg = response.body().string();
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(MeetingDetailsActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            UserAccountUtils.setUserInfo(newInfo, getApplication());
                            UserAccountUtils.setUserToken(newToken, getApplication());
                        }
                    });
                    return;
                }
                Log.d(TAG, "onResponse: " + msg);
                RequestReturnBean requestReturnBean = new Gson().fromJson(msg, RequestReturnBean
                        .class);
                if (requestReturnBean.status == 0) {
                    showToast("邀请会议成员成功");
                    changeMemberUi(userDataBean);
                } else {
                    showToast("邀请会议成员失败，成员可能已添加");
                }
            }
        });
    }

    private void changeMemberUi(final UserDataBean userDataBean) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bean.data.peopleNum += 1;
                tvPeopleSum.setText(bean.data.peopleNum + "");
                stringBuilder.append("\n").append(userDataBean.data.getUsername());
                tvMemberList.setText(stringBuilder.toString());
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
                tvPlace.setText("地点：" + bean.data.roomName);
                tvIntroduce.setText(bean.data.meetingIntro);
                if (UserAccountUtils.getUserInfo(getApplication()).userId != bean.data.masterId) {
                    btnAddMember.setVisibility(View.GONE);
                    fabStartOrEnd.setVisibility(View.GONE);
                }
                stringBuilder.append(bean.data.masterName).append("（组织者）");
                tvMemberList.setText(stringBuilder.toString());
                for (int i = 0; i < bean.data.memberStatus.size(); i++) {
                    if (i == 6) {
                        stringBuilder.append("\n").append("···");
                        break;
                    }
                    MeetingByIdMessage.MemberStatusBean memberStatusBean = bean.data.memberStatus
                            .get(i);
                    if (memberStatusBean.userId == bean.data.masterId) {
                        continue;
                    }
                    stringBuilder.append("\n").append(memberStatusBean.username);
                    tvMemberList.setText(stringBuilder.toString());
                }
            }
        });
    }

    private void showToast(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MeetingDetailsActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
