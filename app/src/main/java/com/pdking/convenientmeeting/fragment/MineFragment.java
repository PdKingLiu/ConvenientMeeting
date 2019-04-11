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
import com.pdking.convenientmeeting.activity.AboutAppActivity;
import com.pdking.convenientmeeting.activity.ModificationUserDataActivity;
import com.pdking.convenientmeeting.activity.AccountAndSafetyActivity;
import com.pdking.convenientmeeting.common.ActivityContainer;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.utils.OkHttpUtils;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * @author liupeidong
 * Created on 2019/1/30 17:53
 */
public class MineFragment extends Fragment implements View.OnClickListener {

    private static MineFragment INSTANCE = null;

    private RelativeLayout rlUserData;

    private RelativeLayout rlSettingSafety;

    private CircleImageView civUserIcon;

    private UserInfo userInfo;

    private File iconFile;

    private TextView tvUserEmail;

    private RelativeLayout rlAboutApp;

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
        rlSettingSafety = view.findViewById(R.id.rl_setting_safety);
        rlAboutApp = view.findViewById(R.id.rl_about_app);
        rlAboutApp.setOnClickListener(this);
        init();
        initListener();
    }

    private void init() {
        userInfo = LitePal.findAll(UserInfo.class).get(0);
        File file = new File(getContext().getExternalFilesDir(null), "/user/userIcon");
        if (!file.exists()) {
            file.mkdirs();
        }
        iconFile = new File(file, "user_icon_clip_" + userInfo.getPhone() + ".jpg");
    }

    private void initListener() {
        rlUserData.setOnClickListener(this);
        civUserIcon.setOnClickListener(this);
        rlSettingSafety.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_user_data:
            case R.id.civ_user_icon:
                getActivity().startActivityForResult(new Intent(getContext(),
                        ModificationUserDataActivity.class), UPDATE_USER_DATA);
                break;
            case R.id.rl_setting_safety:
                ActivityContainer.addActivity(getActivity());
                getActivity().startActivity(new Intent(getContext(), AccountAndSafetyActivity
                        .class));
                break;
            case R.id.rl_about_app:
                startActivity(new Intent(getContext(), AboutAppActivity.class));
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
                    tvUserEmail.setText("邮箱：" + info.email);
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
        if (iconFile.exists()) {
            Log.d("Lpp", "civUserIcon:file ");
            Glide.with(this)
                    .load(iconFile)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy
                            .NONE).skipMemoryCache(true))
                    .into(civUserIcon);
        } else {
            Log.d("Lpp", "civUserIcon:avatarUrl ");
            Request request = new Request.Builder()
                    .url(userInfo.avatarUrl)
                    .build();
            OkHttpUtils.requestHelper(request, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("Lpp", "onFailure: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    InputStream inputStream = null;
                    byte[] bytes = new byte[1024];
                    FileOutputStream fileOutputStream = null;
                    long current = 0;
                    int len;
                    try {
                        long total = response.body().contentLength();
                        inputStream = response.body().byteStream();
                        fileOutputStream = new FileOutputStream(iconFile);
                        while ((len = inputStream.read(bytes)) != -1) {
                            current += len;
                            fileOutputStream.write(bytes, 0, len);
                            Log.d("Lpp", "onResponse: " + current);
                        }
                        fileOutputStream.flush();
                        inputStream.close();
                        fileOutputStream.close();
                    } catch (Exception e) {
                        Log.d("Lpp", "onResponse:hh " + e.getMessage());
                    }
                }
            });
            Glide.with(this)
                    .load(iconFile)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy
                            .NONE).skipMemoryCache(true))
                    .into(civUserIcon);
        }
        tvUserEmail.setText(userInfo.email);
    }
}
