package com.pdking.convenientmeeting.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.RequestReturnBean;
import com.pdking.convenientmeeting.db.RoomOfMeetingMessageBean;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.utils.UserAccountUtils;
import com.pdking.convenientmeeting.weight.TitleView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BookRoomDetailActivity extends AppCompatActivity implements TitleView
        .RightClickListener, TitleView.LeftClickListener {

    @BindView(R.id.title)
    TitleView title;
    @BindView(R.id.ed_meeting_name)
    EditText edMeetingName;
    @BindView(R.id.tv_room_number)
    TextView tvRoomNumber;
    @BindView(R.id.tv_master_name)
    TextView tvMasterName;
    @BindView(R.id.ed_meeting_introduce)
    EditText edMeetingIntroduce;
    @BindView(R.id.ll_meeting_start_time)
    LinearLayout llMeetingStartTime;
    @BindView(R.id.ll_meeting_end_time)
    LinearLayout llMeetingEndTime;
    @BindView(R.id.tv_meeting_start_time)
    TextView tvMeetingStartTime;
    @BindView(R.id.tv_meeting_end_time)
    TextView tvMeetingEndTime;
    @BindView(R.id.tv_member_master)
    TextView tvMemberMaster;

    private String roomNumber;
    private int meetingRoomId;

    private ProgressDialog dialog;
    private AlertDialog dialogHint;

    private Date startDate;
    private Date endDate;
    private Calendar calendar;

    private boolean saveFlag = false;
    private boolean startTimeFlag = false;
    private boolean endTimeFlag = false;
    private int numberSum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_book_room_detail);
        SystemUtil.setTitleMode(getWindow());
        ButterKnife.bind(this);
        init();
        initPage();
    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("预定中");
        dialog.setMessage("正在预订...");
        dialog.setCancelable(false);
        startDate = new Date();
        endDate = new Date();
        calendar = Calendar.getInstance();
    }

    @OnClick({R.id.ll_meeting_start_time, R.id.ll_meeting_end_time})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_meeting_start_time:
                dialogHint = new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("可选择范围为近7天内，分钟数必须是10的整数倍，且会议时长为30分钟到5小时")
                        .setTitle("注意")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TimePickerView pvTime = new TimePickerBuilder(BookRoomDetailActivity
                                        .this, new OnTimeSelectListener() {
                                    @Override
                                    public void onTimeSelect(Date date, View v) {//选中事件回调
                                        Log.d("Lpp", "onTimeSelect: " + date);
                                        checkDate(1, date, tvMeetingStartTime);
                                    }
                                }).setType(new boolean[]{false, true, true, true, true, false})
                                        .setTitleText("注：")
                                        .build();
                                pvTime.setDate(Calendar.getInstance());
                                pvTime.show();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                dialogHint.show();
                break;
            case R.id.ll_meeting_end_time:
                dialogHint = new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("可选择范围为近2天内，分钟数必须是10的整数倍，且会议时长为30分钟到5小时")
                        .setTitle("注意")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TimePickerView pvTime = new TimePickerBuilder(BookRoomDetailActivity
                                        .this, new OnTimeSelectListener() {
                                    @Override
                                    public void onTimeSelect(Date date, View v) {//选中事件回调
                                        Log.d("Lpp", "onTimeSelect: " + date);
                                        checkDate(2, date, tvMeetingEndTime);
                                    }
                                }).setType(new boolean[]{false, true, true, true, true, false})
                                        .setTitleText("注：")
                                        .build();
                                pvTime.setDate(Calendar.getInstance());
                                pvTime.show();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                dialogHint.show();
                break;
        }
    }

    public void checkDate(int flag, Date date, TextView msg) {
        Calendar calendar1 = Calendar.getInstance();
        Log.d("Lpp", "checkDate: " + date);
        calendar.setTime(date);
        if (flag == 1) {
            int timeC = calendar.get(Calendar.DAY_OF_YEAR) - calendar1.get(Calendar.DAY_OF_YEAR);
            Log.d("Lpp", "checkDate: " + timeC);
            if (timeC > 2 || timeC < 0) {
                Toast.makeText(this, "设置失败，时间段有误，需在近2天之内", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("Lpp", "checkDate: " + calendar.get(Calendar.MINUTE));

            if (calendar.get(Calendar.MINUTE) % 10 != 0) {
                Toast.makeText(this, "设置失败，分钟数有误", Toast.LENGTH_SHORT).show();
                return;
            }
            startDate.setTime(date.getTime());
            startTimeFlag = true;
            @SuppressLint("DefaultLocale") String time = String.format("%d年%d月%d日 %d:%02d",
                    calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) + 1),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            changeTextView(msg, time);
        } else {
            if (!startTimeFlag) {
                Toast.makeText(this, "请先设置开始时间", Toast.LENGTH_SHORT).show();
                return;
            }
            if (date.getTime() - startDate.getTime() < 1800000 || date.getTime() - startDate
                    .getTime() > 18000000) {
                Toast.makeText(this, "会议时长有误", Toast.LENGTH_SHORT).show();
                return;
            }
            if (calendar.get(Calendar.MINUTE) % 10 != 0) {
                Toast.makeText(this, "设置失败，分钟数有误", Toast.LENGTH_SHORT).show();
                return;
            }
            endDate.setTime(date.getTime());
            endTimeFlag = true;
            @SuppressLint("DefaultLocale") String time = String.format("%d年%d月%d日 %d:%02d",
                    calendar.get(Calendar.YEAR),
                    (calendar.get(Calendar.MONTH) + 1), calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            changeTextView(msg, time);
        }
    }

    private void changeTextView(final TextView msg, final String time) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msg.setText(time);
            }
        });
    }

    private void initPage() {
        roomNumber = getIntent().getStringExtra("roomNumber");
        meetingRoomId = getIntent().getIntExtra("meetingRoomId", -1);
        tvRoomNumber.setText(roomNumber);
        tvMasterName.setText(UserAccountUtils.getUserInfo(getApplication()).getUsername());
        tvMemberMaster.setText(UserAccountUtils.getUserInfo(getApplication()).getUsername());
        title.setLeftClickListener(this);
        title.setRightClickListener(this);
    }

    @Override
    public void OnLeftButtonClick() {
        finish();
    }

    @Override
    public void OnRightButtonClick() {
        String meetingName;
        String meetingIntroduce;
        if (saveFlag) {
            Toast.makeText(this, "已经预定过啦~", Toast.LENGTH_SHORT).show();
        } else {
            meetingName = edMeetingName.getText().toString();
            meetingIntroduce = edMeetingIntroduce.getText().toString();
            if (meetingName.equals("") || meetingIntroduce.equals("")) {
                Toast.makeText(this, "会议名称或说明有误", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!(startTimeFlag && endTimeFlag)) {
                Toast.makeText(this, "会议时段有误，请重新设置", Toast.LENGTH_SHORT).show();
                return;
            }
            if (numberSum < 1) {
                Toast.makeText(this, "参会成员必须大于一", Toast.LENGTH_SHORT).show();
                return;
            }
            startBook(meetingName, meetingIntroduce);
        }
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

    private void startBook(final String meetingName, final String meetingIntroduce) {
        showProgressBar();
        String meetingStartTime = getDateString(startDate);
        String meetingEndTime = getDateString(endDate);
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.WhetherBookBody[0], meetingRoomId + "");
        body.add(Api.WhetherBookBody[1], meetingStartTime);
        body.add(Api.WhetherBookBody[2], meetingEndTime);
        final Request request = new Request.Builder()
                .post(body.build())
                .url(Api.WhetherBookApi)
                .header(Api.WhetherBookHeader[0], Api.WhetherBookHeader[1])
                .addHeader("token", UserAccountUtils.getUserToken(getApplication()).getToken())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideProgressBar();
                showToast("网络有问题了呢");
                Log.d("Lpp", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(BookRoomDetailActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            UserAccountUtils.setUserInfo(newInfo, getApplication());
                            UserAccountUtils.setUserToken(newToken, getApplication());
                        }
                    });
                    return;
                }
                RequestReturnBean bean = new Gson().fromJson(msg, RequestReturnBean.class);
                if (bean.status == 1) {
                    hideProgressBar();
                    showToast("预定失败，请查看时间是否有冲突");
                } else {
                    startRequestBook(meetingName, meetingIntroduce);
                }
            }
        });
    }

    private void startRequestBook(String meetingName, String meetingIntroduce) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.RequestBookBody[0], meetingName);
        body.add(Api.RequestBookBody[1], meetingIntroduce);
        body.add(Api.RequestBookBody[2], meetingRoomId + "");
        body.add(Api.RequestBookBody[3], UserAccountUtils.getUserInfo(getApplication()).getUserId
                () + "");
        body.add(Api.RequestBookBody[4], getDateString(startDate));
        body.add(Api.RequestBookBody[5], getDateString(endDate));
        final Request request = new Request.Builder()
                .header(Api.RequestBookHeader[0], Api.RequestBookHeader[1])
                .url(Api.RequestBookApi)
                .addHeader("token", UserAccountUtils.getUserToken(getApplication()).getToken())
                .post(body.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideProgressBar();
                showToast("网络好像出错了呢");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(BookRoomDetailActivity.this, new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            UserAccountUtils.setUserInfo(newInfo, getApplication());
                            UserAccountUtils.setUserToken(newToken, getApplication());
                        }
                    });
                    return;
                }
                RoomOfMeetingMessageBean bean = new Gson().fromJson(msg, RoomOfMeetingMessageBean
                        .class);
                if (bean.status == 1) {
                    hideProgressBar();
                    showToast("预定失败");
                } else {
                    hideProgressBar();
                    showToast("预订成功");
                    bean.data.save();
                    Calendar cale = Calendar.getInstance();
                    Calendar cale2 = Calendar.getInstance();
                    cale2.setTime(startDate);
                    int len = cale2.get(Calendar.DAY_OF_YEAR) - cale.get(Calendar.DAY_OF_YEAR) + 1;
                    Intent intent = new Intent();
                    intent.putExtra("dateLen", len);
                    setResult(RESULT_OK, intent);
                    saveFlag = true;
                    Intent intent1 = new Intent(BookRoomDetailActivity.this,
                            MeetingDetailsActivity.class);
                    intent1.putExtra("meetingId", String.valueOf(bean.data.meetingId));
                    startActivity(intent1);
                    finish();
                }
            }
        });
    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BookRoomDetailActivity.this, text, Toast.LENGTH_SHORT).show();
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

    private String getDateString(Date date) {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
    }
}
