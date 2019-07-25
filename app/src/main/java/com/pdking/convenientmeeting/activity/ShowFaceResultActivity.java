package com.pdking.convenientmeeting.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.ParcelableSpan;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.Face3DAngle;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.VersionInfo;
import com.bumptech.glide.Glide;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.Constant;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.utils.ImageUtil;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.TitleView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ShowFaceResultActivity extends AppCompatActivity implements TitleView
        .LeftClickListener {

    @BindView(R.id.title)
    TitleView mTitleView;

    @BindView(R.id.btn_ok)
    Button btnOk;

    @BindView(R.id.iv_face)
    ImageView ivFace;

    @BindView(R.id.tv_status)
    TextView tvStatus;
    String TAG = "Lpp";
    private AlertDialog progressDialog;
    private Bitmap mBitmap;
    private FaceEngine faceEngine;

    private int faceEngineCode = -1;

    private boolean flag = false;

    private UserInfo userInfo;

    private void initEngine() {
        faceEngine = new FaceEngine();
        faceEngineCode = faceEngine.init(this, FaceEngine.ASF_DETECT_MODE_IMAGE, FaceEngine
                        .ASF_OP_0_HIGHER_EXT,
                16, 10, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT | FaceEngine
                        .ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE |
                        FaceEngine.ASF_LIVENESS);
        VersionInfo versionInfo = new VersionInfo();
        faceEngine.getVersion(versionInfo);
        Log.i(TAG, "initEngine: init: " + faceEngineCode + "  version:" + versionInfo);


        if (faceEngineCode != ErrorInfo.MOK) {
            Toast.makeText(this, "引擎初始化失败，错误码为 " + faceEngineCode, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "initEngine: " + "引擎初始化失败，错误码为 " + faceEngineCode);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_show_face_result);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        mTitleView.setLeftClickListener(this);
        activeEngine();
        File file = new File(getExternalFilesDir(null) + "/user/userFace", "user_face_" +
                getIntent().getStringExtra("phone") + ".jpg");
        btnOk.setEnabled(false);
        try {
            mBitmap = new Compressor(this).compressToBitmap(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Glide.with(this).load(mBitmap).into(ivFace);
        progressDialog = new AlertDialog.Builder(this)
                .setView(new ProgressBar(this))
                .create();

    }

    private void activeEngine() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                FaceEngine faceEngine = new FaceEngine();
                int activeCode = faceEngine.active(ShowFaceResultActivity.this, Constant
                        .APP_ID_SMS, Constant.SDK_KEY_SMS);
                emitter.onNext(activeCode);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer activeCode) {
                        if (activeCode == ErrorInfo.MOK) {
                            btnOk.setEnabled(true);
                            initEngine();
                        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            btnOk.setEnabled(true);
                            initEngine();
                        } else {
                            btnOk.setEnabled(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    @OnClick({R.id.btn_ok})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                if (flag) {
                    finish();
                } else {
                    startRecognition(view);
                }
                break;
        }
    }

    private void startRecognition(final View view) {
        view.setClickable(false);
        if (progressDialog == null || progressDialog.isShowing()) {
            return;
        }
        progressDialog.show();
        //图像转化操作和部分引擎调用比较耗时，建议放子线程操作
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                processImage();
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        view.setClickable(true);
                    }
                });

    }

    /**
     * 主要操作逻辑部分
     */
    public void processImage() {
        /**
         * 1.准备操作（校验，显示，获取BGR）
         */
        if (mBitmap == null) {
            Toast.makeText(this, "失败", Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);

        final SpannableStringBuilder notificationSpannableStringBuilder = new
                SpannableStringBuilder();
        if (faceEngineCode != ErrorInfo.MOK) {
            return;
        }
        if (bitmap == null) {
            return;
        }
        if (faceEngine == null) {
            return;
        }

        bitmap = ImageUtil.alignBitmapForBgr24(bitmap);


        if (bitmap == null) {
            return;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        final Bitmap finalBitmap = bitmap;

//        Glide.with(this).load(finalBitmap).into(ivFace);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ivFace.setImageBitmap(finalBitmap);
            }
        });

        //bitmap转bgr
        byte[] bgr24 = ImageUtil.bitmapToBgr(bitmap);

        if (bgr24 == null) {
            return;
        }
        List<FaceInfo> faceInfoList = new ArrayList<>();


        /**
         * 2.成功获取到了BGR24 数据，开始人脸检测
         */
        long fdStartTime = System.currentTimeMillis();
        int detectCode = faceEngine.detectFaces(bgr24, width, height, FaceEngine.CP_PAF_BGR24,
                faceInfoList);
        if (detectCode == ErrorInfo.MOK) {
//            Log.i(TAG, "processImage: fd costTime = " + (System.currentTimeMillis() -
// fdStartTime));
        }

        //绘制bitmap
        Bitmap bitmapForDraw = bitmap.copy(Bitmap.Config.RGB_565, true);
        Canvas canvas = new Canvas(bitmapForDraw);
        Paint paint = new Paint();
        /**
         * 3.若检测结果人脸数量大于0，则在bitmap上绘制人脸框并且重新显示到ImageView，若人脸数量为0，则无法进行下一步操作，操作结束
         */
        if (faceInfoList.size() > 0) {
            paint.setAntiAlias(true);
            paint.setStrokeWidth(5);
            paint.setColor(Color.YELLOW);
            for (int i = 0; i < faceInfoList.size(); i++) {
                //绘制人脸框
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(faceInfoList.get(i).getRect(), paint);
                //绘制人脸序号
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                int textSize = faceInfoList.get(i).getRect().width() / 2;
                paint.setTextSize(textSize);

                canvas.drawText(String.valueOf(i), faceInfoList.get(i).getRect().left,
                        faceInfoList.get(i).getRect().top, paint);
            }
            //显示
            final Bitmap finalBitmapForDraw = bitmapForDraw;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ivFace.setImageBitmap(finalBitmapForDraw);
                }
            });
        } else {
            addNotificationInfo(notificationSpannableStringBuilder, new StyleSpan(Typeface
                    .BOLD), "\n\n错误，未检测到数据\n");
            flag = true;
            Intent intent = new Intent();
            intent.putExtra("status", -1);
            setResult(RESULT_OK, intent);
            btnOkSetText("确定");
            showNotificationAndFinish(notificationSpannableStringBuilder);
            return;
        }
        addNotificationInfo(notificationSpannableStringBuilder, null, "\n");


        /**
         * 4.上一步已获取到人脸位置和角度信息，传入给process函数，进行年龄、性别、三维角度检测
         */

        long processStartTime = System.currentTimeMillis();
        int faceProcessCode = faceEngine.process(bgr24, width, height, FaceEngine.CP_PAF_BGR24,
                faceInfoList, FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine
                        .ASF_FACE3DANGLE | FaceEngine.ASF_LIVENESS);

        if (faceProcessCode != ErrorInfo.MOK) {
        } else {
        }
        //年龄信息结果
        List<AgeInfo> ageInfoList = new ArrayList<>();
        //性别信息结果
        List<GenderInfo> genderInfoList = new ArrayList<>();
        //人脸三维角度结果
        List<Face3DAngle> face3DAngleList = new ArrayList<>();
        //活体检测结果
        List<LivenessInfo> livenessInfoList = new ArrayList<>();
        //获取年龄、性别、三维角度、活体结果
        int ageCode = faceEngine.getAge(ageInfoList);
        int genderCode = faceEngine.getGender(genderInfoList);
        int face3DAngleCode = faceEngine.getFace3DAngle(face3DAngleList);
        int livenessCode = faceEngine.getLiveness(livenessInfoList);

        if ((ageCode | genderCode | face3DAngleCode | livenessCode) != ErrorInfo.MOK) {
            return;
        }
        /**
         * 5.年龄、性别、三维角度已获取成功，添加信息到提示文字中
         */
        //年龄数据
        if (ageInfoList.size() > 0) {
        }
        for (int i = 0; i < ageInfoList.size(); i++) {
        }

        //性别数据
        if (genderInfoList.size() > 0) {
        }
        for (int i = 0; i < genderInfoList.size(); i++) {
        }
        //人脸三维角度数据
        if (face3DAngleList.size() > 0) {
            for (int i = 0; i < face3DAngleList.size(); i++) {
            }
        }

        //活体检测数据
        if (livenessInfoList.size() > 0) {
            for (int i = 0; i < livenessInfoList.size(); i++) {
                String liveness = null;
                switch (livenessInfoList.get(i).getLiveness()) {
                    case LivenessInfo.ALIVE:
                        liveness = "ALIVE";
                        break;
                    case LivenessInfo.NOT_ALIVE:
                        liveness = "NOT_ALIVE";
                        break;
                    case LivenessInfo.UNKNOWN:
                        liveness = "UNKNOWN";
                        break;
                    case LivenessInfo.FACE_NUM_MORE_THAN_ONE:
                        liveness = "FACE_NUM_MORE_THAN_ONE";
                        break;
                    default:
                        liveness = "UNKNOWN";
                        break;
                }
            }
        }

        /**
         * 6.最后将图片内的所有人脸进行一一比对并添加到提示文字中
         */
        if (faceInfoList.size() > 0) {

            FaceFeature[] faceFeatures = new FaceFeature[faceInfoList.size()];
            int[] extractFaceFeatureCodes = new int[faceInfoList.size()];

            for (int i = 0; i < faceInfoList.size(); i++) {
                faceFeatures[i] = new FaceFeature();
                //从图片解析出人脸特征数据
                long frStartTime = System.currentTimeMillis();
                extractFaceFeatureCodes[i] = faceEngine.extractFaceFeature(bgr24, width, height,
                        FaceEngine.CP_PAF_BGR24, faceInfoList.get(i), faceFeatures[i]);

                if (extractFaceFeatureCodes[i] != ErrorInfo.MOK) {
                } else {
                }
            }
            //人脸特征的数量大于2，将所有特征进行比较
            if (faceFeatures.length >= 2) {
                for (int i = 0; i < faceFeatures.length; i++) {
                    for (int j = i + 1; j < faceFeatures.length; j++) {
                        //若其中一个特征提取失败，则不进行比对
                        boolean canCompare = true;
                        if (extractFaceFeatureCodes[i] != 0) {
                            canCompare = false;
                        }
                        if (extractFaceFeatureCodes[j] != 0) {
                            canCompare = false;
                        }
                        if (!canCompare) {
                            continue;
                        }

                        FaceSimilar matching = new FaceSimilar();
                        //比对两个人脸特征获取相似度信息
                        faceEngine.compareFaceFeature(faceFeatures[i], faceFeatures[j], matching);
                        //新增相似度比对结果信息
                    }
                }
                addNotificationInfo(notificationSpannableStringBuilder, new StyleSpan(Typeface
                        .BOLD), "\n错误，请务必保持一个人脸\n");
                flag = true;
                Intent intent = new Intent();
                intent.putExtra("status", -1);
                setResult(RESULT_OK, intent);
                btnOkSetText("确定");
            } else {

                if (!(livenessInfoList.get(0).getLiveness() == LivenessInfo.ALIVE)) {
                    addNotificationInfo(notificationSpannableStringBuilder, new StyleSpan(Typeface
                            .BOLD), "活体检测失败，请确保真人采集并重新尝试\n");
                    flag = true;
                    Intent intent = new Intent();
                    intent.putExtra("status", -1);
                    setResult(RESULT_OK, intent);
                    btnOkSetText("确定");
                } else {
                    addNotificationInfo(notificationSpannableStringBuilder, new StyleSpan(Typeface
                            .BOLD), "成功\n");
                    flag = true;
                    Intent intent = new Intent();
                    intent.putExtra("status", 1);
                    userInfo = new UserInfo();
                    userInfo.setFaceData(android.util.Base64.encodeToString(faceFeatures[0]
                            .getFeatureData(), android.util
                            .Base64.DEFAULT));
                    intent.putExtra("user", userInfo);
                    setResult(RESULT_OK, intent);
                    btnOkSetText("确定");
                }
            }
        }

        showNotificationAndFinish(notificationSpannableStringBuilder);

    }

    private void btnOkSetText(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnOk.setText(data);
            }
        });
    }


    /**
     * 追加提示信息
     *
     * @param stringBuilder 提示的字符串的存放对象
     * @param styleSpan     添加的字符串的格式
     * @param strings       字符串数组
     */
    private void addNotificationInfo(SpannableStringBuilder stringBuilder, ParcelableSpan
            styleSpan, String... strings) {
        if (stringBuilder == null || strings == null || strings.length == 0) {
            return;
        }
        int startLength = stringBuilder.length();
        for (String string : strings) {
            stringBuilder.append(string);
        }
        int endLength = stringBuilder.length();
        if (styleSpan != null) {
            stringBuilder.setSpan(styleSpan, startLength, endLength, Spanned
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    /**
     * 展示提示信息并且关闭提示框
     *
     * @param stringBuilder 带格式的提示文字
     */
    private void showNotificationAndFinish(final SpannableStringBuilder stringBuilder) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (tvStatus != null) {
                    tvStatus.setVisibility(View.VISIBLE);
                    tvStatus.setText(stringBuilder);
                }
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
        unInitEngine();
        super.onDestroy();
    }

    /**
     * 销毁引擎
     */
    private void unInitEngine() {
        if (faceEngine != null) {
            faceEngineCode = faceEngine.unInit();
            faceEngine = null;
        }
    }

    @Override
    public void onBackPressed() {
        OnLeftButtonClick();
    }

    @Override
    public void OnLeftButtonClick() {
        Intent intent = new Intent();
        intent.putExtra("status", -1);
        setResult(RESULT_OK, intent);
        finish();
    }
}
