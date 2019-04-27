package com.pdking.convenientmeeting.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.activity.MeetingDetailsActivity;
import com.pdking.convenientmeeting.adapter.MeetingMineAdapter;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.MeetingMessage;
import com.pdking.convenientmeeting.db.MeetingMessageBean;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.OkHttpUtils;
import com.pdking.convenientmeeting.utils.UserAccountUtils;
import com.pdking.convenientmeeting.weight.PopMenu;
import com.pdking.convenientmeeting.weight.PopMenuItem;
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

public class MeetingMineFragment extends Fragment implements View.OnClickListener {

    private static MeetingMineFragment meetingMineFragment;
    private SmartRefreshLayout refreshLayout;
    private RelativeLayout rlHaveNothing;
    private RecyclerView recyclerView;
    private MeetingMineAdapter mineAdapter;
    private List<MeetingMessage> beanList;
    private PopMenu mPopMenu;

    public MeetingMineFragment() {
    }

    public static MeetingMineFragment newInstance() {
        if (meetingMineFragment == null) {
            meetingMineFragment = new MeetingMineFragment();
        }
        return meetingMineFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initList();
        initMenu();
        initRecyclerAndFlush();
    }

    private void initMenu() {
        final WindowManager.LayoutParams wl = getActivity().getWindow().getAttributes();
        mPopMenu = new PopMenu(getContext());
        ArrayList<PopMenuItem> items = new ArrayList<>();
        items.add(new PopMenuItem(0, R.mipmap.item_leave, "请假"));
        items.add(new PopMenuItem(1, R.mipmap.item_detail, "查看详情"));
        mPopMenu.setCornerVisible(false);
        mPopMenu.addItems(items);
        mPopMenu.getmPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                wl.alpha = 1f;
                (getActivity()).getWindow().setAttributes(wl);
            }
        });
        mPopMenu.setOnItemSelectedListener(new PopMenu.OnItemSelectedListener() {
            @Override
            public void selected(View view, PopMenuItem item, int position) {
                switch (item.id) {
                    case 0:
                        Toast.makeText(getContext(), "等待实现", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Intent intent = new Intent(getContext(), MeetingDetailsActivity.class);
                        intent.putExtra("meetingId", beanList.get(mPopMenu.getBindId()).meetingId
                                + "");
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void initRecyclerAndFlush() {
        mineAdapter = new MeetingMineAdapter(beanList, getContext());
        mineAdapter.setListener(new MeetingMineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getContext(), MeetingDetailsActivity.class);
                intent.putExtra("meetingId", beanList.get(position).meetingId + "");
                startActivity(intent);
            }
        });
        mineAdapter.setMoreListener(new MeetingMineAdapter.OnMoreClickListener() {
            @Override
            public void onMoreClick(View view, int position) {
                mPopMenu.showAsDropDown(view, position);
                WindowManager.LayoutParams wl = getActivity().getWindow().getAttributes();
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                wl.alpha = 0.6f;
                getActivity().getWindow().setAttributes(wl);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mineAdapter);
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
        FormBody.Builder body = new FormBody.Builder()
                .add(Api.RequestUserMeetingListBody[0], UserAccountUtils.getUserInfo(getActivity
                        ().getApplication()).getUserId() + "")
                .add(Api.RequestUserMeetingListBody[1], 1 + "");
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
                        message.meetingType = 1;
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
                    LitePal.deleteAll(MeetingMessage.class, "meetingType = ?", "1");
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
                if (mineAdapter != null && beanList != null) {
                    if (beanList.size() == 0) {
                        rlHaveNothing.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        rlHaveNothing.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        mineAdapter.notifyDataSetChanged();
                    }
                } else {
                    rlHaveNothing.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showToast(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initList() {
        beanList = new ArrayList<>();
        beanList = LitePal.where("meetingType = ?", "1").find(MeetingMessage.class);
        if (beanList.size() == 0) {
            rlHaveNothing.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            rlHaveNothing.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_meeting_mine, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        refreshLayout = view.findViewById(R.id.srl_flush);
        recyclerView = view.findViewById(R.id.rv_mine);
        rlHaveNothing = view.findViewById(R.id.rl_have_nothing);
        rlHaveNothing.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_have_nothing:
//                autoRefresh();
                autoRefresh();
                break;
        }
    }
}
