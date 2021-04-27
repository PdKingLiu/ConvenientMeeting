package com.pdking.convenientmeeting.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.adapter.MeetingRoomAdapter;
import com.pdking.convenientmeeting.adapter.QueryMeetingAdapter;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.MeetingMessage;
import com.pdking.convenientmeeting.db.MeetingMessageBean;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.OkHttpUtils;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.utils.UIUtils;
import com.pdking.convenientmeeting.utils.UserAccountUtils;
import com.pdking.convenientmeeting.weight.TitleView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class DifferentTypesMeetingListActivity extends AppCompatActivity implements TitleView
        .LeftClickListener {

    @BindView(R.id.title)
    TitleView title;
    @BindView(R.id.srl_flush)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.rv_absence)
    RecyclerView recyclerView;
    private List<MeetingMessage> beanList;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Calendar calendar = Calendar.getInstance();
    private Date dateTem;
    private int year;
    private QueryMeetingAdapter adapter;
    private int userStatus = -1;
    private int userKind = -1;
    private int isCancel = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_different_types_meeting_list);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        year = getIntent().getIntExtra("year", -1);
        userStatus = getIntent().getIntExtra("userStatus", -1);
        userKind = getIntent().getIntExtra("userKind", -1);
        isCancel = getIntent().getIntExtra("isCancel", -1);
        if (userStatus == 1) {
            title.setTitleText("正常");
        } else if (userStatus == 2) {
            title.setTitleText("缺勤");
        } else if (userStatus == 3) {
            title.setTitleText("迟到");
        } else if (userStatus == 4) {
            title.setTitleText("请假");
        }
        if (userKind == 1) {
            title.setTitleText("组织");
        } else if (userKind == 2) {
            title.setTitleText("参与");
        }
        if (isCancel == 1) {
            title.setTitleText("取消");
        }
        title.setLeftClickListener(this);
        title.setRightText(year + "年");
        initPage();
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refresh();
            }
        });
        smartRefreshLayout.autoRefresh();
    }

    private void initPage() {
        beanList = new ArrayList<>();
        adapter = new QueryMeetingAdapter(this, beanList);
        adapter.setItemClickListener(new MeetingRoomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                beanList.get(position);
                Intent intent = new Intent(DifferentTypesMeetingListActivity.this,
                        MeetingDetailsActivity.class);
                intent.putExtra("meetingId", beanList.get(position).meetingId + "");
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void OnLeftButtonClick() {
        finish();
    }

    private void refresh() {
        FormBody.Builder body = new FormBody.Builder()
                .add(Api.RequestUserMeetingListBody[0], UserAccountUtils.getUserInfo
                        (getApplication()).getUserId() + "")
                .add(Api.RequestUserMeetingListBody[1], 2 + "");
        Request request = new Request.Builder()
                .post(body.build())
                .header(Api.RequestUserMeetingListHeader[0], Api.RequestUserMeetingListHeader[1])
                .addHeader("token", UserAccountUtils.getUserToken(getApplication()).getToken())
                .url(Api.RequestUserMeetingListApi)
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                smartRefreshLayout.finishRefresh(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(DifferentTypesMeetingListActivity.this, new
                            LoginCallBack() {
                                @Override
                                public void newMessageCallBack(UserInfo newInfo, UserToken
                                        newToken) {
                                    UserAccountUtils.setUserInfo(newInfo, getApplication());
                                    UserAccountUtils.setUserToken(newToken, getApplication());
                                }
                            });
                    return;
                }
                MeetingMessageBean bean = new Gson().fromJson(msg, MeetingMessageBean.class);
                if (bean != null && bean.status == 0) {
                    smartRefreshLayout.finishRefresh(true);
                    beanList.clear();
                    try {
                        for (int i = 0; i < bean.data.size(); i++) {
                            dateTem = format.parse(bean.data.get(i).startTime);
                            calendar.setTime(dateTem);
                            if (calendar.get(Calendar.YEAR) == year) {
                                if (bean.data.get(i).userStatus == userStatus) {
                                    beanList.add(bean.data.get(i));
                                }
                                if (userKind == 1 && bean.data.get(i).masterId ==
                                        UserAccountUtils.getUserInfo(getApplication()).userId) {
                                    beanList.add(bean.data.get(i));
                                }
                                if (userKind == 2 && bean.data.get(i).masterId !=
                                        UserAccountUtils.getUserInfo(getApplication()).userId) {
                                    beanList.add(bean.data.get(i));
                                }
                                if (isCancel == 1 && bean.data.get(i).status == 5) {
                                    beanList.add(bean.data.get(i));
                                }
                            }
                        }
                    } catch (Exception e) {

                    }
                    if (beanList == null || beanList.size() == 0) {
                        UIUtils.showToast(DifferentTypesMeetingListActivity.this, "暂无数据记录！");
                        return;
                    }
                    Collections.sort(beanList, new Comparator<MeetingMessage>() {
                        @Override
                        public int compare(MeetingMessage o1, MeetingMessage o2) {
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new
                                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = null;
                            Date date2 = null;
                            try {
                                date = format.parse(o1.startTime);
                                date2 = format.parse(o2.startTime);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return (int) (date2.getTime() - date.getTime());
                        }
                    });
                    notifyDataChanged();
                } else {
                    smartRefreshLayout.finishRefresh(false);
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
}
