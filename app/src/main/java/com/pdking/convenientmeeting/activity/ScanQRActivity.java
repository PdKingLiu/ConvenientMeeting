package com.pdking.convenientmeeting.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.TitleView;

import java.io.IOException;

public class ScanQRActivity extends AppCompatActivity {

    private final int ALBUM_REQUEST = 1;
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private boolean openFlag = false;
    private ImageView mImageView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_scan_qr);
        initTitle();
        mImageView = findViewById(R.id.iv_open_light);
        mTextView = findViewById(R.id.tv_open_light);
        SystemUtil.setTitleMode(getWindow());
        barcodeScannerView = findViewById(R.id.dbv_custom);
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }

    private void initTitle() {
        TitleView mTitleView = findViewById(R.id.title);
        mTitleView.setLeftClickListener(new TitleView.LeftClickListener() {
            @Override
            public void OnLeftButtonClick() {
                finish();
            }
        });
        mTitleView.setRightClickListener(new TitleView.RightClickListener() {
            @Override
            public void OnRightButtonClick() {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, ALBUM_REQUEST);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    public void openLight(View view) {
        if (openFlag) {
            turnOffLight(view);
            openFlag = false;
        } else {
            turnOnLight(view);
            openFlag = true;
        }
    }

    public void turnOnLight(View view) {
        try {
            barcodeScannerView.setTorchOn();
            mImageView.setImageResource(R.mipmap.open_light_click);
            mTextView.setTextColor(Color.parseColor("#44912f"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void turnOffLight(View view) {
        try {
            barcodeScannerView.setTorchOff();
            mImageView.setImageResource(R.mipmap.open_light);
            mTextView.setTextColor(Color.parseColor("#ffffff"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case ALBUM_REQUEST:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    dealAlbumResult(data.getData());
                }
                break;
        }
    }

    private void dealAlbumResult(Uri data) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data);
            if (bitmap != null) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int[] pixels = new int[width * height];
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
                Reader reader = new QRCodeReader();
                try {
                    Result result = reader.decode(binaryBitmap);
                    Toast.makeText(this, result.getText(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, ScanResultActivity.class);
                    intent.putExtra("data", result.getText());
                    startActivity(intent);
                    finish();
                } catch (NotFoundException | ChecksumException | FormatException e) {
                    e.printStackTrace();
                } finally {
                    bitmap.recycle();
                    bitmap = null;
                    pixels = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
