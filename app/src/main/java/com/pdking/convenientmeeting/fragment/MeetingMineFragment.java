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
import android.widget.Toast;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.adapter.MeetingMineAdapter;
import com.pdking.convenientmeeting.db.MineMeetingBean;
import com.pdking.convenientmeeting.weight.PopMenu;
import com.pdking.convenientmeeting.weight.PopMenuItem;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class MeetingMineFragment extends Fragment {

    private static MeetingMineFragment meetingMineFragment;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private MeetingMineAdapter mineAdapter;
    private List<MineMeetingBean> beanList;
    private PopMenu mPopMenu;

    public MeetingMineFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public static MeetingMineFragment newInstance() {
        if (meetingMineFragment == null) {
            meetingMineFragment = new MeetingMineFragment();
        }
        return meetingMineFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initList();
        initMenu();
        initRecyclerAndFlush();
    }

    public SmartRefreshLayout getRefreshLayout() {
        return refreshLayout;
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
                refreshLayout.finishLoadMore();
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh();
            }
        });
    }

    private void initList() {
        beanList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MineMeetingBean mineMeetingBean = new MineMeetingBean();
            beanList.add(mineMeetingBean);
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
    }

}
