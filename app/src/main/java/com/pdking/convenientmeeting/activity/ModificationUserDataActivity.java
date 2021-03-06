package com.pdking.convenientmeeting.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.RequestReturnBean;
import com.pdking.convenientmeeting.db.UserDataBean;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.IOUtil;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.utils.UserAccountUtils;
import com.pdking.convenientmeeting.weight.TitleView;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModificationUserDataActivity extends AppCompatActivity implements TitleView
        .LeftClickListener, TitleView.RightClickListener {

    private final int ALBUM_REQUEST = 1;
    private final int CLIP_REQUEST = 2;
    private final int CAMERA_REQUEST = 3;
    public boolean error = false;
    @BindView(R.id.tv_net_error)
    RelativeLayout tvNetError;
    @BindView(R.id.title)
    TitleView title;
    @BindView(R.id.nsv_data_view)
    NestedScrollView nsvDataView;
    @BindView(R.id.civ_user_icon)
    CircleImageView civUserIcon;
    @BindView(R.id.ed_modification_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_modification_phone_number)
    TextView tvUserPhone;
    @BindView(R.id.ed_modification_email)
    EditText edUserEmail;
    @BindView(R.id.rg_sex)
    RadioGroup radioGroup;
    private ProgressDialog dialog;
    private AlertDialog dialogLeave;
    private UserDataBean dataBean;
    private boolean iconUpdateFlag = false;
    private boolean iconUpdateFlag2 = false;
    private Uri clipUri;
    private Uri cameraUri;
    private File clipFile;
    private File cameraFile;
    private boolean[] updateFlag = {false, false, false};

    @OnClick(R.id.civ_user_icon)
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.civ_user_icon:
                goChangeUserIcon();
                break;
        }
    }

    private void goChangeUserIcon() {
        Dialog dialogChangeByWhichItem = new AlertDialog.Builder(ModificationUserDataActivity.this)
                .setCancelable(true)
                .setItems(new String[]{"相册", "相机", "取消"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                goAlbum();
                                break;
                            case 1:
                                goCamera();
                                break;
                            case 2:
                            default:
                                break;
                        }
                    }
                }).create();
        dialogChangeByWhichItem.show();
    }

    private void goAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, ALBUM_REQUEST);
    }


    private void goCamera() {
        File file = new File(getExternalFilesDir(null), "/user/userIcon");
        if (!file.exists()) {
            file.mkdirs();
        }
        cameraFile = new File(file, "user_icon_camera_" + UserAccountUtils.getUserInfo
                (getApplication()).getPhone() + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cameraUri = FileProvider.getUriForFile(ModificationUserDataActivity
                            .this, "com.pdking.convenientmeeting.fileprovider",
                    cameraFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            cameraUri = Uri.fromFile(cameraFile);
        }
        Log.d("Lpp", "onClick: " + cameraUri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case ALBUM_REQUEST:
                if (resultCode == RESULT_OK && data != null) {
                    goClip(data.getData());
                }
                break;
            case CLIP_REQUEST:
                if (resultCode == RESULT_OK) {
                    try {
                        Glide.with(this)
                                .load(clipFile)
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy
                                        .NONE).skipMemoryCache(true))
                                .into(civUserIcon);
                        updateFlag[2] = true;
                        iconUpdateFlag2 = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {
                    goClip(cameraUri);
                }
                break;
        }
    }

    private void goClip(Uri data) {
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/" +
                "meeting_user_icon_catch" + ".jpg";
        clipFile = new File(filePath);
        if (clipFile.exists()) {
            clipFile.delete();
        }
        clipUri = Uri.parse("file:///" + filePath);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 调用系统中自带的图片剪裁
        intent.setDataAndType(data, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 400);
        intent.putExtra("aspectY", 400);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 400);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, clipUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", false);
        startActivityForResult(intent, CLIP_REQUEST);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_modification_user_data);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        LitePal.getDatabase();
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在加载...");
        dialog.setTitle("加载中");
        dialog.setCancelable(false);
        title.setLeftClickListener(this);
        title.setRightClickListener(this);
        dialogLeave = new AlertDialog.Builder(ModificationUserDataActivity.this)
                .setCancelable(false)
                .setTitle("退出将不会做任何保存，确定要退出吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create();
        requestUserData();
    }

    private void saveUserData() {
        String email = UserAccountUtils.getUserInfo(getApplication()).getEmail();
        String emailRegex = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\" +
                ".[a-zA-Z0-9]{2,6}$";
        String sex = UserAccountUtils.getUserInfo(getApplication()).getSex();
        switch (edUserEmail.getText().toString()) {
            case "":
                email = "";
                break;
            default:
                if (!edUserEmail.getText().toString().matches(emailRegex)) {
                    Toast.makeText(this, "请输入正确的邮箱", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    email = edUserEmail.getText().toString();
                }
                break;
        }
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.male:
                sex = "man";
                break;
            default:
                sex = "woman";
                break;
        }
        if (!isHaveChange()) {
            Toast.makeText(this, "未作任何修改", Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressBar();
        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder body = new MultipartBody.Builder();
        body.addFormDataPart(Api.UpDateUserInfoBody[0], UserAccountUtils.getUserInfo
                (getApplication()).getUserId() + "");
        body.addFormDataPart(Api.UpDateUserInfoBody[1], UserAccountUtils.getUserInfo
                (getApplication()).getPhone() + "");
        body.addFormDataPart(Api.UpDateUserInfoBody[2], email);
        body.addFormDataPart(Api.UpDateUserInfoBody[3], sex);
        if (updateFlag[2]) {
            body.addFormDataPart(Api.UpDateUserInfoBody[4], clipFile.getName(), RequestBody.create
                    (MediaType.parse("image/jpeg"), clipFile));
        }
        final Request request = new Request.Builder()
                .url(Api.UpDateUserInfoApi)
                .post(body.build())
                .header("token", UserAccountUtils.getUserToken(getApplication()).getToken())
                .addHeader(Api.UpDateUserInfoHeader[0], Api.UpDateUserInfoHeader[1])
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideProgressBar();
                showToast("修改失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(ModificationUserDataActivity.this, new
                            LoginCallBack() {
                                @Override
                                public void newMessageCallBack(UserInfo newInfo, UserToken
                                        newToken) {
                                    UserAccountUtils.setUserInfo(newInfo, getApplication());
                                    UserAccountUtils.setUserToken(newToken, getApplication());
                                }
                            });
                    return;
                }
                RequestReturnBean bean = new Gson().fromJson(msg, RequestReturnBean.class);
                if (bean.status == 1) {
                    hideProgressBar();
                    showToast("修改失败");
                } else {
                    requestNewData();
                }
            }
        });

    }

    private void requestNewData() {
        updateFlag[0] = false;
        updateFlag[1] = false;
        updateFlag[2] = false;
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.GetUserInfoBody[0], UserAccountUtils.getUserInfo(getApplication()).getPhone());
        Request request = new Request.Builder()
                .url(Api.GetUserInfoApi)
                .addHeader("token", UserAccountUtils.getUserToken(getApplication()).getToken())
                .post(body.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Lpp", "失败: " + e.getMessage());
                hideProgressBar();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideProgressBar();
                String msg = response.body().string();
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(ModificationUserDataActivity.this, new
                            LoginCallBack() {
                                @Override
                                public void newMessageCallBack(UserInfo newInfo, UserToken
                                        newToken) {
                                    UserAccountUtils.setUserInfo(newInfo, getApplication());
                                    UserAccountUtils.setUserToken(newToken, getApplication());
                                }
                            });
                    return;
                }
                dataBean = new Gson().fromJson(msg, UserDataBean.class);
                if (dataBean.status == 1) {
                    showToast("失败");
                    Toast.makeText(ModificationUserDataActivity.this, "失败", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    LitePal.deleteAll(UserInfo.class);
                    dataBean.data.save();
                    UserAccountUtils.setUserInfo(dataBean.data, getApplication());
                    Intent intent = new Intent();
                    intent.putExtra("status", 1);
                    setResult(RESULT_OK, intent);
                    showToast("修改成功");
                    iconUpdateFlag = true;
                    saveNewIcon();
                }
            }
        });

    }

    private void saveNewIcon() {
        if (iconUpdateFlag2 && iconUpdateFlag) {
            try {
                File f = new File(getExternalFilesDir(null) + "/user/userIcon");
                if (!f.exists()) {
                    f.mkdirs();
                }
                File file = new File(f, "user_icon_clip_" + UserAccountUtils.getUserInfo
                        (getApplication()).getPhone()
                        + ".jpg");
                if (file.exists()) {
                    file.delete();
                }
                IOUtil.copyFile(clipFile, file);
                if (cameraFile != null && cameraFile.exists()) {
                    cameraFile.delete();
                }
                if (clipFile != null && clipFile.exists()) {
                    clipFile.delete();
                }
            } catch (IOException e) {
                e.getMessage();
                Log.d("Lpp", "e.getMessage(): " + e.getMessage());
            }
        }
    }

    private boolean isHaveChange() {
        String email = UserAccountUtils.getUserInfo(getApplication()).getEmail();
        String sex = UserAccountUtils.getUserInfo(getApplication()).getSex();
        email = edUserEmail.getText().toString();
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.male:
                sex = "man";
                break;
            default:
                sex = "woman";
                break;
        }
        if (!email.equals(UserAccountUtils.getUserInfo(getApplication()).email)) {
            updateFlag[0] = true;
        }
        if (!sex.equals(UserAccountUtils.getUserInfo(getApplication()).sex)) {
            updateFlag[1] = true;
        }
        return (updateFlag[0] || updateFlag[1] || updateFlag[2]);
    }

    @Override
    public void onBackPressed() {
        Log.d("Lpp", "onBackPressed: ");
        OnLeftButtonClick();
    }

    private void requestUserData() {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.GetUserInfoBody[0], UserAccountUtils.getUserInfo(getApplication()).getPhone());
        Request request = new Request.Builder()
                .url(Api.GetUserInfoApi)
                .addHeader("token", UserAccountUtils.getUserToken(getApplication()).getToken())
                .post(body.build())
                .build();
        showProgressBar();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Lpp", "失败: " + e.getMessage());
                hideProgressBar();
                setErrorPage(true);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideProgressBar();
                String msg = response.body().string();
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(ModificationUserDataActivity.this, new
                            LoginCallBack() {
                                @Override
                                public void newMessageCallBack(UserInfo newInfo, UserToken
                                        newToken) {
                                    UserAccountUtils.setUserInfo(newInfo, getApplication());
                                    UserAccountUtils.setUserToken(newToken, getApplication());
                                }
                            });
                    return;
                }
                Log.d("Lpp", "onResponse: " + msg);
                dataBean = new Gson().fromJson(msg, UserDataBean.class);
                if (dataBean == null || dataBean.status == 1 || dataBean.data == null) {
                    setErrorPage(true);
                } else {
                    setErrorPage(false);
                    setPageData();
                }
            }
        });
    }

    private void setPageData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(ModificationUserDataActivity.this).load(dataBean.data.avatarUrl).into
                        (civUserIcon);
                tvUserName.setText(dataBean.data.username);
                if (dataBean.data.sex.equals("man")) {
                    radioGroup.check(R.id.male);
                } else {
                    radioGroup.check(R.id.femle);
                }
                tvUserPhone.setText(dataBean.data.phone);
                if (dataBean.data.email.equals("")) {
                    edUserEmail.setText("");
                    edUserEmail.setHint("请输入邮箱");
                } else {
                    edUserEmail.setText(dataBean.data.email);
                    edUserEmail.setHint("原邮箱：" + dataBean.data.email);
                }
            }
        });
    }

    private void setErrorPage(final boolean isError) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isError) {
                    error = true;
                    title.setLeftMenuTextVisible(false);
                    title.setRightMenuTextVisible(false);
                    tvNetError.setVisibility(View.VISIBLE);
                    nsvDataView.setVisibility(View.GONE);
                } else {
                    error = false;
                    title.setLeftMenuTextVisible(true);
                    title.setRightMenuTextVisible(true);
                    tvNetError.setVisibility(View.GONE);
                    nsvDataView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ModificationUserDataActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.hide();
                }
            }
        });
    }

    private void showProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.show();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.hide();
            }
            dialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void OnLeftButtonClick() {
        Log.d("Lpp", "onBackPressed: ");
        if (error) {
            finish();
            Log.d("Lpp", "onBackPressed: ");
            return;
        }
        if (isHaveChange()) {
            dialogLeave.show();
        } else {
            finish();
        }
    }

    @Override
    public void OnRightButtonClick() {
        saveUserData();
    }
}
