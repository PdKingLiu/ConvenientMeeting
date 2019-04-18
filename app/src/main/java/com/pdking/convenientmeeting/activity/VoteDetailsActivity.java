package com.pdking.convenientmeeting.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.TitleView;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VoteDetailsActivity extends AppCompatActivity implements CompoundButton
        .OnCheckedChangeListener, View.OnClickListener, TitleView.LeftClickListener, TitleView
        .RightClickListener {

    @BindView(R.id.title)
    TitleView title;
    @BindView(R.id.rb_1)
    RadioButton rb1;
    @BindView(R.id.rb_2)
    RadioButton rb2;
    @BindView(R.id.rb_3)
    RadioButton rb3;
    @BindView(R.id.rb_4)
    RadioButton rb4;
    @BindView(R.id.rb_5)
    RadioButton rb5;
    @BindView(R.id.ll_1)
    LinearLayout ll1;
    @BindView(R.id.ll_2)
    LinearLayout ll2;
    @BindView(R.id.ll_3)
    LinearLayout ll3;
    @BindView(R.id.ll_4)
    LinearLayout ll4;
    @BindView(R.id.ll_5)
    LinearLayout ll5;
    @BindView(R.id.iv_1)
    ImageView iv1;
    @BindView(R.id.iv_2)
    ImageView iv2;
    @BindView(R.id.iv_3)
    ImageView iv3;
    @BindView(R.id.iv_4)
    ImageView iv4;
    @BindView(R.id.iv_5)
    ImageView iv5;
    @BindView(R.id.tv_kind)
    TextView tvKind;
    @BindView(R.id.tv_ticket_sum)
    TextView tvTicketSum;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_ticket_1)
    TextView tvTicket1;
    @BindView(R.id.tv_ticket_2)
    TextView tvTicket2;
    @BindView(R.id.tv_ticket_3)
    TextView tvTicket3;
    @BindView(R.id.tv_ticket_4)
    TextView tvTicket4;
    @BindView(R.id.tv_ticket_5)
    TextView tvTicket5;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.btn_vote)
    Button btnVote;
    @BindView(R.id.ll_choose_1)
    LinearLayout llTicket1;
    @BindView(R.id.ll_choose_2)
    LinearLayout llTicket2;
    @BindView(R.id.ll_choose_3)
    LinearLayout llTicket3;
    @BindView(R.id.ll_choose_4)
    LinearLayout llTicket4;
    @BindView(R.id.ll_choose_5)
    LinearLayout llTicket5;
    @BindView(R.id.ll_proportion_1)
    LinearLayout llProportion1;
    @BindView(R.id.ll_proportion_2)
    LinearLayout llProportion2;
    @BindView(R.id.ll_proportion_3)
    LinearLayout llProportion3;
    @BindView(R.id.ll_proportion_4)
    LinearLayout llProportion4;
    @BindView(R.id.ll_proportion_5)
    LinearLayout llProportion5;
    @BindView(R.id.view_shape_1)
    View viewShape1;
    @BindView(R.id.view_shape_2)
    View viewShape2;
    @BindView(R.id.view_shape_3)
    View viewShape3;
    @BindView(R.id.view_shape_4)
    View viewShape4;
    @BindView(R.id.view_shape_5)
    View viewShape5;

    private boolean singleFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_vote_details);
        ButterKnife.bind(this);
        SystemUtil.setTitleMode(getWindow());
        setListener();
    }

    private void setWhichIcon(int[] whichIcon) {
        iv1.setVisibility(View.INVISIBLE);
        iv2.setVisibility(View.INVISIBLE);
        iv3.setVisibility(View.INVISIBLE);
        iv4.setVisibility(View.INVISIBLE);
        iv5.setVisibility(View.INVISIBLE);
        for (int i = 0; i < whichIcon.length; i++) {
            switch (whichIcon[i]) {
                case 1:
                    iv1.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    iv2.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    iv3.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    iv4.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    iv5.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void setItemSum(int sum) {
        llTicket3.setVisibility(View.GONE);
        llTicket4.setVisibility(View.GONE);
        llTicket5.setVisibility(View.GONE);
        if (sum == 2) {
        } else if (sum == 3) {
            llTicket3.setVisibility(View.VISIBLE);
        } else if (sum == 4) {
            llTicket3.setVisibility(View.VISIBLE);
            llTicket4.setVisibility(View.VISIBLE);
        } else if (sum == 5) {
            llTicket3.setVisibility(View.VISIBLE);
            llTicket4.setVisibility(View.VISIBLE);
            llTicket5.setVisibility(View.VISIBLE);
        }
    }

    private void setProportion(int weightSum, int[] proportions) {
        llProportion1.setWeightSum((float) weightSum);
        llProportion2.setWeightSum((float) weightSum);
        llProportion3.setWeightSum((float) weightSum);
        llProportion4.setWeightSum((float) weightSum);
        llProportion5.setWeightSum((float) weightSum);
        viewShape1.setLayoutParams(new LinearLayout.LayoutParams(0, viewShape1.getHeight(),
                proportions[0]));
        viewShape2.setLayoutParams(new LinearLayout.LayoutParams(0, viewShape2.getHeight(),
                proportions[1]));
        viewShape3.setLayoutParams(new LinearLayout.LayoutParams(0, viewShape3.getHeight(),
                proportions[2]));
        viewShape4.setLayoutParams(new LinearLayout.LayoutParams(0, viewShape4.getHeight(),
                proportions[3]));
        viewShape5.setLayoutParams(new LinearLayout.LayoutParams(0, viewShape5.getHeight(),
                proportions[4]));
    }

    private void setListener() {
        rb1.setOnCheckedChangeListener(this);
        rb2.setOnCheckedChangeListener(this);
        rb3.setOnCheckedChangeListener(this);
        rb4.setOnCheckedChangeListener(this);
        rb5.setOnCheckedChangeListener(this);
        ll1.setOnClickListener(this);
        ll2.setOnClickListener(this);
        ll3.setOnClickListener(this);
        ll4.setOnClickListener(this);
        ll5.setOnClickListener(this);
        rb1.setOnClickListener(this);
        rb2.setOnClickListener(this);
        rb3.setOnClickListener(this);
        rb4.setOnClickListener(this);
        rb5.setOnClickListener(this);
        title.setLeftClickListener(this);
        title.setRightClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!singleFlag) {
            return;
        }
        int which;
        switch (buttonView.getId()) {
            case R.id.rb_1:
                which = 1;
                break;
            case R.id.rb_2:
                which = 2;
                break;
            case R.id.rb_3:
                which = 3;
                break;
            case R.id.rb_4:
                which = 4;
                break;
            default:
                which = 5;
        }
        if (isChecked) {
            changeRadioButton(which);
        }
    }

    private void changeRadioButton(int which) {
        if (which == 1) {
            rb1.setChecked(true);
        } else {
            rb1.setChecked(false);
        }
        if (which == 2) {
            rb2.setChecked(true);
        } else {
            rb2.setChecked(false);
        }
        if (which == 3) {
            rb3.setChecked(true);
        } else {
            rb3.setChecked(false);
        }
        if (which == 4) {
            rb4.setChecked(true);
        } else {
            rb4.setChecked(false);
        }
        if (which == 5) {
            rb5.setChecked(true);
        } else {
            rb5.setChecked(false);
        }
    }

    public void setKindAndTicketSum(int kind, int sum) {
        if (kind == 1) {
            tvKind.setText("单选");
        } else {
            tvKind.setText("多选");
        }
        tvTicketSum.setText("共 " + sum + " 票");
    }

    @Override
    public void onClick(View v) {
        int which = -1;
        switch (v.getId()) {
            case R.id.ll_1:
                which = 1;
                break;
            case R.id.ll_2:
                which = 2;
                break;
            case R.id.ll_3:
                which = 3;
                break;
            case R.id.ll_4:
                which = 4;
                break;
            case R.id.ll_5:
                which = 5;
                break;
        }
        if (!singleFlag) {
            switch (which) {
                case 1:
                    if (rb1.isChecked()) {
                        rb1.setChecked(false);
                    } else {
                        rb1.setChecked(true);
                    }
                    break;
                case 2:
                    if (rb2.isChecked()) {
                        rb2.setChecked(false);
                    } else {
                        rb2.setChecked(true);
                    }
                    break;
                case 3:
                    if (rb3.isChecked()) {
                        rb3.setChecked(false);
                    } else {
                        rb3.setChecked(true);
                    }
                    break;
                case 4:
                    if (rb4.isChecked()) {
                        rb4.setChecked(false);
                    } else {
                        rb4.setChecked(true);
                    }
                    break;
                case 5:
                    if (rb5.isChecked()) {
                        rb5.setChecked(false);
                    } else {
                        rb5.setChecked(true);
                    }
                    break;
            }
        } else {
            if (which != -1) {
                changeRadioButton(which);
            }
        }
    }

    @Override
    public void OnLeftButtonClick() {
        finish();
    }

    @Override
    public void OnRightButtonClick() {
        Random random = new Random();
        int sum = 0;
        int which = random.nextInt(5) % 5 + 1;
        int item = random.nextInt(2) + 3;
        int ticket[] = new int[5];
        for (int i = 0; i < ticket.length; i++) {
            ticket[i] = random.nextInt(20);
            sum += ticket[i];
        }
        setItemSum(item);
        setKindAndTicketSum(1, sum);
        setWhichIcon(new int[]{which});
        setProportion(sum, ticket);
        setItemCount(ticket);
    }

    private void setItemCount(int ticket[]) {
        tvTicket1.setText(ticket[0] + "票");
        tvTicket2.setText(ticket[1] + "票");
        tvTicket3.setText(ticket[2] + "票");
        tvTicket4.setText(ticket[3] + "票");
        tvTicket5.setText(ticket[4] + "票");
    }
}
