package com.pdking.convenientmeeting.fragment;

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
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.adapter.MeetingMineAdapter;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.MeetingMessage;
import com.pdking.convenientmeeting.db.MeetingMessageBean;
import com.pdking.convenientmeeting.db.OneMeetingRoomMessage;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.OkHttpUtils;
import com.pdking.convenientmeeting.weight.PopMenu;
import com.pdking.convenientmeeting.weight.PopMenuItem;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
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
    private UserToken userToken;
    private UserInfo userInfo;

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
                        Toast.makeText(getContext(), "请假", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getContext(), "查看详情", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
            }
        });
        mineAdapter.setMoreListener(new MeetingMineAdapter.OnMoreClickListener() {
            @Override
            public void onMoreClick(View view, int position) {
                int[] a = new int[2];
                view.getLocationInWindow(a);
                view.getLocationOnScreen(a);
                Log.d("Lpp", "onMoreClick:getLocationOnScreen " + a[0] + "-" + a[1]);
                Log.d("Lpp", "onMoreClick:getHeight" + view.getHeight());
//                if (a[1] > 2000) {
                int offsetX = -mPopMenu.getmPopupWindow().getContentView().getMeasuredWidth();
                int offsetY = 0;
//                    mPopMenu.showAsDropDown(view, offsetX,offsetY,Gravity.LEFT|Gravity.TOP);
//                } else {
//                    mPopMenu.showAsDropDown(view);
//                }
//                mPopMenu.getmPopupWindow().showAtLocation(view, Gravity.TOP | Gravity.LEFT, a[0],
//                        a[1]);
                mPopMenu.getmPopupWindow().showAsDropDown(view);
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
                .add("token", userToken.getToken())
                .add(Api.RequestUserMeetingListBody[0], userInfo.getUserId() + "")
                .add(Api.RequestUserMeetingListBody[1], 1 + "");
        Request request = new Request.Builder()
                .post(body.build())
                .header(Api.RequestUserMeetingListHeader[0], Api.RequestUserMeetingListHeader[1])
                .addHeader("token", userToken.getToken())
                .url(Api.RequestUserMeetingListApi)
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                refreshLayout.finishRefresh(3000, false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Log.d("Lpp", "onResponse: " + msg);
                MeetingMessageBean bean = new Gson().fromJson(msg, MeetingMessageBean.class);
                if (bean != null && bean.status == 0) {
                    for (MeetingMessage message : bean.data) {
                        message.meetingType = 1;
                    }
                    refreshLayout.finishRefresh(2000, true);
                    beanList.clear();
                    beanList.addAll(bean.data);
                    Log.d("Lpp", "onResponse: " + beanList.size());
                    LitePal.deleteAll(MeetingMessage.class, "meetingType = ?", "1");
                    LitePal.saveAll(beanList);
                    notifyDataChanged();
                } else {
                    refreshLayout.finishRefresh(2000, false);
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
        userInfo = LitePal.findAll(UserInfo.class).get(0);
        userToken = LitePal.findAll(UserToken.class).get(0);
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
                autoRefresh();
                break;
        }
    }
}
