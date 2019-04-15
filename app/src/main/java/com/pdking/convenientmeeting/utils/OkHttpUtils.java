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
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .build();

    public static void requestHelper(Request request, Callback callback) {
        client.newCall(request).enqueue(callback);
    }

}
