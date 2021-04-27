package com.pdking.convenientmeeting.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.activity.DifferentTypesMeetingListActivity;
import com.pdking.convenientmeeting.activity.ModificationUserDataActivity;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.MeetingMessage;
import com.pdking.convenientmeeting.db.MeetingMessageBean;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;
import com.pdking.convenientmeeting.utils.LoginCallBack;
import com.pdking.convenientmeeting.utils.LoginStatusUtils;
import com.pdking.convenientmeeting.utils.OkHttpUtils;
import com.pdking.convenientmeeting.utils.UIUtils;
import com.pdking.convenientmeeting.utils.UserAccountUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

import static androidx.appcompat.app.AppCompatActivity.RESULT_OK;

/**
 * @author liupeidong
 * Created on 2019/1/30 17:52
 */
public class RecordFragment extends Fragment implements View.OnClickListener {

    final private int UPDATE_USER_DATA = 1;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format = new
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private View mView;
    private PieChart pieChart;
    private CircleImageView civUserIcon;
    private File iconFile;
    private LinearLayout llUserData;
    private LinearLayout llSwitchoverChart;
    private LinearLayout llYear;
    private TextView tvSwitchoverChart;
    private TextView tvYear;
    private TextView tvProportion;
    private SmartRefreshLayout smartRefreshLayout;
    private boolean chartTimeFlag = true;
    private Calendar calendar = Calendar.getInstance();
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
    private String email;
    private String avatarUrl;
    private int year = calendar.get(Calendar.YEAR);
    private float[] chartStatus = {10, 20, 30, 40};
    private float[] chartIdentity = {0, 0};

    public static RecordFragment getINSTANCE() {
        return new RecordFragment();
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
        File file = new File(getContext().getExternalFilesDir(null), "/user/userIcon");
        if (!file.exists()) {
            file.mkdirs();
        }
        iconFile = new File(file, "user_icon_clip_" + UserAccountUtils.getUserInfo(getActivity()
                .getApplication()).getPhone() + ".jpg");
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
        final PieEntry pieEntry1 = new PieEntry(chartStatus[0], "缺勤");
        final PieEntry pieEntry2 = new PieEntry(chartStatus[1], "请假");
        final PieEntry pieEntry3 = new PieEntry(chartStatus[2], "迟到");
        final PieEntry pieEntry4 = new PieEntry(chartStatus[3], "正常");
        pieCharts.add(pieEntry1);
        pieCharts.add(pieEntry2);
        pieCharts.add(pieEntry3);
        pieCharts.add(pieEntry4);
        PieDataSet pieDataSet = new PieDataSet(pieCharts, "");
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
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e.equalTo(pieEntry1)) {
                    rlAbsence.callOnClick();
                }
                if (e.equalTo(pieEntry2)) {
                    rlLeave.callOnClick();
                }
                if (e.equalTo(pieEntry3)) {
                    rlLate.callOnClick();
                }
                if (e.equalTo(pieEntry4)) {
                    rlNormal.callOnClick();
                }
            }

            @Override
            public void onNothingSelected() {
            }
        });
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean flag = false;
                if (!avatarUrl.equals(UserAccountUtils.getUserInfo(getActivity().getApplication()
                ).avatarUrl)) {
                    try {
                        Bitmap bitmap = new Compressor(getContext()).compressToBitmap(iconFile);
                        civUserIcon.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    flag = true;
                }
                if (flag) {
                    avatarUrl = UserAccountUtils.getUserInfo(getActivity().getApplication())
                            .avatarUrl;
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        email = UserAccountUtils.getUserInfo(getActivity().getApplication()).getEmail();
        avatarUrl = UserAccountUtils.getUserInfo(getActivity().getApplication()).avatarUrl;
        beanList = new ArrayList<>();
        yearList = new ArrayList<>();
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
        if (!UserAccountUtils.accountIsValid(getActivity().getApplication())) {
            UIUtils.showToast(getActivity(), "未知错误");
            return;
        }
        FormBody.Builder body = new FormBody.Builder()
                .add(Api.RequestUserMeetingListBody[0], UserAccountUtils.getUserInfo(getActivity
                        ().getApplication()).getUserId() + "")
                .add(Api.RequestUserMeetingListBody[1], 2 + "");
        Request request = new Request.Builder()
                .post(body.build())
                .header(Api.RequestUserMeetingListHeader[0], Api.RequestUserMeetingListHeader[1])
                .addHeader("token",
                        UserAccountUtils.getUserToken(getActivity().getApplication()).getToken())
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
                            UserAccountUtils.setUserInfo(newInfo, getActivity().getApplication());
                            UserAccountUtils.setUserToken(newToken, getActivity().getApplication());
                        }
                    });
                    smartRefreshLayout.finishRefresh(false);
                    return;
                }
                MeetingMessageBean bean = new Gson().fromJson(msg, MeetingMessageBean.class);
                if (bean != null && bean.status == 0) {
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
        year = calendar.get(Calendar.YEAR);
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
            if (yearList.get(i).masterId == UserAccountUtils.getUserInfo(getActivity()
                    .getApplication()).userId) {
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
        chartIdentity[0] = (float) ((((master * 1.0) / (master + parter)) * 1000) / 10.0);
        chartIdentity[1] = (float) ((((parter * 1.0) / (master + parter)) * 1000) / 10.0);
        tvName.setText(UserAccountUtils.getUserInfo(getActivity().getApplication()).getUsername());
        tvProportion.setText(normal + " / " + total);
        pieChart.setVisibility(View.VISIBLE);
        initChart();
    }

    private void loadUserData() {
        if (iconFile.exists()) {
            Glide.with(getContext())
                    .load(iconFile)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy
                            .NONE).skipMemoryCache(true))
                    .into(civUserIcon);
        } else {
            Request request = new Request.Builder()
                    .url(UserAccountUtils.getUserInfo(getActivity().getApplication()).avatarUrl)
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
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_absence:
                intent = new Intent(getContext(), DifferentTypesMeetingListActivity.class);
                intent.putExtra("year", year);
                intent.putExtra("userStatus", 2);
                startActivity(intent);
                break;
            case R.id.rl_leave:
                intent = new Intent(getContext(), DifferentTypesMeetingListActivity.class);
                intent.putExtra("year", year);
                intent.putExtra("userStatus", 4);
                startActivity(intent);
                break;
            case R.id.rl_late:
                intent = new Intent(getContext(), DifferentTypesMeetingListActivity.class);
                intent.putExtra("year", year);
                intent.putExtra("userStatus", 3);
                startActivity(intent);
                break;
            case R.id.rl_normal:
                intent = new Intent(getContext(), DifferentTypesMeetingListActivity.class);
                intent.putExtra("year", year);
                intent.putExtra("userStatus", 1);
                startActivity(intent);
                break;
            case R.id.rl_organize:
                intent = new Intent(getContext(), DifferentTypesMeetingListActivity.class);
                intent.putExtra("year", year);
                intent.putExtra("userKind", 1);
                startActivity(intent);
                break;
            case R.id.rl_join:
                intent = new Intent(getContext(), DifferentTypesMeetingListActivity.class);
                intent.putExtra("year", year);
                intent.putExtra("userKind", 2);
                startActivity(intent);
                break;
            case R.id.rl_cancel:
                intent = new Intent(getContext(), DifferentTypesMeetingListActivity.class);
                intent.putExtra("year", year);
                intent.putExtra("isCancel", 1);
                startActivity(intent);
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
        final PieEntry pieEntry1 = new PieEntry(chartIdentity[0], "组织者");
        final PieEntry pieEntry2 = new PieEntry(chartIdentity[1], "参与者");
        pieCharts.add(pieEntry1);
        pieCharts.add(pieEntry2);
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
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e.equalTo(pieEntry1)) {
                    rlOrganize.callOnClick();
                }
                if (e.equalTo(pieEntry2)) {
                    rlJoin.callOnClick();
                }
            }

            @Override
            public void onNothingSelected() {
            }
        });
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
