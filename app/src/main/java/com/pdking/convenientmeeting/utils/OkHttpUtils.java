package com.pdking.convenientmeeting.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @author liupeidong
 * Created on 2019/4/1 10:11
 */
public class OkHttpUtils {

    private static OkHttpClient client = new OkHttpClient.Builder()
            .writeTimeout(15,  TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();

    public static void requestHelper(Request request, Callback callback) {
        client.newCall(request).enqueue(callback);
    }

}
