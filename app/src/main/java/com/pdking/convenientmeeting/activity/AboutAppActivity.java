package com.pdking.convenientmeeting.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.TitleView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutAppActivity extends AppCompatActivity {

    @BindView(R.id.title)
    TitleView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_about_app);
        ButterKnife.bind(this);
        SystemUtil.setTitleMode(getWindow());
        titleView.setLeftClickListener(new TitleView.LeftClickListener() {
            @Override
            public void OnLeftButtonClick() {
                finish();
            }
        });
    }
}
