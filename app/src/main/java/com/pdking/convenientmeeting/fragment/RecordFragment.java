package com.pdking.convenientmeeting.fragment;

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
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.activity.ModificationUserDataActivity;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.utils.OkHttpUtils;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
        init();
        initView(mView);
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

    private void initView(View view) {
        pieChart = view.findViewById(R.id.pie_chart);
        civUserIcon = view.findViewById(R.id.civ_user_icon);
        llUserData = view.findViewById(R.id.ll_user_data);
        llUserData.setOnClickListener(this);
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
        loadUserData();
    }

    private void loadUserData() {
        if (iconFile.exists()) {
            Log.d("Lpp", "civUserIcon:file ");
            Glide.with(this)
                    .load(iconFile)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy
                            .NONE).skipMemoryCache(true))
                    .into(civUserIcon);
        } else {
            Log.d("Lpp", "civUserIcon:avatarUrl ");
            Request request = new Request.Builder()
                    .url(userInfo.avatarUrl)
                    .build();
            OkHttpUtils.requestHelper(request, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("Lpp", "onFailure: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    InputStream inputStream = null;
                    byte[] bytes = new byte[1024];
                    FileOutputStream fileOutputStream = null;
                    long current = 0;
                    int len;
                    try {
                        long total = response.body().contentLength();
                        Log.d("Lpp", "onResponse: " + total);
                        inputStream = response.body().byteStream();
                        fileOutputStream = new FileOutputStream(iconFile);
                        while ((len = inputStream.read(bytes)) != -1) {
                            current += len;
                            fileOutputStream.write(bytes, 0, len);
                            Log.d("Lpp", "onResponse: " + current);
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
                        Log.d("Lpp", "onResponse: " + e.getMessage());
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_user_data:
                startActivityForResult(new Intent(getContext(), ModificationUserDataActivity
                        .class), UPDATE_USER_DATA);
                break;

        }
    }
}
