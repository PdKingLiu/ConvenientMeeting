package com.pdking.convenientmeeting.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.TitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class BookRoomActivity extends AppCompatActivity {


    @BindView(R.id.btn_scan)
    Button btnScan;
    @BindView(R.id.btn_input)
    Button btnInput;
    @BindView(R.id.title)
    TitleView title;
    @BindView(R.id.ed_input)
    TextInputEditText edInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_book_room);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        title.setLeftClickListener(new TitleView.LeftClickListener() {
            @Override
            public void OnLeftButtonClick() {
                finish();
            }
        });
    }

    @OnClick({R.id.btn_scan, R.id.btn_input})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_scan:
                new IntentIntegrator(this)
                        .setCaptureActivity(ScanQRActivity.class)
                        .setPrompt("")
                        .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)// 扫码的类型,
                        .setCameraId(0)// 选择摄像头,可使用前置或者后置
                        .setBeepEnabled(true)// 是否开启声音,扫完码之后会"哔"的一声
                        .initiateScan();// 初始化扫码
                break;
            case R.id.btn_input:
                final EditText editText = new EditText(this);
                editText.setGravity(Gravity.CENTER);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("输入设备号")
                        .setCancelable(true)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                dealText(editText.getText().toString());
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setView(editText)
                        .show();
                break;
        }
    }

    private void dealText(String s) {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                Toast.makeText(this, "" + result.getContents(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, ScanResultActivity.class);
                intent.putExtra("data", result.getContents());
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnTextChanged(R.id.ed_input)
    void onTextChanged(CharSequence s) {
        if (s.length() == 10) {
            Toast.makeText(this, "" + s, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, ScanResultActivity.class);
            intent.putExtra("data", s);
            startActivity(intent);
        }
    }
}
