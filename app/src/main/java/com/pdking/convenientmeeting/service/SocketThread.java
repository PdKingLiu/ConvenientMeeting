package com.pdking.convenientmeeting.service;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.activity.MainActivity;
import com.pdking.convenientmeeting.utils.UserAccountUtils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * @author liupeidong
 * Created on 2019/5/5 21:01
 */
public class SocketThread extends Thread {

    private static final long HEART_BEAT_RATE = 15 * 1000;//每隔15秒进行一次对长连接的心跳检测
    private static String WEBSOCKET_HOST_AND_PORT = "ws://www.shidongxuan" +
            ".top:8080/smartMeeting_Web/socket/";//可替换为自己的主机名和端口号
    private static int i = 0;
    private WebSocket mWebSocket;
    private Handler mHandler = new Handler();
    private long sendTime = 0L;
    private Application application;

    private Notification notification;

    private NotificationManager manager;
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
                boolean isSuccess = mWebSocket.send("测试");//发送一个空消息给服务器，通过发送消息的成功失败来判断长连接的连接状态
                if (!isSuccess) {//长连接已断开
                    mHandler.removeCallbacks(heartBeatRunnable);
                    mWebSocket.cancel();//取消掉以前的长连接
                    new SocketThread(application).start();//创建一个新的连接
                } else {//长连接处于连接状态

                }
                sendTime = System.currentTimeMillis();
            }
            mHandler.postDelayed(this, HEART_BEAT_RATE);//每隔一定的时间，对长连接进行一次心跳检测
        }
    };
    private String TAG = "Lpp";

    public SocketThread(Application application) {
        this.application = application;
        manager = (NotificationManager) application.getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void run() {
        initSocket();
    }

    private void initSocket() {
        if (application != null) {
            if (UserAccountUtils.getUserInfo(application) != null) {
                WEBSOCKET_HOST_AND_PORT = WEBSOCKET_HOST_AND_PORT
                        + UserAccountUtils.getUserInfo(application).userId;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(WEBSOCKET_HOST_AND_PORT).build();
                client.newWebSocket(request, new WebSocketListener() {
                    @Override
                    public void onOpen(WebSocket webSocket, Response response) {
                        mWebSocket = webSocket;
                    }

                    @Override
                    public void onMessage(WebSocket webSocket, String text) {
                        showNotification(text);
                    }

                    @Override
                    public void onMessage(WebSocket webSocket, ByteString bytes) {
                        this.onMessage(webSocket, bytes.toString());
                    }

                    @Override
                    public void onClosing(WebSocket webSocket, int code, String reason) {
                    }

                    @Override
                    public void onClosed(WebSocket webSocket, int code, String reason) {
                    }

                    @Override
                    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response
                            response) {
                    }
                });
                client.dispatcher().executorService().shutdown();
                mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测

            }
        }
    }

    public void showNotification(String text) {
        if (application != null) {
            Intent intent = new Intent(application, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(application, 0, intent, 0);
            notification = new NotificationCompat.Builder(application)
                    .setContentText("您有一个投票即将结束，请查看")
                    .setContentTitle("通知")
                    .setSmallIcon(R.mipmap.icon_query_master)
                    .setWhen(System.currentTimeMillis())
                    .setLargeIcon(BitmapFactory.decodeResource(application.getResources(), R.mipmap
                            .icon_query_master))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            manager.notify(i++, notification);
        }
    }

}
