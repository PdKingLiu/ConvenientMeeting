package com.pdking.convenientmeeting.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.Api;
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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class LiveMeetingDetailActivity extends AppCompatActivity implements TitleView.LeftClickListener {

    private String liveId;

    @BindView(R.id.title)
    TitleView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_live_meeting_detail);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        liveId = getIntent().getStringExtra("liveId");
        title.setLeftClickListener(this);
        loadData();
    }

    private void loadData() {
        Request request = new Request.Builder()
                .header(Api.GetLiveMessageHeader[0], Api.GetLiveMessageHeader[1])
                .addHeader("token", UserAccountUtils.getUserToken(getApplication()).getToken())
                .get()
                .url(Api.GetLiveMessageApi + "?" + "liveId=" + liveId)
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtils.showToast(LiveMeetingDetailActivity.this, "网络错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Log.d("Lpp", "onResponse: " + msg);
                if (msg.contains("token")) {
                    LoginStatusUtils.stateFailure(LiveMeetingDetailActivity.this, new
                            LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            UserAccountUtils.setUserToken(newToken, getApplication());
                        }
                    });
                    return;
                }
            }
        });
    }

    @Override
    public void OnLeftButtonClick() {
        finish();
    }
}
