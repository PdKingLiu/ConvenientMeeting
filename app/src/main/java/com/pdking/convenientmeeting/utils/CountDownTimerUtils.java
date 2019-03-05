package com.pdking.convenientmeeting.utils;

import android.os.CountDownTimer;
import android.widget.Button;

/**
 * @author liupeidong
 * Created on 2019/2/15 13:39
 */
public class CountDownTimerUtils extends CountDownTimer {

    private Button mButton;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and
     *                          {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */

    public CountDownTimerUtils(Button mButton ,long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mButton = mButton;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mButton.setClickable(false);
        String s = millisUntilFinished / 1000 + "秒后重新获取";
        mButton.setText(s);
    }

    @Override
    public void onFinish() {
        mButton.setText("重新获取验证码");
        mButton.setClickable(true);//重新获得点击
    }
}
