package com.pdking.convenientmeeting.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.pdking.convenientmeeting.R;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author liupeidong
 * Created on 2019/1/30 17:52
 */
public class RecordFragment extends Fragment {

    private View mView;

    private PieChart pieChart;

    private static RecordFragment INSTANCE = null;

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
        initView(mView);
        initChart();
        return mView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initChart();
        }
    }

    private void initChart() {
        List<PieEntry> pieCharts = new ArrayList<>();
        pieCharts.add(new PieEntry(10, "缺勤"));
        pieCharts.add(new PieEntry(20, "请假"));
        pieCharts.add(new PieEntry(30, "迟到"));
        pieCharts.add(new PieEntry(40, "正常"));
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
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.animateX(2000, Easing.EaseOutCirc);
        Description description = new Description();
        description.setText("");
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

    private void initView(View mView) {
        pieChart = mView.findViewById(R.id.pie_chart);
    }


}
