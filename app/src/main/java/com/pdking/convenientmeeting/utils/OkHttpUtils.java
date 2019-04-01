package com.pdking.convenientmeeting.utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author liupeidong
 * Created on 2019/4/1 10:11
 */
public class OkHttpUtils {

    private static OkHttpClient client = new OkHttpClient();

    public static void requestHelper(Request request, Callback callback) {
        client.newCall(request).enqueue(callback);
    }

}
