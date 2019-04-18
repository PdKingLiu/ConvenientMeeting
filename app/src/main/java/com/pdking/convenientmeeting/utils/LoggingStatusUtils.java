package com.pdking.convenientmeeting.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.pdking.convenientmeeting.activity.LoginActivity;
import com.pdking.convenientmeeting.common.ActivityContainer;
import com.pdking.convenientmeeting.common.Api;
import com.pdking.convenientmeeting.db.LoginBean;
import com.pdking.convenientmeeting.db.UserInfo;
import com.pdking.convenientmeeting.db.UserToken;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author liupeidong
 * Created on 2019/4/18 22:01
 */
public class LoggingStatusUtils {

    private static AlertDialog dialog;

    private static AlertDialog progress;

    public static void stateFailure(final Activity activity, final UserInfo userInfo, final
    LoginCallBack callBack) {
        dialog = new AlertDialog.Builder(activity)
                .setNegativeButton("退出登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                        ActivityContainer.removeAllActivity();
                    }
                })
                .setPositiveButton("重新登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        againLogin(activity, userInfo, callBack);
                    }
                })
                .setCancelable(false)
                .setMessage("登录状态失效，是否重新登录？")
                .setTitle("提示")
                .create();
    }

    private static void againLogin(final Activity activity, UserInfo userInfo, final LoginCallBack
            callBack) {
        progress = new ProgressDialog(activity);
        progress.setMessage("正在登录...");
        progress.setTitle("登录中");
        progress.setCancelable(false);
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody.Builder body = new FormBody.Builder();
        body.add(Api.LoginBody[0], userInfo.getPhone());
        body.add(Api.LoginBody[1], userInfo.getPassword());
        Request request = new Request.Builder()
                .url(Api.LoginApi)
                .post(body.build())
                .header(Api.LoginHeader[0], Api.LoginHeader[1])
                .build();
        UIUtils.showProgressBar(activity, progress);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtils.hideProgressBar(activity, progress);
                Log.d("Lpp", "连接登录失败: " + e.getMessage());
                UIUtils.showToast(activity, "连接登录失败,请重新登录");
                activity.startActivity(new Intent(activity, LoginActivity.class));
                activity.finish();
                ActivityContainer.removeAllActivity();
                progress.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                UIUtils.hideProgressBar(activity, progress);
                String message = response.body().string();
                LoginBean loginInfo = new Gson().fromJson(message, LoginBean.class);
                if (loginInfo != null) {
                    if (loginInfo.status == 1) {
                        UIUtils.showToast(activity, "密码错误,请重新登录");
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                        activity.finish();
                        ActivityContainer.removeAllActivity();
                    } else {
                        UIUtils.showToast(activity, "登录成功");
                        Log.d("Lpp", "登录成功: " + message);
                        UserToken userToken = new UserToken(loginInfo.msg);
                        UserInfo userInfo = loginInfo.data;
                        callBack.newMessageCallBack(userInfo, userToken);
                    }
                } else {
                    UIUtils.hideProgressBar(activity, progress);
                    UIUtils.showToast(activity, "连接登录失败,请重新登录");
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                    activity.finish();
                    ActivityContainer.removeAllActivity();
                }
                progress.dismiss();
            }
        });
    }
}
