package com.pdking.convenientmeeting.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.MeetingNoteBean;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.OkHttpUtils;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.utils.UIUtils;
import com.pdking.convenientmeeting.utils.UserAccountUtils;
import com.pdking.convenientmeeting.weight.TitleView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MeetingNoteActivity extends AppCompatActivity implements TitleView
        .RightClickListener, TitleView.LeftClickListener {

    @BindView(R.id.tv_string_len)
    TextView tvStringLen;
    @BindView(R.id.title)
    TitleView title;
    @BindView(R.id.ed_note)
    EditText edNote;

    private String userId;
    private String meetingId;
    private String token;
    private String note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_meeting_note);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        userId = getIntent().getStringExtra("userId");
        meetingId = getIntent().getStringExtra("meetingId");
        token = getIntent().getStringExtra("token");
        title.setRightClickListener(this);
        title.setLeftClickListener(this);
        loadNote();
    }

    @OnTextChanged(R.id.ed_note)
    void onTextChange(CharSequence charSequence) {
        tvStringLen.setText(charSequence.length() + "/1000");
        if (charSequence.toString().equals(note)) {
            title.setRightTextVisible(false);
        } else {
            title.setRightTextVisible(true);
        }
    }

    private void saveNote() {
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.SetMeetingNoteBody[0], meetingId);
        body.add(Api.SetMeetingNoteBody[1], userId);
        body.add(Api.SetMeetingNoteBody[2], edNote.getText().toString());
        final Request request = new Request.Builder()
                .header(Api.SetMeetingNoteHeader[0], Api.SetMeetingNoteHeader[1])
                .addHeader("token", token)
                .post(body.build())
                .url(Api.SetMeetingNoteApi)
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtils.showToast(MeetingNoteActivity.this, "保存失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(MeetingNoteActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            token = newToken.getToken();
                            UserAccountUtils.getUserToken(getApplication()).setToken(token);
                        }
                    });
                    return;
                }
                UIUtils.showToast(MeetingNoteActivity.this, "保存成功");
                note = edNote.getText().toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setRightTextVisible(false);
                    }
                });
            }
        });
    }

    private void loadNote() {
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.GetMeetingNoteBody[0], meetingId);
        body.add(Api.GetMeetingNoteBody[1], userId);
        Request request = new Request.Builder()
                .addHeader(Api.GetMeetingNoteHeader[0], Api.GetMeetingNoteHeader[1])
                .header("token", token)
                .post(body.build())
                .url(Api.GetMeetingNoteApi)
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtils.showToast(MeetingNoteActivity.this, "获取笔记失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(MeetingNoteActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            token = newToken.getToken();
                            UserAccountUtils.getUserToken(getApplication()).setToken(token);
                        }
                    });
                    return;
                }
                MeetingNoteBean bean = new Gson().fromJson(msg, MeetingNoteBean.class);
                if (bean.status != 0) {
                } else {
                    note = bean.data;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            edNote.setText(note);
                            tvStringLen.setText(note.length() + "/1000");
                        }
                    });
                }
            }
        });
    }

    @Override
    public void OnRightButtonClick() {
        saveNote();
    }

    @Override
    public void OnLeftButtonClick() {
        finish();
    }
}
