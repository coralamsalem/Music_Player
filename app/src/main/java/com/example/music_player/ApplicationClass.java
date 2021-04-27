package com.example.music_player;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class ApplicationClass extends Application {
    public static final String CHANNEL_ID_1 ="channel1";
    public static final String CHANNEL_ID_2 ="channel2";
    public static final String ACTION_PLAY ="actionplay";
    public static final String ACTION_PREV ="actionprev";
    public static final String ACTION_NEXT ="actionnext";
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT>=26)
        {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID_1
                    ,"Channel(1)", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel 1 Desc");
            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID_2
                    ,"Channel(2)", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel 2 Desc");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
        }
    }
}
