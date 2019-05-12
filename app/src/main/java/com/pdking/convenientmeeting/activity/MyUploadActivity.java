package com.pdking.convenientmeeting.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.adapter.FileAdapter;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.FileData;
import com.pdking.convenientmeeting.db.FileDataListBean;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.OkHttpUtils;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.utils.UIUtils;
import com.pdking.convenientmeeting.utils.UserAccountUtils;
import com.pdking.convenientmeeting.weight.TitleView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MyUploadActivity extends AppCompatActivity implements FileAdapter
        .OnItemClickListener, OnRefreshListener {

    private FileAdapter adapter;
    private FileDataListBean fileDataListBean;
    private List<FileData> fileList;
    private ProgressDialog dialog;

    @BindView(R.id.title)
    TitleView titleView;
    @BindView(R.id.rl_file_list)
    RecyclerView recyclerView;
    @BindView(R.id.srl_flush)
    SmartRefreshLayout smartRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_upload);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        dialog = new ProgressDialog(this);
        titleView.setLeftClickListener(new TitleView.LeftClickListener() {
            @Override
            public void OnLeftButtonClick() {
                finish();
            }
        });
        initList();
    }

    private void initList() {
        fileList = new ArrayList<>();
        adapter = new FileAdapter(this, fileList);
        adapter.setClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        smartRefreshLayout.setOnRefreshListener(this);
        smartRefreshLayout.autoRefresh();
    }

    @Override
    public void onItemClick(View view, int position) {
        final FileData data = fileList.get(position);
        dialog.setTitle("下载中");
        dialog.setMessage("正在下载");
        Request request = new Request.Builder()
                .url(data.fileUrl)
                .build();
        File fileDir = new File(getExternalFilesDir(null) + "/user/meetingFile");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        final File file = new File(fileDir, data.fileName);
        if (file.exists()) {
            Intent intent = new Intent(this, ScanFileActivity.class);
            intent.putExtra("fileName", data.fileName);
            startActivity(intent);
        } else {
            UIUtils.showProgressBar(this, dialog);
            OkHttpUtils.requestHelper(request, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    UIUtils.hideProgressBar(MyUploadActivity.this, dialog);
                    UIUtils.showToast(MyUploadActivity.this, "文件下载失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    InputStream inputStream = null;
                    byte[] bytes = new byte[1024];
                    FileOutputStream fileOutputStream = null;
                    long current = 0;
                    int len;
                    try {
                        file.createNewFile();
                        inputStream = response.body().byteStream();
                        fileOutputStream = new FileOutputStream(file);
                        while ((len = inputStream.read(bytes)) != -1) {
                            current += len;
                            fileOutputStream.write(bytes, 0, len);
                        }
                        fileOutputStream.flush();
                        inputStream.close();
                        fileOutputStream.close();
                        UIUtils.hideProgressBar(MyUploadActivity.this, dialog);
                        UIUtils.showToast(MyUploadActivity.this, "文件下载成功");
                        Intent intent = new Intent(MyUploadActivity.this, ScanFileActivity.class);
                        intent.putExtra("fileName", data.fileName);
                        startActivity(intent);
                    } catch (Exception e) {
                        UIUtils.showToast(MyUploadActivity.this, "文件下载失败");
                    } finally {
                        UIUtils.hideProgressBar(MyUploadActivity.this, dialog);
                    }
                }
            });
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        Request request = new Request.Builder()
                .header(Api.GetUserFileListHeader[0], Api.GetUserFileListHeader[1])
                .addHeader("token", UserAccountUtils.getUserToken(getApplication()).getToken())
                .get()
                .url(Api.GetUserFileListApi + "?userId=" + UserAccountUtils.getUserInfo
                        (getApplication()).getUserId())
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                smartRefreshLayout.finishRefresh(false);
                Log.d("Lpp", "加载文件列表失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(MyUploadActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            UserAccountUtils.getUserToken(getApplication()).setToken(newToken
                                    .getToken());
                        }
                    });
                    smartRefreshLayout.finishRefresh(false);
                    return;
                }
                fileDataListBean = new Gson().fromJson(msg, FileDataListBean.class);
                if (fileDataListBean != null) {
                    if (fileDataListBean.status != 0) {
                        smartRefreshLayout.finishRefresh(false);
                    } else {
                        fileList.clear();
                        fileList.addAll(fileDataListBean.data);
                        notifyDataChanged();
                        smartRefreshLayout.finishRefresh(true);
                    }
                }
            }
        });
    }

    private void notifyDataChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
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

}
