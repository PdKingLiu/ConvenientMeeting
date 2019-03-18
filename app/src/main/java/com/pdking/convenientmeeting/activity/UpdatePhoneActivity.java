package com.pdking.convenientmeeting.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.weight.TitleView;

import butterknife.BindView;

public class UpdatePhoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_update_phone);
    }
}
