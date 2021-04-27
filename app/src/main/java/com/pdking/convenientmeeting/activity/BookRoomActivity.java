package com.pdking.convenientmeeting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.OneMeetingRoomMessageBean;
import com.pdking.convenientmeeting.db.SMSSendStatusBean;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.DesUtil;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.OkHttpUtils;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.utils.UIUtils;
import com.pdking.convenientmeeting.utils.UserAccountUtils;
import com.pdking.convenientmeeting.weight.BookRoomByRoomNameDialog;
import com.pdking.convenientmeeting.weight.TitleView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
                BookRoomByRoomNameDialog dialog = new BookRoomByRoomNameDialog(this, R.style
                        .DialogTheme, new BookRoomByRoomNameDialog.OnClickListener() {
                    @Override
                    public void onClick(String room) {
                        dealRoom(room);
                    }
                });
                dialog.show();
                break;
        }
    }

    private void dealRoom(String room) {
        Request request = new Request.Builder()
                .url(Api.GetIdByRoomApi + "?roomNumber=" + room)
                .get()
                .header("token", UserAccountUtils.getUserToken(getApplication()).getToken())
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtils.showToast(BookRoomActivity.this, "网络错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                if (msg.contains("token")) {
                    LoginStatusUtils.stateFailure(BookRoomActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            UserAccountUtils.setUserToken(newToken, getApplication());
                        }
                    });
                    return;
                }
                SMSSendStatusBean bean = new Gson().fromJson(msg, SMSSendStatusBean.class);
                if (bean == null || bean.data == null) {
                    UIUtils.showToast(BookRoomActivity.this, "不存在此会议室");
                } else {
                    if (bean.status == 0) {
                        queryRoomById(bean.data);
                    }
                }
            }
        });
    }

    private void queryRoomById(String data) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.GetOneMeetingRoomMessageBody[0], data);
        Request request = new Request.Builder()
                .header("token", String.valueOf(UserAccountUtils.getUserToken(getApplication())
                        .getToken()))
                .url(Api.GetOneMeetingRoomMessageApi)
                .post(body.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtils.showToast(BookRoomActivity.this, "网络错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                OneMeetingRoomMessageBean bean = new Gson().fromJson(msg,
                        OneMeetingRoomMessageBean.class);
                Intent intent = new Intent(BookRoomActivity.this, MeetingRoomDetailsActivity.class);
                intent.putExtra("roomNumber", bean.data.roomNumber);
                intent.putExtra("content", bean.data.content);
                intent.putExtra("status", bean.data.status);
                intent.putExtra("meetingRoomId", bean.data.meetingRoomId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String roomId;
                try {
                    roomId = DesUtil.talker.decrypt(result.getContents());
                    queryRoomById(roomId);
                } catch (Exception e) {
                    UIUtils.showToast(BookRoomActivity.this, "二维码异常，请扫描正确的会议室二维码");
                }
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
