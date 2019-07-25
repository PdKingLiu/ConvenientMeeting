package com.pdking.convenientmeeting.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.haozhang.lib.SlantedTextView;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.RequestReturnBean;
import com.pdking.convenientmeeting.db.UserAccount;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.utils.IOUtil;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.TitleView;

import org.litepal.LitePal;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivityThree extends AppCompatActivity implements TitleView
        .LeftClickListener {
    private final int ALBUM_REQUEST = 1;
    private final int CLIP_REQUEST = 2;
    private final int CAMERA_REQUEST = 3;
    private final int FACE_REQUEST = 4;
    private final int FACE_ACTIVITY = 5;
    @BindView(R.id.title)
    TitleView mTitleView;
    @BindView(R.id.civ_user_icon)
    CircleImageView userImageView;
    @BindView(R.id.tv_phone_number)
    TextView tvPhoneNumber;
    @BindView(R.id.rg_sex)
    RadioGroup radioGroup;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.ed_register_user_name)
    EditText etUserName;
    @BindView(R.id.ed_register_email)
    EditText etUserEmail;
    @BindView(R.id.tv_start_get_face)
    TextView tvGetFace;
    @BindView(R.id.stv_status_get_face)
    SlantedTextView stvGetFaceStatus;
    AlertDialog dialog;
    private String TAG = "Lpp";
    private Uri endClipUri;
    private Uri faceFileUri;
    private Uri cameraFileUri;
    private File cameraSavePath;
    private File faceSavePath;
    private File endClipFile;
    private UserInfo userInfo;
    private String emailRegex = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\" +
            ".[a-zA-Z0-9]{2,6}$";
    private boolean[] flags = {false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register_three);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        applyPermission();
        endClipUri = null;
        mTitleView.setLeftClickListener(this);
        btnLogin.setEnabled(false);
        dialog = new AlertDialog.Builder(this)
                .setView(new ProgressBar(this))
                .setCancelable(false)
                .create();
        userInfo = new UserInfo();
        userInfo.setPhone(getIntent().getStringExtra("phone_number"));
        userInfo.setPassword(getIntent().getStringExtra("password"));
        tvPhoneNumber.setText(userInfo.getPhone());
    }

    @OnTextChanged(R.id.ed_register_user_name)
    void onTextChanged(CharSequence s) {
        if (s.length() > 0) {
            flags[0] = true;
        } else {
            flags[0] = false;
        }
        changeButtonStatus();
    }

    private void changeButtonStatus() {
        if (flags[0] && flags[1]) {
            btnLogin.setEnabled(true);
        } else {
            btnLogin.setEnabled(false);
        }
    }

    @OnClick({R.id.civ_user_icon, R.id.tv_start_get_face, R.id.btn_login})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                startLogin();
                break;
            case R.id.tv_start_get_face:
                startGetFace();
                break;
            case R.id.civ_user_icon:
                changeUserIcon();
                break;
            default:
                break;
        }
    }

    private void startGetFace() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setNegativeButton("取消".subSequence(0, 2), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton("确定".subSequence(0, 2), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File fileFaceDir = new File(getExternalFilesDir(null) + "/user/userFace");
                if (!fileFaceDir.exists()) {
                    fileFaceDir.mkdirs();
                }
                faceSavePath = new File(fileFaceDir, "user_face_" + userInfo.getPhone() + "" +
                        ".jpg");
                try {
                    if (faceSavePath.exists()) {
                        faceSavePath.delete();
                    }
                    faceSavePath.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra("camerasensortype", 2);
                intent.putExtra("autofocus", true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    faceFileUri = FileProvider.getUriForFile(RegisterActivityThree
                                    .this, "com.pdking.convenientmeeting.fileprovider",
                            faceSavePath);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    faceFileUri = Uri.fromFile(faceSavePath);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, faceFileUri);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                startActivityForResult(intent, FACE_REQUEST);
            }
        });
        dialog.setCancelable(false);
        dialog.setMessage("即将开启相机，请确保只有一个人脸并且不是图片".subSequence(0, 16));
        dialog.show();

    }

    private void applyPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager
                .PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
                    .CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void startLogin() {
        switch (etUserEmail.getText().toString()) {
            case "":
                userInfo.setEmail("");
                break;
            default:
                if (!etUserEmail.getText().toString().matches(emailRegex)) {
                    Toast.makeText(this, "请输入正确的邮箱", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    userInfo.setEmail(etUserEmail.getText().toString());
                }
                break;
        }
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.male:
                userInfo.setSex("man");
                break;
            default:
                userInfo.setSex("woman");
                break;
        }
        userInfo.setUsername(etUserName.getText().toString());
        userInfo.setEmail(etUserEmail.getText().toString());
        if (cameraSavePath != null && cameraSavePath.exists()) {
            cameraSavePath.delete();
        }
        requestRegister(userInfo);

    }

    private void requestRegister(final UserInfo userInfo) {
        File iconFile = null;
        File faceSavePath2 = null;
        try {
            if (endClipUri == null) {
                File f = new File(getExternalFilesDir(null) + "/user/userIcon");
                if (!f.exists()) {
                    f.mkdirs();
                }
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap
                        .user_default_icon);
                iconFile = new File(f, "user_icon_clip_" + userInfo.getPhone()
                        + ".jpg");
                if (iconFile.exists()) {
                    iconFile.delete();
                }
                iconFile.createNewFile();
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new
                        FileOutputStream(iconFile));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bufferedOutputStream);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();


            } else {
                /* iconFile = new Compressor(this).compressToFile(endClipFile);*/
                iconFile = endClipFile;
            }

            faceSavePath2 = new Compressor(this).compressToFile(faceSavePath);
        } catch (Exception e) {
        }


        OkHttpClient client = new OkHttpClient();
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(Api.RegisterBody[0], userInfo.getUsername())
                .addFormDataPart(Api.RegisterBody[1], userInfo.getPassword())
                .addFormDataPart(Api.RegisterBody[2], userInfo.getSex())
                .addFormDataPart(Api.RegisterBody[3], userInfo.getPhone())
                .addFormDataPart(Api.RegisterBody[4], userInfo.getFaceData())
                .addFormDataPart(Api.RegisterBody[5], userInfo.getEmail())
                .addFormDataPart(Api.RegisterBody[6], iconFile.getName(), RequestBody.create
                        (MediaType.parse("image/jpeg"), iconFile))
                .addFormDataPart(Api.RegisterBody[7], faceSavePath2.getName(), RequestBody.create
                        (MediaType.parse("image/jpeg"), faceSavePath2))
                .build();
        Request request = new Request.Builder()
                .url(Api.RegisterApi)
                .post(multipartBody)
                .header(Api.RegisterHeader[0], Api.RegisterHeader[1])
                .build();
        showDialog();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideDialog();
                showToast("注册失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                hideDialog();
                RequestReturnBean bean = new Gson().fromJson(string, RequestReturnBean.class);
                if (bean.status == 0) {
                    showToast("注册成功，请登录");
                    UserAccount account = new UserAccount(userInfo.getPhone(), userInfo
                            .getPassword());
                    LitePal.deleteAll(UserAccount.class);
                    account.save();
                    Intent intent = new Intent(RegisterActivityThree.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    showToast("注册失败，该账号可能已经注册");
                }
            }
        });
    }

    public void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegisterActivityThree.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null && dialog.isShowing()) {
                    dialog.hide();
                }
            }
        });
    }

    private void showDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null && !dialog.isShowing()) {
                    dialog.show();
                }
            }
        });
    }

    /**
     * 更改用户头像
     */
    private void changeUserIcon() {
        CharSequence[] charSequence = {"拍照", "相册", "取消"};
        new AlertDialog.Builder(this).setItems(charSequence, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        goCamera();
                        break;
                    case 1:
                        goAlbum();
                        break;
                    case 2:
                    default:
                        break;
                }
            }
        }).setCancelable(true).show();

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
        cameraSavePath = new File(file, "user_icon_camera_" + userInfo.getPhone() + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cameraFileUri = FileProvider.getUriForFile(RegisterActivityThree
                            .this, "com.pdking.convenientmeeting.fileprovider",
                    cameraSavePath);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            cameraFileUri = Uri.fromFile(cameraSavePath);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CAMERA_REQUEST);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "请先确认权限", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case 2:
                break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case FACE_ACTIVITY:
                if (data != null) {
                    int status = data.getIntExtra("status", -1);
                    switch (status) {
                        case -1:
                            Toast.makeText(this, "采集失败", Toast.LENGTH_SHORT).show();
                            tvGetFace.setText("采集失败，请重新采集");
                            stvGetFaceStatus.setText("上传失败");
                            stvGetFaceStatus.setSlantedBackgroundColor(Color.GRAY);
                            flags[1] = false;
                            changeButtonStatus();
                            break;
                        case 1:
                            Toast.makeText(this, "采集成功", Toast.LENGTH_SHORT).show();
                            tvGetFace.setText("采集成功");
                            stvGetFaceStatus.setText("上传成功");
                            stvGetFaceStatus.setSlantedBackgroundColor(0xff669900);
                            flags[1] = true;
                            UserInfo userInfoTem = data.getParcelableExtra("user");
                            userInfo.setFaceData(userInfoTem.getFaceData());
                            changeButtonStatus();
                            break;
                    }
                }

                break;
            case FACE_REQUEST:
                if (resultCode == RESULT_OK) {
                    goFaceResult(faceFileUri);
                }
                break;
            case ALBUM_REQUEST:
                if (resultCode == RESULT_OK && data != null) {
                    goClip(data.getData());
                }
                break;
            case CLIP_REQUEST:
                if (resultCode == RESULT_OK) {
                    try {
                        Glide.with(this)
                                .load(endClipFile)
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy
                                        .NONE).skipMemoryCache(true))
                                .into(userImageView);
                            /*userInfo.setIcon(BitmapFactory.decodeStream(getContentResolver()
                                    .openInputStream(endClipUri)));*/
                        File f = new File(getExternalFilesDir(null) + "/user/userIcon");
                        if (!f.exists()) {
                            f.mkdirs();
                        }
                        File file = new File(f, "user_icon_clip_" + userInfo.getPhone()
                                + ".jpg");
                        if (file.exists()) {
                            file.delete();
                        }
                        IOUtil.copyFile(endClipFile, file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {
                    goClip(cameraFileUri);
                }
                break;
            default:
                break;
        }

    }

    private void goFaceResult(Uri faceFileUri) {
        Intent intent = new Intent(this, ShowFaceResultActivity.class);
        intent.putExtra("phone", userInfo.getPhone());
        startActivityForResult(intent, FACE_ACTIVITY);
    }

    private void goClip(Uri data) {
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/" +
                "meeting_user_icon_catch" + ".jpg";
        endClipFile = new File(filePath);
        if (endClipFile.exists()) {
            endClipFile.delete();
        }
        endClipUri = Uri.parse("file:///" + filePath);
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
        intent.putExtra(MediaStore.EXTRA_OUTPUT, endClipUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", false);
        startActivityForResult(intent, CLIP_REQUEST);
    }

    @Override
    public void OnLeftButtonClick() {
        finish();
    }

    @Override
    protected void onDestroy() {
        endClipUri = null;
        if (endClipFile != null && endClipFile.exists()) {
            endClipFile.delete();
        }
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.hide();
            }
            dialog.dismiss();
        }
        super.onDestroy();
    }
}