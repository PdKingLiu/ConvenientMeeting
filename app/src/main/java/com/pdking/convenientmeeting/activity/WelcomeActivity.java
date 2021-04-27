package com.pdking.convenientmeeting.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.pdking.convenientmeeting.R;

public class WelcomeActivity extends AppCompatActivity {

    CountDownTimer timer;
    private Button btnEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_welcome_acticity);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        btnEnter = findViewById(R.id.btn_enter);
        timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                btnEnter.setText(millisUntilFinished / 1000 + " 跳过");
            }

            @Override
            public void onFinish() {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        };
        timer.start();
    }

    public void enter(View view) {
        timer.onFinish();
        timer.cancel();
    }
}
