package com.pdking.convenientmeeting.activity;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.TitleView;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanFileActivity extends AppCompatActivity {

    @BindView(R.id.ll_sum)
    LinearLayout llSum;
    @BindView(R.id.title)
    TitleView titleView;

    private TbsReaderView trv;
    private File file;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_scan_file);
        ButterKnife.bind(this);
        SystemUtil.setTitleMode(getWindow());
        fileName = getIntent().getStringExtra("fileName");
        file = new File(new File(getExternalFilesDir(null) + "/user/meetingFile"), fileName);
        titleView.setLeftClickListener(new TitleView.LeftClickListener() {
            @Override
            public void OnLeftButtonClick() {
                finish();
            }
        });
        titleView.setTitleText(file.getName());
        scanFile(file);
    }

    private void scanFile(File file) {
        trv = new TbsReaderView(this, new TbsReaderView.ReaderCallback() {
            @Override
            public void onCallBackAction(Integer integer, Object o, Object o1) {

            }
        });
        String fileType = getFileType(file);
        if (fileType == null) {
            showToast("暂不支持预览此文件");
            return;
        }
        llSum.addView(trv, new LinearLayout.LayoutParams(-1, -1));
        Log.d("Lpp", "file.exists(): " + file.exists());
        Bundle localBundle = new Bundle();
        localBundle.putString("filePath", file.toString());
        localBundle.putString("tempPath", Environment.getExternalStorageDirectory() + "/" +
                "TbsReaderTemp");
        boolean bool = trv.preOpen(fileType, false);
        if (bool) {
            trv.openFile(localBundle);
        }
    }


    @Override
    protected void onDestroy() {
        if (trv != null) {
            trv.onStop();
        }
        super.onDestroy();
    }

    public String getFileType(File file) {
        String name = file.getName();
        int end = name.lastIndexOf(".");
        if (end == -1) {
            return null;
        }
        String str = name.substring(end + 1).toLowerCase();
        String type = null;
        switch (str) {
            case "pdf":
                type = "pdf";
                break;
            case "xla":
            case "xlc":
            case "xlm":
            case "xls":
            case "xlt":
            case "xlw":
                type = "excel";
                break;
            case "txt":
                type = "txt";
                break;
            case "mp3":
                type = "mp3";
                break;
            case "mp4":
                type = "mp4";
                break;
            case "jpe":
            case "jpeg":
            case "jpg":
            case "gif":
            case "png":
                type = "jpg";
                break;
            case "ppt":
            case "pptx":
                type = "ppt";
                break;
            case "doc":
            case "docx":
                type = "doc";
                break;
            default:
                type = null;
        }
        type = name.substring(name.lastIndexOf(".") + 1);
        return type;
    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ScanFileActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
