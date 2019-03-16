package com.pdking.convenientmeeting.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.activity.ModificationUserDataActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author liupeidong
 * Created on 2019/1/30 17:53
 */
public class MineFragment extends Fragment implements View.OnClickListener {

    private static MineFragment INSTANCE = null;

    private RelativeLayout rlUserData;

    private CircleImageView civUserIcon;

    public static MineFragment getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new MineFragment();
        }
        return INSTANCE;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_minefragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        rlUserData = view.findViewById(R.id.rl_user_data);
        civUserIcon = view.findViewById(R.id.civ_user_icon);
        initListener();
    }

    private void initListener() {
        rlUserData.setOnClickListener(this);
        civUserIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d("Lpp", "onClick: ");
        switch (v.getId()) {
            case R.id.rl_user_data:
            case R.id.civ_user_icon:
                startActivity(new Intent(getContext(), ModificationUserDataActivity.class));
                break;
        }
    }
}
