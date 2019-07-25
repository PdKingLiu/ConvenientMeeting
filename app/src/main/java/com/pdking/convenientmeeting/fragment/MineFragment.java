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
import com.pdking.convenientmeeting.activity.AccountAndSafetyActivity;
import com.pdking.convenientmeeting.activity.MeetingSettingActivity;
import com.pdking.convenientmeeting.activity.ModificationUserDataActivity;
import com.pdking.convenientmeeting.activity.MyUploadActivity;
import com.pdking.convenientmeeting.utils.OkHttpUtils;
import com.pdking.convenientmeeting.utils.UserAccountUtils;

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

    final private int UPDATE_USER_DATA = 1;
    private RelativeLayout rlUserData;
    private RelativeLayout rlSettingSafety;
    private RelativeLayout rlMeetingSetting;
    private RelativeLayout rlMyUpload;
    private CircleImageView civUserIcon;
    private File iconFile;
    private TextView tvUserEmail;
    private TextView tvUserName;
    private RelativeLayout rlAboutApp;
    private String email;
    private String avatarUrl;

    public static MineFragment getINSTANCE() {
        return new MineFragment();
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
        rlMeetingSetting = view.findViewById(R.id.rl_meeting_setting);
        rlMyUpload = view.findViewById(R.id.rl_my_upload);
        tvUserName = view.findViewById(R.id.tv_user_name);
        rlAboutApp = view.findViewById(R.id.rl_about_app);
        init();
        initListener();
    }

    private void init() {
        File file = new File(getContext().getExternalFilesDir(null), "/user/userIcon");
        if (!file.exists()) {
            file.mkdirs();
        }
        iconFile = new File(file, "user_icon_clip_" + UserAccountUtils.getUserInfo(getActivity()
                .getApplication()).getPhone() + ".jpg");
    }

    private void initListener() {
        rlAboutApp.setOnClickListener(this);
        rlUserData.setOnClickListener(this);
        civUserIcon.setOnClickListener(this);
        rlSettingSafety.setOnClickListener(this);
        rlMeetingSetting.setOnClickListener(this);
        rlMyUpload.setOnClickListener(this);
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
                getActivity().startActivity(new Intent(getContext(), AccountAndSafetyActivity
                        .class));
                break;
            case R.id.rl_about_app:
                startActivity(new Intent(getContext(), AboutAppActivity.class));
                break;
            case R.id.rl_meeting_setting:
                startActivity(new Intent(getContext(), MeetingSettingActivity.class));
                break;
            case R.id.rl_my_upload:
                startActivity(new Intent(getContext(), MyUploadActivity.class));
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean flag[] = {false, false};
                if (!email.equals(UserAccountUtils.getUserInfo(getActivity().getApplication())
                        .email)) {
                    tvUserEmail.setText("邮箱：" + UserAccountUtils.getUserInfo(getActivity()
                            .getApplication()).email);
                    flag[0] = true;
                }
                if (!avatarUrl.equals(UserAccountUtils.getUserInfo(getActivity().getApplication()
                ).avatarUrl)) {
                    try {
                        Bitmap bitmap = new Compressor(getContext()).compressToBitmap(iconFile);
                        civUserIcon.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    flag[1] = true;
                }
                if (flag[0] || flag[1]) {
                    email = UserAccountUtils.getUserInfo(getActivity().getApplication()).email;
                    avatarUrl = UserAccountUtils.getUserInfo(getActivity().getApplication())
                            .avatarUrl;
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        email = UserAccountUtils.getUserInfo(getActivity().getApplication()).getEmail();
        avatarUrl = UserAccountUtils.getUserInfo(getActivity().getApplication()).avatarUrl;
        loadUserData();
    }

    private void loadUserData() {
        if (iconFile.exists()) {
            Glide.with(this)
                    .load(iconFile)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy
                            .NONE).skipMemoryCache(true))
                    .into(civUserIcon);
        } else {
            Request request = new Request.Builder()
                    .url(UserAccountUtils.getUserInfo(getActivity().getApplication()).avatarUrl)
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
        tvUserEmail.setText(UserAccountUtils.getUserInfo(getActivity().getApplication()).email);
        tvUserName.setText(UserAccountUtils.getUserInfo(getActivity().getApplication()).username);
    }

}
