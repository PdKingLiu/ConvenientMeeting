package com.pdking.convenientmeeting.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.activity.MainActivity;
import com.pdking.convenientmeeting.weight.PopMenu;
import com.pdking.convenientmeeting.weight.PopMenuItem;
import com.pdking.convenientmeeting.weight.TitleView;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author liupeidong
 * Created on 2019/1/30 17:49
 */
public class MeetingFragment extends Fragment{

    private static MeetingFragment INSTANCE = null;

    private PopMenu mPopMenu;

    private TitleView mTitleView;

    private View mView;

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
        return mView;
    }

    private void initView(View mView) {
        mTitleView = mView.findViewById(R.id.title);
        mTitleView.setRightClickListener(new TitleView.RightClickListener() {
            @Override
            public void OnRightButtonClick() {
                WindowManager.LayoutParams wl = getActivity().getWindow().getAttributes();
                mPopMenu.showAsDropDown(mTitleView.getBtnMenu());
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
