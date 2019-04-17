package com.pdking.convenientmeeting.activity;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.TitleView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private boolean[] isVisibleFlags = {true, true, false, false, false};
    private int itemSum = 2;
    private int kind = 1;
    private Date endTime;
    private int remind = 1;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_release_vote);
        ButterKnife.bind(this);
        SystemUtil.setTitleMode(getWindow());
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
                    kind = 2;
                } else if (list.get(options1).equals("提前24小时")) {
                    tvRemind.setText("提前24小时");
                    kind = 3;
                } else if (list.get(options1).equals("不提醒")) {
                    tvRemind.setText("不提醒");
                    kind = 4;
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
