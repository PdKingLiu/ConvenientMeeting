package com.pdking.convenientmeeting.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.db.VoteListBean;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.OkHttpUtils;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.utils.UIUtils;
import com.pdking.convenientmeeting.weight.TitleView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class VoteDetailsActivity extends AppCompatActivity implements CompoundButton
        .OnCheckedChangeListener, View.OnClickListener, TitleView.LeftClickListener {

    @BindView(R.id.title)
    TitleView title;
    @BindView(R.id.rb_1)
    RadioButton rb1;
    @BindView(R.id.rb_2)
    RadioButton rb2;
    @BindView(R.id.rb_3)
    RadioButton rb3;
    @BindView(R.id.rb_4)
    RadioButton rb4;
    @BindView(R.id.rb_5)
    RadioButton rb5;
    @BindView(R.id.ll_1)
    LinearLayout ll1;
    @BindView(R.id.ll_2)
    LinearLayout ll2;
    @BindView(R.id.ll_3)
    LinearLayout ll3;
    @BindView(R.id.ll_4)
    LinearLayout ll4;
    @BindView(R.id.ll_5)
    LinearLayout ll5;
    @BindView(R.id.iv_1)
    ImageView iv1;
    @BindView(R.id.iv_2)
    ImageView iv2;
    @BindView(R.id.iv_3)
    ImageView iv3;
    @BindView(R.id.iv_4)
    ImageView iv4;
    @BindView(R.id.iv_5)
    ImageView iv5;
    @BindView(R.id.tv_kind)
    TextView tvKind;
    @BindView(R.id.tv_ticket_sum)
    TextView tvTicketSum;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_ticket_1)
    TextView tvTicket1;
    @BindView(R.id.tv_ticket_2)
    TextView tvTicket2;
    @BindView(R.id.tv_ticket_3)
    TextView tvTicket3;
    @BindView(R.id.tv_ticket_4)
    TextView tvTicket4;
    @BindView(R.id.tv_ticket_5)
    TextView tvTicket5;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.btn_vote)
    Button btnVote;
    @BindView(R.id.ll_choose_1)
    LinearLayout llTicket1;
    @BindView(R.id.ll_choose_2)
    LinearLayout llTicket2;
    @BindView(R.id.ll_choose_3)
    LinearLayout llTicket3;
    @BindView(R.id.ll_choose_4)
    LinearLayout llTicket4;
    @BindView(R.id.ll_choose_5)
    LinearLayout llTicket5;
    @BindView(R.id.ll_proportion_1)
    LinearLayout llProportion1;
    @BindView(R.id.ll_proportion_2)
    LinearLayout llProportion2;
    @BindView(R.id.ll_proportion_3)
    LinearLayout llProportion3;
    @BindView(R.id.ll_proportion_4)
    LinearLayout llProportion4;
    @BindView(R.id.ll_proportion_5)
    LinearLayout llProportion5;
    @BindView(R.id.view_shape_1)
    View viewShape1;
    @BindView(R.id.view_shape_2)
    View viewShape2;
    @BindView(R.id.view_shape_3)
    View viewShape3;
    @BindView(R.id.view_shape_4)
    View viewShape4;
    @BindView(R.id.view_shape_5)
    View viewShape5;
    @BindView(R.id.ll_vote_item)
    LinearLayout llVoteItem;
    @BindView(R.id.ll_vote_result)
    LinearLayout llVoteResult;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.civ_user_icon)
    CircleImageView civUserIcon;
    @BindView(R.id.tv_option_1)
    TextView tvOption1;
    @BindView(R.id.tv_option_2)
    TextView tvOption2;
    @BindView(R.id.tv_option_3)
    TextView tvOption3;
    @BindView(R.id.tv_option_4)
    TextView tvOption4;
    @BindView(R.id.tv_option_5)
    TextView tvOption5;
    @BindView(R.id.tv_option_result_1)
    TextView tvOptionResult1;
    @BindView(R.id.tv_option_result_2)
    TextView tvOptionResult2;
    @BindView(R.id.tv_option_result_3)
    TextView tvOptionResult3;
    @BindView(R.id.tv_option_result_4)
    TextView tvOptionResult4;
    @BindView(R.id.tv_option_result_5)
    TextView tvOptionResult5;

    private boolean singleFlag = true;

    private String kind;
    private String meetingId;
    private String userId;
    private String token;
    private String voteId;
    private List<VoteListBean.VoteBean> voteList;
    private VoteListBean.VoteBean thisMessage;
    private boolean isVote = false;

    private boolean[] chooseList = {false, false, false, false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_vote_details);
        ButterKnife.bind(this);
        SystemUtil.setTitleMode(getWindow());
        kind = getIntent().getStringExtra("kind");
        meetingId = getIntent().getStringExtra("meetingId");
        userId = getIntent().getStringExtra("userId");
        token = getIntent().getStringExtra("token");
        voteId = getIntent().getStringExtra("voteId");
        setListener();
        switch (kind) {
            case "1":
                loadResultByNoChoose();
                break;
            case "2":
                loadChoose();
                break;
            case "3":
                loadResultByHaveChoose();
                break;
        }
    }

    private void loadResultByNoChoose() {
        llVoteItem.setVisibility(View.GONE);
        llVoteResult.setVisibility(View.VISIBLE);
        btnVote.setVisibility(View.GONE);
        Request request = new Request.Builder()
                .url(Api.GetVoteListApi + "?" + Api.GetVoteListBody[0] + "=" + meetingId + "&" + Api
                        .GetVoteListBody[1] + "=" + userId)
                .header("token", token)
                .get()
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtils.showToast(VoteDetailsActivity.this, "加载失败");
                Log.d("Lpp", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                if (msg.contains("token过期")) {
                    LoginStatusUtils.stateFailure(VoteDetailsActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            token = newToken.getToken();
                        }
                    });
                    return;
                }
                VoteListBean bean = new Gson().fromJson(msg, VoteListBean.class);
                if (bean == null || bean.status != 0) {
                    UIUtils.showToast(VoteDetailsActivity.this, "加载失败");
                } else {
                    voteList = new ArrayList<>();
                    voteList.addAll(bean.data);
                    for (int i = 0; i < voteList.size(); i++) {
                        if ((voteList.get(i).voteId + "").equals(voteId)) {
                            thisMessage = voteList.get(i);
                            Log.d("Lpp", "thisMessage: " + thisMessage);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadOtherPage();
                                    loadNotChoose();
                                }
                            });
                            break;
                        }
                    }
                }
            }
        });

    }

    private void loadNotChoose() {
        tvTicketSum.setVisibility(View.VISIBLE);
        int sum = 0;
        int[] proportions = {0, 0, 0, 0, 0};
        for (int i = 0; i < thisMessage.optionList.size(); i++) {
            sum += thisMessage.optionList.get(i).num;
            proportions[i] = thisMessage.optionList.get(i).num;
        }
        tvTicketSum.setText("共" + sum + "票");
        llTicket1.setVisibility(View.VISIBLE);
        tvOptionResult1.setText(thisMessage.optionList.get(0).optionName);
        tvOptionResult2.setText(thisMessage.optionList.get(1).optionName);
        llTicket2.setVisibility(View.VISIBLE);
        tvTicket1.setText(thisMessage.optionList.get(0).num + " 票");
        tvTicket2.setText(thisMessage.optionList.get(1).num + " 票");
        Log.d("Lpp", "thisMessage.optionList.size(): " + thisMessage.optionList.size());
        switch (thisMessage.optionList.size() - 2) {
            case 1:
                tvTicket3.setText(thisMessage.optionList.get(2).num + " 票");
                llTicket3.setVisibility(View.VISIBLE);
                tvOptionResult3.setText(thisMessage.optionList.get(2).optionName);
                llTicket4.setVisibility(View.GONE);
                llTicket5.setVisibility(View.GONE);
                break;
            case 2:
                tvTicket4.setText(thisMessage.optionList.get(3).num + " 票");
                tvTicket3.setText(thisMessage.optionList.get(2).num + " 票");
                llTicket3.setVisibility(View.VISIBLE);
                llTicket4.setVisibility(View.VISIBLE);
                tvOptionResult3.setText(thisMessage.optionList.get(2).optionName);
                tvOptionResult4.setText(thisMessage.optionList.get(3).optionName);
                llTicket5.setVisibility(View.GONE);
                break;
            case 3:
                tvTicket4.setText(thisMessage.optionList.get(3).num + " 票");
                tvTicket3.setText(thisMessage.optionList.get(2).num + " 票");
                tvTicket5.setText(thisMessage.optionList.get(4).num + " 票");
                tvOptionResult3.setText(thisMessage.optionList.get(2).optionName);
                tvOptionResult4.setText(thisMessage.optionList.get(3).optionName);
                tvOptionResult5.setText(thisMessage.optionList.get(4).optionName);
                llTicket3.setVisibility(View.VISIBLE);
                llTicket4.setVisibility(View.VISIBLE);
                llTicket5.setVisibility(View.VISIBLE);
                break;
        }
        setProportion(sum, proportions);
    }

    private void loadChoose() {
        llVoteItem.setVisibility(View.VISIBLE);
        llVoteResult.setVisibility(View.GONE);
        btnVote.setVisibility(View.VISIBLE);
        Request request = new Request.Builder()
                .url(Api.GetVoteListApi + "?" + Api.GetVoteListBody[0] + "=" + meetingId + "&" + Api
                        .GetVoteListBody[1] + "=" + userId)
                .header("token", token)
                .get()
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtils.showToast(VoteDetailsActivity.this, "加载失败");
                Log.d("Lpp", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Log.d("Lpp", "onResponse: " + msg);
                if (msg.contains("token过期")) {
                    LoginStatusUtils.stateFailure(VoteDetailsActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            token = newToken.getToken();
                        }
                    });
                    return;
                }
                VoteListBean bean = new Gson().fromJson(msg, VoteListBean.class);
                if (bean == null || bean.status != 0) {
                    UIUtils.showToast(VoteDetailsActivity.this, "加载失败");
                } else {
                    voteList = new ArrayList<>();
                    voteList.addAll(bean.data);
                    for (int i = 0; i < voteList.size(); i++) {
                        if ((voteList.get(i).voteId + "").equals(voteId)) {
                            thisMessage = voteList.get(i);
                            Log.d("Lpp", "thisMessage: " + thisMessage);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadOtherPage();
                                    loadChoosePage();
                                }
                            });
                            break;
                        }
                    }
                }
            }
        });
    }

    private void loadResultPage() {
        tvTicketSum.setVisibility(View.VISIBLE);
        int sum = 0;
        int[] proportions = {0, 0, 0, 0, 0};
        for (int i = 0; i < thisMessage.optionList.size(); i++) {
            sum += thisMessage.optionList.get(i).num;
            proportions[i] = thisMessage.optionList.get(i).num;
        }
        tvTicketSum.setText("共" + sum + "票");
        llTicket1.setVisibility(View.VISIBLE);
        tvOptionResult1.setText(thisMessage.optionList.get(0).optionName);
        tvOptionResult2.setText(thisMessage.optionList.get(1).optionName);
        llTicket2.setVisibility(View.VISIBLE);
        tvTicket1.setText(thisMessage.optionList.get(0).num + " 票");
        tvTicket2.setText(thisMessage.optionList.get(1).num + " 票");
        Log.d("Lpp", "thisMessage.optionList.size(): " + thisMessage.optionList.size());
        switch (thisMessage.optionList.size() - 2) {
            case 1:
                tvTicket3.setText(thisMessage.optionList.get(2).num + " 票");
                tvOptionResult3.setText(thisMessage.optionList.get(2).optionName);
                llTicket3.setVisibility(View.VISIBLE);
                llTicket4.setVisibility(View.GONE);
                llTicket5.setVisibility(View.GONE);
                break;
            case 2:
                tvTicket3.setText(thisMessage.optionList.get(2).num + " 票");
                tvTicket4.setText(thisMessage.optionList.get(3).num + " 票");
                tvOptionResult3.setText(thisMessage.optionList.get(2).optionName);
                tvOptionResult4.setText(thisMessage.optionList.get(3).optionName);
                llTicket3.setVisibility(View.VISIBLE);
                llTicket4.setVisibility(View.VISIBLE);
                llTicket5.setVisibility(View.GONE);
                break;
            case 3:
                tvTicket3.setText(thisMessage.optionList.get(2).num + " 票");
                tvTicket4.setText(thisMessage.optionList.get(3).num + " 票");
                tvTicket5.setText(thisMessage.optionList.get(4).num + " 票");
                tvOptionResult3.setText(thisMessage.optionList.get(2).optionName);
                tvOptionResult4.setText(thisMessage.optionList.get(3).optionName);
                tvOptionResult5.setText(thisMessage.optionList.get(4).optionName);
                llTicket3.setVisibility(View.VISIBLE);
                llTicket4.setVisibility(View.VISIBLE);
                llTicket5.setVisibility(View.VISIBLE);
                break;
        }
        setProportion(sum, proportions);
        int[] which = new int[thisMessage.userSelectList.size()];
        for (int i = 0; i < thisMessage.userSelectList.size(); i++) {
            for (int j = 0; j < thisMessage.optionList.size(); j++) {
                if (thisMessage.optionList.get(j).id == thisMessage.userSelectList.get(i)) {
                    which[i] = j + 1;
                    break;
                }
            }
        }
        setWhichIcon(which);
    }

    private void loadOtherPage() {
        if (thisMessage.selectWay != 1) {
            singleFlag = false;
            tvKind.setText("多选");
        } else {
            singleFlag = true;
            tvKind.setText("单选");
        }
        tvContent.setText(thisMessage.topic);
        tvEndTime.setText("截止日期：" + new SimpleDateFormat("MM-dd HH:mm").format(new Date(thisMessage
                .endTime)));
        Glide.with(this).load(thisMessage.userInfo.avatarUrl).into(civUserIcon);
        tvUserName.setText(thisMessage.userInfo.username);
        Calendar calendar = Calendar.getInstance();
        long len = calendar.getTime().getTime() - thisMessage.createTime;
        if (len / 1000 / 60 <= 60) {
            if (len <= 0) {
                tvTime.setText("刚刚");
            } else {
                tvTime.setText(len / 1000 / 60 + "分钟前");
            }
        } else {
            tvTime.setText(new SimpleDateFormat("MM-dd HH:mm").format(new Date(thisMessage
                    .createTime)));
        }
        if (calendar.getTime().getTime() > thisMessage.endTime) {
            tvStatus.setText("已结束");
            tvStatus.setBackground(getResources().getDrawable(R.mipmap.icon_vote_end));
        } else {
            tvStatus.setText("正在进行");
            tvStatus.setBackground(getResources().getDrawable(R.mipmap
                    .icon_vote_proceed));
        }
    }

    private void loadChoosePage() {
        tvTicketSum.setVisibility(View.GONE);
        isVote = false;
        ll1.setVisibility(View.VISIBLE);
        tvOption1.setText(thisMessage.optionList.get(0).optionName);
        tvOption2.setText(thisMessage.optionList.get(1).optionName);
        ll2.setVisibility(View.VISIBLE);
        switch (thisMessage.optionList.size() - 2) {
            case 1:
                ll3.setVisibility(View.VISIBLE);
                tvOption3.setText(thisMessage.optionList.get(2).optionName);
                ll4.setVisibility(View.GONE);
                ll5.setVisibility(View.GONE);
                break;
            case 2:
                ll3.setVisibility(View.VISIBLE);
                ll4.setVisibility(View.VISIBLE);
                tvOption3.setText(thisMessage.optionList.get(2).optionName);
                tvOption4.setText(thisMessage.optionList.get(3).optionName);
                ll5.setVisibility(View.GONE);
                break;
            case 3:
                tvOption3.setText(thisMessage.optionList.get(2).optionName);
                tvOption4.setText(thisMessage.optionList.get(3).optionName);
                tvOption5.setText(thisMessage.optionList.get(4).optionName);
                ll3.setVisibility(View.VISIBLE);
                ll4.setVisibility(View.VISIBLE);
                ll5.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void loadResultByHaveChoose() {
        llVoteItem.setVisibility(View.GONE);
        llVoteResult.setVisibility(View.VISIBLE);
        btnVote.setVisibility(View.GONE);
        Request request = new Request.Builder()
                .url(Api.GetVoteListApi + "?" + Api.GetVoteListBody[0] + "=" + meetingId + "&" + Api
                        .GetVoteListBody[1] + "=" + userId)
                .header("token", token)
                .get()
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtils.showToast(VoteDetailsActivity.this, "加载失败");
                Log.d("Lpp", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                if (msg.contains("token过期")) {
                    LoginStatusUtils.stateFailure(VoteDetailsActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            token = newToken.getToken();
                        }
                    });
                    return;
                }
                VoteListBean bean = new Gson().fromJson(msg, VoteListBean.class);
                if (bean == null || bean.status != 0) {
                    UIUtils.showToast(VoteDetailsActivity.this, "加载失败");
                } else {
                    voteList = new ArrayList<>();
                    voteList.addAll(bean.data);
                    for (int i = 0; i < voteList.size(); i++) {
                        if ((voteList.get(i).voteId + "").equals(voteId)) {
                            thisMessage = voteList.get(i);
                            Log.d("Lpp", "thisMessage: " + thisMessage);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadOtherPage();
                                    loadResultPage();
                                }
                            });
                            break;
                        }
                    }
                }
            }
        });

    }

    private void setWhichIcon(int[] whichIcon) {
        iv1.setVisibility(View.INVISIBLE);
        iv2.setVisibility(View.INVISIBLE);
        iv3.setVisibility(View.INVISIBLE);
        iv4.setVisibility(View.INVISIBLE);
        iv5.setVisibility(View.INVISIBLE);
        for (int i = 0; i < whichIcon.length; i++) {
            switch (whichIcon[i]) {
                case 1:
                    iv1.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    iv2.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    iv3.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    iv4.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    iv5.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void setProportion(int weightSum, int[] proportions) {
        llProportion1.setWeightSum((float) weightSum);
        llProportion2.setWeightSum((float) weightSum);
        llProportion3.setWeightSum((float) weightSum);
        llProportion4.setWeightSum((float) weightSum);
        llProportion5.setWeightSum((float) weightSum);
        viewShape1.setLayoutParams(new LinearLayout.LayoutParams(0, viewShape1.getHeight(),
                proportions[0]));
        viewShape2.setLayoutParams(new LinearLayout.LayoutParams(0, viewShape2.getHeight(),
                proportions[1]));
        viewShape3.setLayoutParams(new LinearLayout.LayoutParams(0, viewShape3.getHeight(),
                proportions[2]));
        viewShape4.setLayoutParams(new LinearLayout.LayoutParams(0, viewShape4.getHeight(),
                proportions[3]));
        viewShape5.setLayoutParams(new LinearLayout.LayoutParams(0, viewShape5.getHeight(),
                proportions[4]));
    }

    private void setListener() {
        rb1.setOnCheckedChangeListener(this);
        rb2.setOnCheckedChangeListener(this);
        rb3.setOnCheckedChangeListener(this);
        rb4.setOnCheckedChangeListener(this);
        rb5.setOnCheckedChangeListener(this);
        ll1.setOnClickListener(this);
        ll2.setOnClickListener(this);
        ll3.setOnClickListener(this);
        ll4.setOnClickListener(this);
        ll5.setOnClickListener(this);
        rb1.setOnClickListener(this);
        rb2.setOnClickListener(this);
        rb3.setOnClickListener(this);
        rb4.setOnClickListener(this);
        rb5.setOnClickListener(this);
        title.setLeftClickListener(this);
        btnVote.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!singleFlag) {
            return;
        }
        int which;
        switch (buttonView.getId()) {
            case R.id.rb_1:
                which = 1;
                break;
            case R.id.rb_2:
                which = 2;
                break;
            case R.id.rb_3:
                which = 3;
                break;
            case R.id.rb_4:
                which = 4;
                break;
            default:
                which = 5;
        }
        if (isChecked) {
            changeRadioButton(which);
        }
    }

    private void changeRadioButton(int which) {
        if (which == 1) {
            rb1.setChecked(true);
        } else {
            rb1.setChecked(false);
        }
        if (which == 2) {
            rb2.setChecked(true);
        } else {
            rb2.setChecked(false);
        }
        if (which == 3) {
            rb3.setChecked(true);
        } else {
            rb3.setChecked(false);
        }
        if (which == 4) {
            rb4.setChecked(true);
        } else {
            rb4.setChecked(false);
        }
        if (which == 5) {
            rb5.setChecked(true);
        } else {
            rb5.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {
        int which = -1;
        switch (v.getId()) {
            case R.id.ll_1:
                which = 1;
                break;
            case R.id.ll_2:
                which = 2;
                break;
            case R.id.ll_3:
                which = 3;
                break;
            case R.id.ll_4:
                which = 4;
                break;
            case R.id.ll_5:
                which = 5;
                break;
            case R.id.btn_vote:
                vote();
                break;
        }
        if (!singleFlag) {
            switch (which) {
                case 1:
                    if (rb1.isChecked()) {
                        rb1.setChecked(false);
                    } else {
                        rb1.setChecked(true);
                    }
                    break;
                case 2:
                    if (rb2.isChecked()) {
                        rb2.setChecked(false);
                    } else {
                        rb2.setChecked(true);
                    }
                    break;
                case 3:
                    if (rb3.isChecked()) {
                        rb3.setChecked(false);
                    } else {
                        rb3.setChecked(true);
                    }
                    break;
                case 4:
                    if (rb4.isChecked()) {
                        rb4.setChecked(false);
                    } else {
                        rb4.setChecked(true);
                    }
                    break;
                case 5:
                    if (rb5.isChecked()) {
                        rb5.setChecked(false);
                    } else {
                        rb5.setChecked(true);
                    }
                    break;
            }
        } else {
            if (which != -1) {
                changeRadioButton(which);
            }
        }
    }

    private void vote() {
        if (isVote) {
            UIUtils.showToast(this, "已经投过了");
            return;
        }
        chooseList[0] = rb1.isChecked();
        chooseList[1] = rb2.isChecked();
        chooseList[2] = rb3.isChecked();
        chooseList[3] = rb4.isChecked();
        chooseList[4] = rb5.isChecked();
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.VoteBody[0], userId);
        body.add(Api.VoteBody[1], thisMessage.voteId + "");
        if (chooseList[0]) {
            body.add(Api.VoteBody[2], thisMessage.optionList.get(0).id + "");
        }
        if (chooseList[1]) {
            body.add(Api.VoteBody[2], thisMessage.optionList.get(1).id + "");
        }
        if (chooseList[2]) {
            body.add(Api.VoteBody[2], thisMessage.optionList.get(2).id + "");
        }
        if (chooseList[3]) {
            body.add(Api.VoteBody[2], thisMessage.optionList.get(3).id + "");
        }
        if (chooseList[4]) {
            body.add(Api.VoteBody[2], thisMessage.optionList.get(4).id + "");
        }
        Request request = new Request.Builder()
                .header(Api.VoteHeader[0], Api.VoteHeader[1])
                .addHeader("token", token)
                .url(Api.VoteApi)
                .post(body.build())
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtils.showToast(VoteDetailsActivity.this, "投票失败");
                isVote = false;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Log.d("Lpp", "msg:" + msg);
                if (msg.contains("token过期")) {
                    LoginStatusUtils.stateFailure(VoteDetailsActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            token = newToken.getToken();
                        }
                    });
                    return;
                }
                if (msg.contains("投票成功")) {
                    UIUtils.showToast(VoteDetailsActivity.this, "投票成功");
                    Intent intent = new Intent();
                    intent.putExtra("result", 1);
                    setResult(RESULT_OK, intent);
                    isVote = true;
                } else {
                    UIUtils.showToast(VoteDetailsActivity.this, "投票失败");
                    isVote = false;
                }
            }
        });
    }

    @Override
    public void OnLeftButtonClick() {
        finish();
    }

}
