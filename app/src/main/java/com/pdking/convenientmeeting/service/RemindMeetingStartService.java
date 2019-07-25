package com.pdking.convenientmeeting.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.activity.MainActivity;
import com.pdking.convenientmeeting.db.MeetingMessage;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.List;

public class RemindMeetingStartService extends Service {

    private NotificationManager manager;

    private Notification notification;

    public RemindMeetingStartService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                List<MeetingMessage> beanList = LitePal.where("meetingType = ?", "1").find
                        (MeetingMessage.class);
                if (beanList == null || beanList.size() == 0) {
                    return;
                } else {
                    try {
                        for (int i = 0; i < beanList.size(); i++) {
                            MeetingMessage message = beanList.get(i);
                            long startTime = format.parse(message.startTime).getTime();
                            if ((startTime - now) / 1000 / 60 <= 20
                                    && (startTime - now) / 1000 / 60 > 0
                                    && message.isPoll == -1) {
                                message.isPoll = 1;
                                showNotification("您有一个会议即将开始，请及时参加会议：" + message.meetingName,
                                        message);
                            }
                        }
                        LitePal.deleteAll(MeetingMessage.class, "meetingType = ?", "1");
                        LitePal.saveAll(beanList);
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        super.onCreate();
    }

    public void showNotification(String text, MeetingMessage message) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("isComePoll", 1);
        intent.putExtra("meetingId", message.meetingId + "");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notification = new NotificationCompat.Builder(this)
                .setContentText(text)
                .setContentTitle("通知")
                .setSmallIcon(R.mipmap.icon_query_master)
                .setWhen(System.currentTimeMillis())
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap
                        .icon_query_master))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        manager.notify(message.meetingId, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
