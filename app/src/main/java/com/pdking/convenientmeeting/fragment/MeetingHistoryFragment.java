package com.pdking.convenientmeeting.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.activity.MeetingDetailsActivity;
import com.pdking.convenientmeeting.adapter.MeetingHistoryAdapter;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.MeetingMessage;
import com.pdking.convenientmeeting.db.MeetingMessageBean;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.OkHttpUtils;
import com.pdking.convenientmeeting.utils.UIUtils;
import com.pdking.convenientmeeting.utils.UserAccountUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.litepal.LitePal;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MeetingHistoryFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private SmartRefreshLayout refreshLayout;
    private RelativeLayout rlHaveNothing;
    private List<MeetingMessage> beanList;
    private MeetingHistoryAdapter mAdapter;

    public MeetingHistoryFragment() {
    }

    public static MeetingHistoryFragment newInstance() {
        return new MeetingHistoryFragment();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initList();
        initRecyclerAndRefresh();
    }

    private void initRecyclerAndRefresh() {
        mAdapter = new MeetingHistoryAdapter(beanList, (AppCompatActivity) getActivity());
        mAdapter.setClickListener(new MeetingHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getContext(), MeetingDetailsActivity.class);
                intent.putExtra("meetingId", beanList.get(position).meetingId + "");
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refresh();
            }
        });
    }

    private void refresh() {
        if (!UserAccountUtils.accountIsValid(getActivity().getApplication())) {
            UIUtils.showToast(getActivity(), "未知错误");
            return;
        }
        FormBody.Builder body = new FormBody.Builder()
                .add(Api.RequestUserMeetingListBody[0], UserAccountUtils.getUserInfo(getActivity
                        ().getApplication()).getUserId() + "")
                .add(Api.RequestUserMeetingListBody[1], 2 + "");
        Request request = new Request.Builder()
                .post(body.build())
                .header(Api.RequestUserMeetingListHeader[0], Api.RequestUserMeetingListHeader[1])
                .addHeader("token", UserAccountUtils.getUserToken(getActivity().getApplication())
                        .getToken())
                .url(Api.RequestUserMeetingListApi)
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
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
                MeetingMessageBean bean = new Gson().fromJson(msg, MeetingMessageBean.class);
                if (bean != null && bean.status == 0) {
                    for (MeetingMessage message : bean.data) {
                        message.meetingType = 2;
                    }
                    refreshLayout.finishRefresh(true);
                    beanList.clear();
                    beanList.addAll(bean.data);
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
                    LitePal.deleteAll(MeetingMessage.class, "meetingType = ?", "2");
                    LitePal.saveAll(beanList);
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
                if (mAdapter != null && beanList != null) {
                    if (beanList.size() == 0) {
                        rlHaveNothing.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        rlHaveNothing.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    rlHaveNothing.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initList() {
        beanList = new ArrayList<>();
        beanList = LitePal.where("meetingType = ?", "2").find(MeetingMessage.class);
        if (beanList.size() == 0) {
            rlHaveNothing.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            rlHaveNothing.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_meeting_history, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        refreshLayout = view.findViewById(R.id.srl_flush);
        recyclerView = view.findViewById(R.id.rv_history);
        rlHaveNothing = view.findViewById(R.id.rl_have_nothing);
        rlHaveNothing.setOnClickListener(this);
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
