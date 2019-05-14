package com.pdking.convenientmeeting.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class VoteRemindService extends Service {

    public VoteRemindService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new SocketThread(getApplication()).start();
    }
}
