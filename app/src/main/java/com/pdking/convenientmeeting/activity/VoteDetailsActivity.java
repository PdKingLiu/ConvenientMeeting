package com.pdking.convenientmeeting.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.TitleView;

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

    private boolean singleFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_vote_details);
        ButterKnife.bind(this);
        SystemUtil.setTitleMode(getWindow());
        setListener();
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

    @Override
    public void onClick(View v) {
        int which = -1;
        Log.d("Lpp", "onClick: " + v.getId());
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
        if (singleFlag) {
            singleFlag = false;
        } else {
            singleFlag = true;
        }
    }
}
