package com.pdking.convenientmeeting.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.activity.ModificationUserDataActivity;
import com.pdking.convenientmeeting.db.UserInfo;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

/**
 * @author liupeidong
 * Created on 2019/1/30 17:53
 */
public class MineFragment extends Fragment implements View.OnClickListener {

    private static MineFragment INSTANCE = null;

    private RelativeLayout rlUserData;

    private CircleImageView civUserIcon;

    private UserInfo userInfo;

    private File iconFile;

    private TextView tvUserEmail;

    final private int UPDATE_USER_DATA = 1;

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
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        initListener();
    }

    private void initListener() {
        rlUserData.setOnClickListener(this);
        civUserIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_user_data:
            case R.id.civ_user_icon:
                getActivity().startActivityForResult(new Intent(getContext(),
                        ModificationUserDataActivity
                                .class), UPDATE_USER_DATA);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case UPDATE_USER_DATA:
                if (resultCode == RESULT_OK && data != null) {
                    int status = data.getIntExtra("status", -1);
                    if (status == 1) {
                        changeFragmentUI();
                    }
                }
        }
    }

    private void changeFragmentUI() {
        final UserInfo info = LitePal.findAll(UserInfo.class).get(0);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean flag[] = {false, false};
                if (!info.email.equals(userInfo.email)) {
                    tvUserEmail.setText("邮箱：" + userInfo.email);
                    flag[0] = true;
                }
                if (!info.avatarUrl.equals(userInfo.avatarUrl)) {
                    try {
                        Bitmap bitmap = new Compressor(getContext()).compressToBitmap(iconFile);
                        civUserIcon.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    flag[1] = true;
                }
                if (flag[0] || flag[1]) {
                    userInfo = info;
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadUserData();
    }

    private void loadUserData() {
        userInfo = LitePal.findAll(UserInfo.class).get(0);
        iconFile = new File(getContext().getExternalFilesDir(null) + "/user/userIcon",
                "user_icon_clip_" + userInfo.getPhone() + ".jpg");
        if (iconFile.exists()) {
            Log.d("Lpp", "civUserIcon:file ");
            Glide.with(this)
                    .load(iconFile)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy
                            .NONE).skipMemoryCache(true))
                    .into(civUserIcon);
        } else {
            Log.d("Lpp", "civUserIcon:avatarUrl ");
            Glide.with(this)
                    .load(userInfo.avatarUrl)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy
                            .NONE).skipMemoryCache(true))
                    .into(civUserIcon);
        }
        tvUserEmail.setText(userInfo.email);
    }
}
