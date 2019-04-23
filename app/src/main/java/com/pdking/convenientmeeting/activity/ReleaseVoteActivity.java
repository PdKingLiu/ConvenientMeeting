package com.pdking.convenientmeeting.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.OkHttpUtils;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.utils.UIUtils;
import com.pdking.convenientmeeting.weight.TitleView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class ReleaseVoteActivity extends AppCompatActivity implements TitleView
        .LeftClickListener, TitleView.RightClickListener, View.OnClickListener {

    @BindView(R.id.title)
    TitleView title;
    @BindView(R.id.ll_add)
    LinearLayout llAdd;
    @BindView(R.id.ll_3)
    LinearLayout ll3;
    @BindView(R.id.ll_4)
    LinearLayout ll4;
    @BindView(R.id.ll_5)
    LinearLayout ll5;
    @BindView(R.id.iv_3)
    ImageView iv3;
    @BindView(R.id.iv_4)
    ImageView iv4;
    @BindView(R.id.iv_5)
    ImageView iv5;
    @BindView(R.id.ll_kind)
    LinearLayout llKind;
    @BindView(R.id.ll_end_time)
    LinearLayout llEndTime;
    @BindView(R.id.ll_remind)
    LinearLayout llRemind;
    @BindView(R.id.tv_kind)
    TextView tvKind;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.tv_remind)
    TextView tvRemind;
    @BindView(R.id.ed_vote_theme)
    EditText edVoteTheme;
    @BindView(R.id.ed_choose_1)
    EditText edChoose1;
    @BindView(R.id.ed_choose_2)
    EditText edChoose2;
    @BindView(R.id.ed_choose_3)
    EditText edChoose3;
    @BindView(R.id.ed_choose_4)
    EditText edChoose4;
    @BindView(R.id.ed_choose_5)
    EditText edChoose5;

    private boolean[] isVisibleFlags = {true, true, false, false, false};
    private int itemSum = 2;
    private int kind = 1;
    private Date endTime;
    private int remind = 1;
    private Calendar calendar = Calendar.getInstance();


    private String theme = "";
    private String[] options = {"", "", "", "", ""};
    private Date createTime = new Date();

    private String meetingId;
    private String userId;
    private String token;
    private boolean isSucceed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_release_vote);
        ButterKnife.bind(this);
        SystemUtil.setTitleMode(getWindow());
        meetingId = getIntent().getStringExtra("meetingId");
        userId = getIntent().getStringExtra("userId");
        token = getIntent().getStringExtra("token");
        setListener();
        setDate();
    }

    private void setDate() {
        endTime = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
        calendar.setTime(endTime);
        @SuppressLint("DefaultLocale") String time = String.format("%d年%d月%d日 %d:%02d",
                calendar.get(Calendar.YEAR),
                (calendar.get(Calendar.MONTH) + 1), calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        tvEndTime.setText(time);
    }

    private void setListener() {
        title.setRightClickListener(this);
        title.setLeftClickListener(this);
        llAdd.setOnClickListener(this);
        iv3.setOnClickListener(this);
        iv4.setOnClickListener(this);
        iv5.setOnClickListener(this);
        llKind.setOnClickListener(this);
        llEndTime.setOnClickListener(this);
        llRemind.setOnClickListener(this);
    }

    @Override
    public void OnLeftButtonClick() {
        finish();
    }

    @Override
    public void OnRightButtonClick() {
        releaseVote();
    }

    private void releaseVote() {
        if (isSucceed) {
            UIUtils.showToast(this, "已经发布过啦");
            return;
        }
        theme = edVoteTheme.getText().toString();
        if (theme.equals("")) {
            UIUtils.showToast(this, "主题有误");
            return;
        }
        options[0] = edChoose1.getText().toString();
        options[1] = edChoose2.getText().toString();
        options[2] = edChoose3.getText().toString();
        options[3] = edChoose4.getText().toString();
        options[4] = edChoose5.getText().toString();
        if (options[0].equals("") || options[1].equals("")) {
            UIUtils.showToast(this, "选项有误");
            return;
        }
        switch (itemSum - 2) {
            case 1:
                if (options[2].equals("")) {
                    UIUtils.showToast(this, "选项有误");
                    return;
                }
                break;
            case 2:
                if (options[2].equals("") || options[3].equals("")) {
                    UIUtils.showToast(this, "选项有误");
                    return;
                }
                break;
            case 3:
                if (options[2].equals("") || options[3].equals("") || options[4].equals("")) {
                    UIUtils.showToast(this, "选项有误");
                    return;
                }
                break;
        }
        createTime.setTime(System.currentTimeMillis());
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.SetVoteBody[0], meetingId + "");
        body.add(Api.SetVoteBody[1], userId + "");
        body.add(Api.SetVoteBody[2], theme);
        body.add(Api.SetVoteBody[3], kind + "");
        body.add(Api.SetVoteBody[4], remind + "");
        body.add(Api.SetVoteBody[5], new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format
                (createTime));
        body.add(Api.SetVoteBody[6], new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(endTime));
        body.add(Api.SetVoteBody[7], options[0]);
        body.add(Api.SetVoteBody[7], options[1]);
        switch (itemSum - 2) {
            case 1:
                body.add(Api.SetVoteBody[7], options[2]);
                break;
            case 2:
                body.add(Api.SetVoteBody[7], options[2]);
                body.add(Api.SetVoteBody[7], options[3]);
                break;
            case 3:
                body.add(Api.SetVoteBody[7], options[2]);
                body.add(Api.SetVoteBody[7], options[3]);
                body.add(Api.SetVoteBody[7], options[4]);
                break;
        }
        Request request = new Request.Builder()
                .header(Api.SetVoteHeader[0], Api.SetVoteHeader[1])
                .addHeader("token", token)
                .post(body.build())
                .url(Api.SetVoteApi)
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtils.showToast(ReleaseVoteActivity.this, "发布失败");
                Log.d("Lpp", "发布失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Log.d("Lpp", "onResponse: " + msg);
                if (msg.contains("token过期")) {
                    LoginStatusUtils.stateFailure(ReleaseVoteActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            token = newToken.getToken();
                        }
                    });
                    return;
                }
                if (msg.contains("创建投票成功")) {
                    UIUtils.showToast(ReleaseVoteActivity.this, "发布成功");
                    Intent intent = new Intent();
                    intent.putExtra("result", 1);
                    setResult(RESULT_OK,intent);
                    isSucceed = true;
                } else {
                    UIUtils.showToast(ReleaseVoteActivity.this, "发布失败");
                    isSucceed = false;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_add:
                dealAdd();
                break;
            case R.id.iv_3:
                itemSum--;
                ll3.setVisibility(View.GONE);
                isVisibleFlags[2] = false;
                break;
            case R.id.iv_4:
                itemSum--;
                ll4.setVisibility(View.GONE);
                iv3.setVisibility(View.VISIBLE);
                isVisibleFlags[3] = false;
                break;
            case R.id.iv_5:
                itemSum--;
                ll5.setVisibility(View.GONE);
                iv4.setVisibility(View.VISIBLE);
                isVisibleFlags[4] = false;
                break;
            case R.id.ll_kind:
                getKind();
                break;
            case R.id.ll_end_time:
                getEndTime();
                break;
            case R.id.ll_remind:
                getRemind();
                break;
        }
    }

    private void getRemind() {
        final List<String> list = new ArrayList<>();
        list.add("提前30分钟");
        list.add("提前12小时");
        list.add("提前24小时");
        list.add("不提醒");
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (list.get(options1).equals("提前30分钟")) {
                    tvRemind.setText("提前30分钟");
                    remind = 1;
                } else if (list.get(options1).equals("提前12小时")) {
                    tvRemind.setText("提前12小时");
                    remind = 2;
                } else if (list.get(options1).equals("提前24小时")) {
                    tvRemind.setText("提前24小时");
                    remind = 3;
                } else if (list.get(options1).equals("不提醒")) {
                    tvRemind.setText("不提醒");
                    remind = 4;
                }
            }
        }).build();
        pvOptions.setPicker(list, null, null);
        pvOptions.show();
    }

    private void getEndTime() {
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                if ((date.getTime() - System.currentTimeMillis()) < 0) {
                    Toast.makeText(ReleaseVoteActivity.this, "时间有误", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (((date.getTime() - System.currentTimeMillis()) / 1000 / 60 / 60 / 24) > 30) {
                    Toast.makeText(ReleaseVoteActivity.this, "最长期限为30天内", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                endTime = date;
                calendar.setTime(endTime);
                @SuppressLint("DefaultLocale") String time = String.format("%d年%d月%d日 %d:%02d",
                        calendar.get(Calendar.YEAR),
                        (calendar.get(Calendar.MONTH) + 1), calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                tvEndTime.setText(time);
            }
        }).setType(new boolean[]{false, true, true, true, true, false})
                .build();
        pvTime.setDate(Calendar.getInstance());
        pvTime.show();
    }

    private void dealAdd() {
        int i;
        for (i = 0; i < isVisibleFlags.length; i++) {
            if (!isVisibleFlags[i]) {
                itemSum++;
                changeUI(i + 1);
                isVisibleFlags[i] = true;
                break;
            }
        }
        if (i == isVisibleFlags.length) {
            Toast.makeText(this, "目前最多支持五个选项", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeUI(int i) {
        switch (i) {
            case 3:
                ll3.setVisibility(View.VISIBLE);
                iv3.setVisibility(View.VISIBLE);
                break;
            case 4:
                ll4.setVisibility(View.VISIBLE);
                iv4.setVisibility(View.VISIBLE);
                iv3.setVisibility(View.INVISIBLE);
                break;
            case 5:
                ll5.setVisibility(View.VISIBLE);
                iv5.setVisibility(View.VISIBLE);
                iv4.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void getKind() {
        final List<String> list = new ArrayList<>();
        list.add("单选");
        list.add("多选");
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (list.get(options1).equals("单选")) {
                    tvKind.setText("单选");
                    kind = 1;
                } else {
                    tvKind.setText("多选");
                    kind = 2;
                }
            }
        }).build();
        pvOptions.setPicker(list, null, null);
        pvOptions.show();
    }
}
