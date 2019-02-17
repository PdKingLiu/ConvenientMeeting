package com.pdking.convenientmeeting.activity;

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
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.haozhang.lib.SlantedTextView;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.ActivityContainer;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.utils.IOUtil;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.TitleView;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivityThree extends AppCompatActivity implements TitleView
        .LeftClickListener {
    private String TAG = "Lpp";
    private final int ALBUM_REQUEST = 1;
    private final int CLIP_REQUEST = 2;
    private final int CAMERA_REQUEST = 3;
    private final int FACE_REQUEST = 4;
    private final int FACE_ACTIVITY = 5;
    private Uri endClipUri;
    private Uri cameraFileUri;
    private File cameraSavePath;
    private Uri faceFileUri;
    private File faceSavePath;
    private File endClipFile;
    private UserInfo userInfo;
    private String emailRegex = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\" +
            ".[a-zA-Z0-9]{2,6}$";
    private boolean[] flags = {false, false};

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register_three);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        LitePal.getDatabase();
        ActivityContainer.addActivity(this);
        mTitleView.setLeftClickListener(this);
        btnLogin.setEnabled(false);
        userInfo = new UserInfo();
        userInfo.setPhoneNumber(getIntent().getStringExtra("phone_number"));
        userInfo.setPassword(getIntent().getStringExtra("password"));
        tvPhoneNumber.setText(userInfo.getPhoneNumber());
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
                faceSavePath = new File(fileFaceDir, "user_face_" + userInfo.getPhoneNumber() + "" +
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
                userInfo.setSex("男");
                break;
            default:
                userInfo.setSex("女");
                break;
        }
        userInfo.setName(etUserName.getText().toString());
        userInfo.setEmail(etUserEmail.getText().toString());
        Bitmap bitmap = null;
        try {
            if (endClipUri != null) {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), endClipUri);
            } else {
                bitmap = BitmapFactory.decodeResource(getResources(), R.id.civ_user_icon);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        userInfo.setIcon(bitmap);
//        if (cameraSavePath != null && cameraSavePath.exists()) {
//            cameraSavePath.delete();
//        }
        userInfo.save();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", userInfo);
        startActivity(intent);
        ActivityContainer.removeAllActivity();

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
        cameraSavePath = new File(file, "user_icon_camera_" + userInfo.getPhoneNumber() + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cameraFileUri = FileProvider.getUriForFile(RegisterActivityThree
                            .this, "com.pdking.convenientmeeting.fileprovider",
                    cameraSavePath);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            cameraFileUri = Uri.fromFile(cameraSavePath);
        }
        Log.d(TAG, "onClick: " + cameraFileUri);
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
                            Log.d(TAG, "onActivityResult:userInfoTem " + userInfoTem);
                            userInfo.setGenderInfo(userInfoTem.getGenderInfo());
                            userInfo.setLivenessInfo(userInfoTem.getLivenessInfo());
                            userInfo.setFaceInfo(userInfoTem.getFaceInfo());
                            userInfo.setFace3DAngle(userInfoTem.getFace3DAngle());
                            userInfo.setAgeInfo(userInfoTem.getAgeInfo());
                            userInfo.setFaceFeature(userInfoTem.getFaceFeature());
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
                        Glide.with(this).load(endClipUri).into(userImageView);
//                            userInfo.setIcon(BitmapFactory.decodeStream(getContentResolver()
//                                    .openInputStream(endClipUri)));
                        File f = new File(getExternalFilesDir(null) + "/user/userIcon");
                        if (!f.exists()) {
                            f.mkdirs();
                        }
                        File file = new File(f, "user_icon_clip_" + userInfo.getPhoneNumber()
                                + ".jpg");
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
        intent.putExtra("phone", userInfo.getPhoneNumber());
        startActivityForResult(intent, FACE_ACTIVITY);
    }

    private void goClip(Uri data) {
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/" +
                "meeting_user_icon_catch" + ".jpg";
        endClipFile = new File(filePath);
        endClipUri = Uri.parse("file:///" + filePath);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 调用系统中自带的图片剪裁
        intent.setDataAndType(data, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
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
        if (endClipFile.exists()) {
            endClipFile.delete();
        }
        super.onDestroy();
    }
}