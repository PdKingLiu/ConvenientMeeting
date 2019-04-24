package com.pdking.convenientmeeting.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.activity.BookRoomDetailActivity;
import com.pdking.convenientmeeting.activity.ModificationUserDataActivity;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.MeetingMessage;
import com.pdking.convenientmeeting.db.MeetingMessageBean;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.OkHttpUtils;
import com.pdking.convenientmeeting.weight.TitleView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * @author liupeidong
 * Created on 2019/1/30 17:52
 */
public class RecordFragment extends Fragment implements View.OnClickListener {

    final private int UPDATE_USER_DATA = 1;

    private View mView;
    private PieChart pieChart;
    private CircleImageView civUserIcon;
    private UserInfo userInfo;
    private File iconFile;
    private LinearLayout llUserData;
    private LinearLayout llSwitchoverChart;
    private LinearLayout llYear;
    private TextView tvSwitchoverChart;
    private TextView tvYear;
    private TextView tvProportion;
    private SmartRefreshLayout smartRefreshLayout;
    private static RecordFragment INSTANCE = null;
    private boolean chartTimeFlag = true;
    private Calendar calendar = Calendar.getInstance();
    private UserToken userToken;
    private List<MeetingMessage> beanList;
    private List<MeetingMessage> yearList;
    private Date date = new Date();
    private TextView tvSumAbsence;
    private TextView tvSumLeave;
    private TextView tvSumLate;
    private TextView tvSumNormal;
    private TextView tvSumOrganize;
    private TextView tvSumJoin;
    private TextView tvSumCancel;
    private TextView tvName;
    private RelativeLayout rlAbsence;
    private RelativeLayout rlLeave;
    private RelativeLayout rlLate;
    private RelativeLayout rlNormal;
    private RelativeLayout rlOrganize;
    private RelativeLayout rlJoin;
    private RelativeLayout rlCancel;

    private float[] chartStatus = {10, 20, 30, 40};
    private float[] chartIdentity = {0, 0};

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format = new
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static RecordFragment getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new RecordFragment();
        }
        return INSTANCE;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.layout_recordfragment, container, false);
        init();
        initView(mView);
        pieChart.setVisibility(View.INVISIBLE);
        initChart();
        return mView;
    }

    private void init() {
        userInfo = LitePal.findAll(UserInfo.class).get(0);
        File file = new File(getContext().getExternalFilesDir(null), "/user/userIcon");
        if (!file.exists()) {
            file.mkdirs();
        }
        iconFile = new File(file, "user_icon_clip_" + userInfo.getPhone() + ".jpg");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (chartTimeFlag) {
                initChart();
            } else {
                initIdentityChart();
            }
        }
    }

    private void initChart() {
        List<PieEntry> pieCharts = new ArrayList<>();
        PieEntry pieEntry1 = new PieEntry(chartStatus[0], "缺勤");
        PieEntry pieEntry2 = new PieEntry(chartStatus[1], "请假");
        PieEntry pieEntry3 = new PieEntry(chartStatus[2], "迟到");
        PieEntry pieEntry4 = new PieEntry(chartStatus[3], "正常");
        pieCharts.add(pieEntry1);
        pieCharts.add(pieEntry2);
        pieCharts.add(pieEntry3);
        pieCharts.add(pieEntry4);
        PieDataSet pieDataSet = new PieDataSet(pieCharts, "");
        if (chartStatus[0] + chartStatus[1] + chartStatus[2] + chartStatus[3] == 0) {
            pieDataSet = new PieDataSet(pieCharts, "暂无数据");
        } else {
        }
        PieData data = new PieData(pieDataSet);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(14f);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter());
        List<Integer> integerList = new ArrayList<>();
        integerList.add(getResources().getColor(R.color.pie_blue));
        integerList.add(getResources().getColor(R.color.pie_green));
        integerList.add(getResources().getColor(R.color.pie_orange));
        integerList.add(getResources().getColor(R.color.pie_yellow));
        pieDataSet.setHighlightEnabled(true);
        pieDataSet.setColors(integerList);
        pieDataSet.setSliceSpace(5);
        pieChart.setData(data);
        pieChart.invalidate(); // refresh
        pieChart.getLegend();
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.animateX(2000, Easing.EaseOutCirc);
        Description description = new Description();
        if ((int) chartStatus[0] + (int) chartStatus[1] + (int) chartStatus[2] + (int)
                chartStatus[3] == 0) {
            description.setText("暂无数据");
        } else {
            description.setText("");
        }
        description.setXOffset(20f);
        description.setYOffset(20f);
        pieChart.setDescription(description);
        Legend l = pieChart.getLegend();
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(0);
        l.setYOffset(20);
        // 输入标签样式
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);
    }

    private void initView(View view) {
        pieChart = view.findViewById(R.id.pie_chart);
        civUserIcon = view.findViewById(R.id.civ_user_icon);
        llUserData = view.findViewById(R.id.ll_user_data);
        llSwitchoverChart = view.findViewById(R.id.ll_switchover_chart);
        tvSwitchoverChart = view.findViewById(R.id.tv_switchover_chart);
        llYear = view.findViewById(R.id.ll_year);
        tvSumAbsence = view.findViewById(R.id.tv_sum_absence);
        tvSumLeave = view.findViewById(R.id.tv_sum_leave);
        tvSumLate = view.findViewById(R.id.tv_sum_late);
        tvSumNormal = view.findViewById(R.id.tv_sum_normal);
        tvSumOrganize = view.findViewById(R.id.tv_sum_organize);
        tvSumJoin = view.findViewById(R.id.tv_sum_join);
        tvSumCancel = view.findViewById(R.id.tv_sum_cancel);
        rlAbsence = view.findViewById(R.id.rl_absence);
        rlLeave = view.findViewById(R.id.rl_leave);
        rlLate = view.findViewById(R.id.rl_late);
        rlNormal = view.findViewById(R.id.rl_normal);
        rlOrganize = view.findViewById(R.id.rl_organize);
        rlJoin = view.findViewById(R.id.rl_join);
        rlCancel = view.findViewById(R.id.rl_cancel);
        tvSumCancel = view.findViewById(R.id.tv_sum_cancel);
        tvName = view.findViewById(R.id.tv_name);
        tvYear = view.findViewById(R.id.tv_year);
        tvProportion = view.findViewById(R.id.tv_proportion);
        smartRefreshLayout = view.findViewById(R.id.srl_flush);
        rlAbsence.setOnClickListener(this);
        rlLeave.setOnClickListener(this);
        rlLate.setOnClickListener(this);
        rlNormal.setOnClickListener(this);
        rlOrganize.setOnClickListener(this);
        rlJoin.setOnClickListener(this);
        rlCancel.setOnClickListener(this);
        tvName.setOnClickListener(this);
        civUserIcon.setOnClickListener(this);
        llSwitchoverChart.setOnClickListener(this);
        llYear.setOnClickListener(this);
        llUserData.setOnClickListener(this);
        calendar.setTime(date);
        tvYear.setText(calendar.get(Calendar.YEAR) + "年");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case UPDATE_USER_DATA:
                if (resultCode == RESULT_OK && data != null) {
                    int status = data.getIntExtra("status", -1);
                    if (status == 1) {
                        changeFragmentUI();
                    }
                }
        }
    }

    private void changeFragmentUI() {
        final UserInfo info = LitePal.findAll(UserInfo.class).get(0);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean flag = false;
                if (!info.avatarUrl.equals(userInfo.avatarUrl)) {
                    try {
                        Bitmap bitmap = new Compressor(getContext()).compressToBitmap(iconFile);
                        civUserIcon.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    flag = true;
                }
                if (flag) {
                    userInfo = info;
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        beanList = new ArrayList<>();
        yearList = new ArrayList<>();
        userInfo = LitePal.findAll(UserInfo.class).get(0);
        userToken = LitePal.findAll(UserToken.class).get(0);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshData();
            }
        });
        smartRefreshLayout.autoRefresh();
        loadUserData();
    }

    private void refreshData() {
        FormBody.Builder body = new FormBody.Builder()
                .add("token", userToken.getToken())
                .add(Api.RequestUserMeetingListBody[0], userInfo.getUserId() + "")
                .add(Api.RequestUserMeetingListBody[1], 2 + "");
        Request request = new Request.Builder()
                .post(body.build())
                .header(Api.RequestUserMeetingListHeader[0], Api.RequestUserMeetingListHeader[1])
                .addHeader("token", userToken.getToken())
                .url(Api.RequestUserMeetingListApi)
                .build();
        OkHttpUtils.requestHelper(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                smartRefreshLayout.finishRefresh(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                if (msg.contains("token过期!")) {
                    LoginStatusUtils.stateFailure(getActivity(), new LoginCallBack() {
                        @Override
                        public void newMessageCallBack(UserInfo newInfo, UserToken newToken) {
                            userInfo = newInfo;
                            userToken = newToken;
                        }
                    });
                    return;
                }
                MeetingMessageBean bean = new Gson().fromJson(msg, MeetingMessageBean.class);
                if (bean != null && bean.status == 0) {
                    for (MeetingMessage message : bean.data) {
                        message.meetingType = 1;
                    }
                    smartRefreshLayout.finishRefresh(true);
                    beanList.clear();
                    beanList.addAll(bean.data);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            changeUIByDataAndDate();
                        }
                    });
                } else {
                    smartRefreshLayout.finishRefresh(false);
                }
            }
        });

    }

    private void changeUIByDataAndDate() {
        yearList.clear();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        Date dateTem;
        try {
            for (int i = 0; i < beanList.size(); i++) {
                dateTem = format.parse(beanList.get(i).startTime);
                calendar.setTime(dateTem);
                if (calendar.get(Calendar.YEAR) == year) {
                    yearList.add(beanList.get(i));
                }
            }
        } catch (Exception e) {

        }
        int normal = 0;
        int absenteeism = 0;
        int late = 0;
        int leave = 0;
        int total = yearList.size();
        int master = 0;
        int parter = 0;
        for (int i = 0; i < yearList.size(); i++) {
            if (yearList.get(i).userStatus == 1) {
                normal++;
            } else if (yearList.get(i).userStatus == 2) {
                absenteeism++;
            } else if (yearList.get(i).userStatus == 3) {
                late++;
            } else if (yearList.get(i).userStatus == 4) {
                leave++;
            }
            if (yearList.get(i).masterId == userInfo.userId) {
                master++;
            } else {
                parter++;
            }
        }
        tvSumAbsence.setText(absenteeism + "次");
        tvSumLeave.setText(leave + "次");
        tvSumLate.setText(late + "次");
        tvSumNormal.setText(normal + "次");
        tvSumOrganize.setText(master + "次");
        tvSumJoin.setText(parter + "次");
        tvSumCancel.setText(0 + "次");
        chartStatus[0] = (float) ((((absenteeism * 1.0) / total) * 1000) / 10.0);
        chartStatus[1] = (float) ((((leave * 1.0) / total) * 1000) / 10.0);
        chartStatus[2] = (float) ((((late * 1.0) / total) * 1000) / 10.0);
        chartStatus[3] = (float) ((((normal * 1.0) / total) * 1000) / 10.0);
        chartIdentity[0] = (float) ((((master * 1.0) / master + parter) * 1000) / 10.0);
        chartIdentity[1] = (float) ((((parter * 1.0) / master + parter) * 1000) / 10.0);
        tvProportion.setText(normal + " / " + total);
        pieChart.setVisibility(View.VISIBLE);
        initChart();
    }

    private void loadUserData() {
        if (iconFile.exists()) {
            Glide.with(this)
                    .load(iconFile)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy
                            .NONE).skipMemoryCache(true))
                    .into(civUserIcon);
        } else {
            Request request = new Request.Builder()
                    .url(userInfo.avatarUrl)
                    .build();
            OkHttpUtils.requestHelper(request, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    InputStream inputStream = null;
                    byte[] bytes = new byte[1024];
                    FileOutputStream fileOutputStream = null;
                    int len;
                    try {
                        inputStream = response.body().byteStream();
                        fileOutputStream = new FileOutputStream(iconFile);
                        while ((len = inputStream.read(bytes)) != -1) {
                            fileOutputStream.write(bytes, 0, len);
                        }
                        fileOutputStream.flush();
                        inputStream.close();
                        fileOutputStream.close();
                        Glide.with(getContext())
                                .load(iconFile)
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy
                                        .NONE).skipMemoryCache(true))
                                .into(civUserIcon);
                    } catch (Exception e) {
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_absence:
                Toast.makeText(getContext(), "rl_absence", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_leave:
                Toast.makeText(getContext(), "rl_leave", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_late:
                Toast.makeText(getContext(), "rl_late", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_normal:
                Toast.makeText(getContext(), "rl_normal", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_organize:
                Toast.makeText(getContext(), "rl_organize", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_join:
                Toast.makeText(getContext(), "rl_join", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_cancel:
                Toast.makeText(getContext(), "rl_cancel", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_name:
            case R.id.civ_user_icon:
            case R.id.ll_user_data:
                startActivityForResult(new Intent(getContext(), ModificationUserDataActivity
                        .class), UPDATE_USER_DATA);
                break;
            case R.id.ll_switchover_chart:
                if (chartTimeFlag) {
                    initIdentityChart();
                    tvSwitchoverChart.setText("切换出勤占比图");
                    chartTimeFlag = false;
                } else {
                    initChart();
                    tvSwitchoverChart.setText("切换会议身份图");
                    chartTimeFlag = true;
                }
                break;
            case R.id.ll_year:
                chooseYear();
                break;
        }
    }

    private void chooseYear() {
        TimePickerView pvTime = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                queryData(date);
            }
        }).setType(new boolean[]{true, false, false, false, false, false})
                .setTitleText("注：")
                .build();
        calendar.setTime(date);
        pvTime.setDate(calendar);
        pvTime.show();
    }

    private void queryData(Date date) {
        this.date = date;
        calendar.setTime(date);
        tvYear.setText(calendar.get(Calendar.YEAR) + "年");
        changeUIByDataAndDate();
    }

    private void initIdentityChart() {
        List<PieEntry> pieCharts = new ArrayList<>();
        pieCharts.add(new PieEntry(chartIdentity[0], "组织者"));
        pieCharts.add(new PieEntry(chartIdentity[1], "参与者"));
        PieDataSet pieDataSet = new PieDataSet(pieCharts, "");
        PieData data = new PieData(pieDataSet);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(14f);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter());
        List<Integer> integerList = new ArrayList<>();
        integerList.add(getResources().getColor(R.color.pie_blue));
        integerList.add(getResources().getColor(R.color.pie_orange));
        pieDataSet.setHighlightEnabled(true);
        pieDataSet.setColors(integerList);
        pieDataSet.setSliceSpace(5);
        pieChart.setData(data);
        pieChart.invalidate(); // refresh
        pieChart.getLegend();
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.animateX(2000, Easing.EaseOutCirc);
        Description description = new Description();
        if ((int) chartIdentity[0] + (int) chartIdentity[1] == 0) {
            description.setText("暂无数据");
        } else {
            description.setText("");
        }
        description.setXOffset(20f);
        description.setYOffset(20f);
        pieChart.setDescription(description);
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(0);
        l.setYOffset(20);
        // 输入标签样式
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);
    }
}
