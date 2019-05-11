package com.pdking.convenientmeeting.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.pdking.convenientmeeting.App;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.adapter.OnItemClickListener;
import com.pdking.convenientmeeting.adapter.VideoListAdapter;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.EnterLiveVideoBean;
import com.pdking.convenientmeeting.db.QueryVideoMessageBean;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.livemeeting.openlive.model.ConstantApp;
import com.pdking.convenientmeeting.livemeeting.openlive.ui.LiveRoomActivity;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.OkHttpUtils;
import com.pdking.convenientmeeting.utils.UIUtils;
import com.pdking.convenientmeeting.utils.UserAccountUtils;
import com.pdking.convenientmeeting.weight.EnterLiveMeetingDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.agora.rtc.Constants;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoNowFragment extends Fragment implements View.OnClickListener {

    private static VideoNowFragment videoNowFragment;

    private SmartRefreshLayout refreshLayout;
    private RelativeLayout rlHaveNothing;
    private RecyclerView recyclerView;

    private List<QueryVideoMessageBean.DataBean> beanList;

    private VideoListAdapter videoListAdapter;


    public static VideoNowFragment newInstance() {
        if (videoNowFragment == null) {
            videoNowFragment = new VideoNowFragment();
        }
        return videoNowFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_video_now, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        refreshLayout = view.findViewById(R.id.srl_flush);
        recyclerView = view.findViewById(R.id.rv_now_video);
        rlHaveNothing = view.findViewById(R.id.rl_have_nothing);
        rlHaveNothing.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initList();
        initRecyclerAndFlush();
        refreshLayout.autoRefresh();
    }

    private void initList() {
        beanList = new ArrayList<>();
        if (beanList.size() == 0) {
            rlHaveNothing.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            rlHaveNothing.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_have_nothing:
                autoRefresh();
                break;
        }
    }

    public void autoRefresh() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshLayout.autoRefresh();
            }
        });
    }

    private void initRecyclerAndFlush() {
        videoListAdapter = new VideoListAdapter(beanList, getContext());
        videoListAdapter.setListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                enterRoom(position);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(videoListAdapter);
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

    private void enterRoom(final int position) {
        ((App) getActivity().getApplication()).initWorkerThread();
        EnterLiveMeetingDialog dialog = new EnterLiveMeetingDialog(getContext(), R.style
                .DialogTheme);
        dialog.setListener(new EnterLiveMeetingDialog.OnClickListener() {
            @Override
            public void onClick(String password) {
                enterRoom(password, position);
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }

    private void enterRoom(String password, int position) {
        final QueryVideoMessageBean.DataBean bean = beanList.get(position);
        if (bean.status == 1) {
            FormBody.Builder body = new FormBody.Builder();
            body.add(Api.EnterLiveRoomBody[0], String.valueOf(bean.id));
            body.add(Api.EnterLiveRoomBody[1], String.valueOf(UserAccountUtils.getUserInfo
                    (getActivity().getApplication()).userId));
            body.add(Api.EnterLiveRoomBody[2], password);
            Log.d("Lpp", "enterRoom: " + String.valueOf(bean.id) + String.valueOf
                    (UserAccountUtils.getUserInfo
                            (getActivity().getApplication()).userId) + password);
            Request request = new Request.Builder()
                    .post(body.build())
                    .url(Api.EnterLiveRoomApi)
                    .header(Api.EnterLiveRoomHeader[0], Api.EnterLiveRoomHeader[1])
                    .addHeader("token", UserAccountUtils.getUserToken(getActivity()
                            .getApplication()).getToken())
                    .build();
            OkHttpUtils.requestHelper(request, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    UIUtils.showToast(getActivity(), "网络错误");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String msg = response.body().string();
                    Log.d("Lpp", "进入房间: " + msg);
                    if (msg.contains("token")) {
                        LoginStatusUtils.stateFailure(getActivity(), new LoginCallBack() {
                            @Override
                            public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                                UserAccountUtils.setUserToken(newToken, getActivity()
                                        .getApplication());
                            }
                        });
                        return;
                    }
                    EnterLiveVideoBean bean1 = new Gson().fromJson(msg, EnterLiveVideoBean.class);
                    if (bean1 == null) {
                        UIUtils.showToast(getActivity(), "未知错误");
                    } else {
                        if (bean1.status != 0) {
                            UIUtils.showToast(getActivity(), "密码错误");
                        } else {
                            enterRoomActivity(bean.id);
                        }
                    }
                }
            });
        } else {
            UIUtils.showToast(getActivity(), "次会议已结束，请刷新后重新查看");
        }
    }

    private void enterRoomActivity(int id) {
        int cRole = Constants.CLIENT_ROLE_BROADCASTER;
        Intent i = new Intent(getActivity(), LiveRoomActivity.class);
        i.putExtra(ConstantApp.ACTION_KEY_CROLE, cRole);
        i.putExtra(ConstantApp.ACTION_KEY_ROOM_NAME, String.valueOf(id));
        i.putExtra("liveId", String.valueOf(id));
        startActivity(i);
    }

    private void refresh() {
        if (UserAccountUtils.getUserToken(getActivity().getApplication()) == null) {
            refreshLayout.finishRefresh(false);
            return;
        }
        final Request request = new Request.Builder()
                .header(Api.GetVideoListHeader[0], Api.GetVideoListHeader[1])
                .addHeader("token", UserAccountUtils.getUserToken(getActivity().getApplication())
                        .getToken())
                .url(Api.GetVideoListApi)
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                refreshLayout.finishRefresh(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Log.d("Lpp", "List: " + msg);
                if (msg.contains("token过期")) {
                    LoginStatusUtils.stateFailure(getActivity(), new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            UserAccountUtils.getUserToken(getActivity().getApplication())
                                    .setToken(newToken.getToken());
                            UserAccountUtils.setUserInfo(newInfo, getActivity().getApplication());
                        }
                    });
                    refreshLayout.finishRefresh(false);
                    return;
                }
                QueryVideoMessageBean bean = new Gson().fromJson(msg, QueryVideoMessageBean.class);
                if (!(bean == null || bean.status != 0 || bean.data == null)) {
                    beanList.clear();
                    for (int i = 0; i < bean.data.size(); i++) {
                        if (bean.data.get(i).status == 1) {
                            beanList.add(bean.data.get(i));
                        }
                    }
                    Collections.sort(beanList, new Comparator<QueryVideoMessageBean.DataBean>() {
                        @Override
                        public int compare(QueryVideoMessageBean.DataBean o1,
                                           QueryVideoMessageBean.DataBean o2) {
                            return (int) (o2.startTime - o1.startTime);
                        }
                    });
                    notifyDataChanged();
                    refreshLayout.finishRefresh(true);
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
                if (videoListAdapter != null && beanList != null) {
                    if (beanList.size() == 0) {
                        rlHaveNothing.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        rlHaveNothing.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        videoListAdapter.notifyDataSetChanged();
                    }
                } else {
                    rlHaveNothing.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

}
