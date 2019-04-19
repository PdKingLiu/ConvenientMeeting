package com.pdking.convenientmeeting.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.adapter.FileAdapter;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.FileData;
import com.pdking.convenientmeeting.db.FileDataListBean;
import com.pdking.convenientmeeting.db.RequestReturnBean;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.OkHttpUtils;
import com.pdking.convenientmeeting.utils.SystemUtil;
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
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileListActivity extends AppCompatActivity implements FileAdapter.OnItemClickListener {

    @BindView(R.id.title)
    TitleView titleView;
    @BindView(R.id.rl_file_list)
    RecyclerView recyclerView;
    @BindView(R.id.srl_flush)
    SmartRefreshLayout smartRefreshLayout;


    private String meetingID;
    private String userId;
    private String token;
    private ProgressDialog dialog;
    private FileAdapter adapter;
    private String TAG = "Lpp";

    private FileDataListBean fileDataListBean;
    private List<FileData> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_file_list);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        meetingID = getIntent().getStringExtra("meetingID");
        userId = getIntent().getStringExtra("userId");
        token = getIntent().getStringExtra("token");
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在加载...");
        dialog.setTitle("加载中");
        dialog.setCancelable(false);
        initListAndLoadFileList();
        titleView.setRightTextSize(20);
        titleView.setLeftClickListener(new TitleView.LeftClickListener() {
            @Override
            public void OnLeftButtonClick() {
                finish();
            }
        });
        titleView.setRightClickListener(new TitleView.RightClickListener() {
            @Override
            public void OnRightButtonClick() {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadData();
            }
        });
    }

    private void loadData() {
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.LoadMeetingFileBody[0], meetingID);
        Request request = new Request.Builder()
                .header(Api.LoadMeetingFileHeader[0], Api.LoadMeetingFileHeader[1])
                .addHeader("token", token)
                .post(body.build())
                .url(Api.LoadMeetingFileApi)
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                smartRefreshLayout.finishRefresh(2000, false);
                Log.d(TAG, "加载文件列表失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(FileListActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            userId = newInfo.getUserId() + "";
                            token = newToken.getToken();
                        }
                    });
                    return;
                }
                fileDataListBean = new Gson().fromJson(msg, FileDataListBean.class);
                if (fileDataListBean != null) {
                    if (fileDataListBean.status != 0) {
                        smartRefreshLayout.finishRefresh(2000, false);
                    } else {
                        smartRefreshLayout.finishRefresh(2000, true);
                        fileList.clear();
                        fileList.addAll(fileDataListBean.data);
                        notifyDataChanged();
                    }
                }
            }
        });
    }

    private void initListAndLoadFileList() {
        fileList = new ArrayList<>();
        adapter = new FileAdapter(this, fileList);
        adapter.setClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        showProgressBar();
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.LoadMeetingFileBody[0], meetingID);
        Request request = new Request.Builder()
                .header(Api.LoadMeetingFileHeader[0], Api.LoadMeetingFileHeader[1])
                .addHeader("token", token)
                .post(body.build())
                .url(Api.LoadMeetingFileApi)
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideProgressBar();
                showToast("加载文件列表失败");
                Log.d(TAG, "加载文件列表失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideProgressBar();
                String msg = response.body().string();
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(FileListActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            userId = newInfo.getUserId() + "";
                            token = newToken.getToken();
                        }
                    });
                    return;
                }
                fileDataListBean = new Gson().fromJson(msg, FileDataListBean.class);
                if (fileDataListBean != null) {
                    if (fileDataListBean.status != 0) {
                        showToast("加载文件列表失败");
                    } else {
                        fileList.addAll(fileDataListBean.data);
                        notifyDataChanged();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 1:
                if (data != null) {
                    upLoadFile(data.getData());
                }
                break;
        }
    }

    private void upLoadFile(Uri data) {
        File file = uriToFile(data, this);
        if (file == null) {
            showToast("文件读取出现了问题");
            return;
        }
        if (!file.exists() || !file.canRead()) {
            showToast("文件读取出现了问题");
            return;
        }
        String mediaType = getType(file);
        Log.d(TAG, "mediaType: " + mediaType);
        if (mediaType == null) {
            showToast("暂不支持此类文件");
            return;
        }
        dialog.setTitle("上传中");
        dialog.setMessage("正在上传...");
        showProgressBar();
        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(Api.UpLoadFileBody[1], meetingID)
                .addFormDataPart(Api.UpLoadFileBody[2], userId)
                .addFormDataPart(Api.UpLoadFileBody[0], file.getName(), RequestBody.create
                        (MediaType.parse(mediaType), file))
                .build();
        Request request = new Request.Builder()
                .url(Api.UpLoadFileApi)
                .post(body)
                .header(Api.UpLoadFileHeader[0], Api.UpLoadFileHeader[1])
                .addHeader("token", token)
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideProgressBar();
                showToast("上传失败");
                e.printStackTrace();
                Log.d("Lpp", "上传失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideProgressBar();
                String msg = response.body().string();
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(FileListActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            userId = newInfo.getUserId() + "";
                            token = newToken.getToken();
                        }
                    });
                    return;
                }
                RequestReturnBean bean = new Gson().fromJson(msg, RequestReturnBean.class);
                if (bean.status != 0) {
                    showToast("上传失败");
                } else {
                    showToast("上传成功");
                    refresh();
                }
            }
        });
    }

    public void refresh() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                smartRefreshLayout.autoRefresh();
            }
        });
    }

    private String getType(File file) {
        String name = file.getName();
        int end = name.lastIndexOf(".");
        if (end == -1) {
            return null;
        }
        String str = name.substring(end + 1).toLowerCase();
        String type = null;
        switch (str) {
            case "pdf":
                type = "application/pdf";
                break;
            case "xla":
            case "xlc":
            case "xlm":
            case "xls":
            case "xlt":
            case "xlw":
                type = "application/vnd.ms-excel";
                break;
            case "txt":
                type = "text/plain";
                break;
            case "mp3":
                type = "audio/mpeg";
                break;
            case "mp4":
                type = "application/octet-stream";
                break;
            case "jpe":
            case "jpeg":
            case "jpg":
                type = "image/jpeg";
                break;
            case "gif":
                type = "image/gif";
                break;
            case "png":
                type = "image/png";
                break;
            case "ppt":
            case "pptx":
                type = "application/vnd.ms-powerpoint";
                break;
            case "doc":
            case "docx":
                type = "application/msword";
                break;
            default:
                type = null;
        }
        return type;
    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FileListActivity.this, text, Toast.LENGTH_SHORT).show();
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

    public static File uriToFile(Uri uri, Context context) {
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append
                        ("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new
                        String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images
                        .ImageColumns.DATA}, buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0) {
                } else {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();
            if (path != null) {
                return new File(path);
            } else return null;
        } else {
            //Log.i(TAG, "Uri Scheme:" + uri.getScheme());
        }
        return null;
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
            showProgressBar();
            OkHttpUtils.requestHelper(request, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    hideProgressBar();
                    showToast("文件下载失败");
                    Log.d(TAG, "文件下载失败: " + e.getMessage());
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
                        Log.d(TAG, "contentLength: " + response.body().contentLength());
                        inputStream = response.body().byteStream();
                        fileOutputStream = new FileOutputStream(file);
                        while ((len = inputStream.read(bytes)) != -1) {
                            current += len;
                            fileOutputStream.write(bytes, 0, len);
                            Log.d(TAG, "fileLen: " + current);
                        }
                        fileOutputStream.flush();
                        inputStream.close();
                        fileOutputStream.close();
                        hideProgressBar();
                        showToast("文件下载成功");
                        Intent intent = new Intent(FileListActivity.this, ScanFileActivity.class);
                        intent.putExtra("fileName", data.fileName);
                        startActivity(intent);
                    } catch (Exception e) {
                        showToast("文件下载失败");
                        Log.d(TAG, "Exception: " + e.getMessage());
                    } finally {
                        hideProgressBar();
                    }
                }
            });

        }
    }
}
