package com.pdking.convenientmeeting.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.activity.MainActivity;
import com.pdking.convenientmeeting.weight.PopMenu;
import com.pdking.convenientmeeting.weight.PopMenuItem;
import com.pdking.convenientmeeting.weight.TitleView;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.ButterKnife;

/**
 * @author liupeidong
 * Created on 2019/1/30 17:49
 */
public class MeetingFragment extends Fragment{

    private static MeetingFragment INSTANCE = null;

    private PopMenu mPopMenu;

    private LinearLayout mLinearLayout;

    private View mView;

    private TabLayout mTabLayout;

    private String[] strings = {"我的", "会议室", "历史"};

    public static MeetingFragment getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new MeetingFragment();
        }
        return INSTANCE;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.layout_meetingfragment, container, false);
        initView(mView);
        initTabItem();
        return mView;
    }

    private void initTabItem() {
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            tab.setCustomView(R.layout.item_table_item);
            if (i == 1) {
                tab.select();
                tab.getCustomView().findViewById(R.id.tv_tab).setSelected(true);
                ((TextView) tab.getCustomView().findViewById(R.id.tv_tab)).setTextSize(17);
            }
            ((TextView) tab.getCustomView().findViewById(R.id.tv_tab)).setText(strings[i]);
        }
        mTabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabView(tab, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabView(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void updateTabView(TabLayout.Tab tab, boolean b) {
        if (b) {
            tab.select();
            ((TextView)tab.getCustomView().findViewById(R.id.tv_tab)).setTextSize(17);
        } else {
            ((TextView)tab.getCustomView().findViewById(R.id.tv_tab)).setTextSize(15);
        }
    }

    private void initView(View mView) {
        mLinearLayout = mView.findViewById(R.id.ll_pop);
        mTabLayout = mView.findViewById(R.id.tl_title);
        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WindowManager.LayoutParams wl = getActivity().getWindow().getAttributes();
                mPopMenu.showAsDropDown(mLinearLayout);
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                wl.alpha = 0.6f;
                getActivity().getWindow().setAttributes(wl);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMenu();
    }

    private void initMenu() {
        final WindowManager.LayoutParams wl = getActivity().getWindow().getAttributes();
        mPopMenu = new PopMenu(getContext());
        ArrayList<PopMenuItem> items = new ArrayList<>();
        items.add(new PopMenuItem(0, R.drawable.ic_pop_menu_sign, "会议签到"));
        items.add(new PopMenuItem(1, R.drawable.ic_pop_menu_book, "预定会议室"));

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
                        Toast.makeText(getContext(), "0", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

    }

}
