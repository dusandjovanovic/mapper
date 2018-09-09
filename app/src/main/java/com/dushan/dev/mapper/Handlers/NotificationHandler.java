package com.dushan.dev.mapper.Handlers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.dushan.dev.mapper.Activities.HomeActivity;
import com.dushan.dev.mapper.R;

public class NotificationHandler {
    private static NotificationHandler nHandler;
    private static NotificationManager mNotificationManager;
    private static String CHANNEL_ID = "MAPPER";

    private NotificationHandler () {}

    public static  NotificationHandler getInstance(Context context) {
        if(nHandler == null) {
            nHandler = new NotificationHandler();
            createNotificationChannel(context);
        }
        return nHandler;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createSimpleNotification(Context context, String message) {
        int notificationId = 11;
        StatusBarNotification[] notifications = mNotificationManager.getActiveNotifications();
        for (StatusBarNotification notification : notifications)
            if (notification.getId() == notificationId)
                return;

        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Uri audio = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] vibrationRatio = {500,1000};

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Mapper")
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setVibrate(vibrationRatio)
                .setSound(audio)
                .setPriority(Notification.PRIORITY_MAX)
                .setChannelId(CHANNEL_ID);

        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "mapper";
            String description = "mapper_app";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            mNotificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(channel);
        }
        else
            mNotificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
