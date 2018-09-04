package com.dushan.dev.mapper.Handlers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

public class NotificationHandler {
    private static NotificationHandler nHandler;
    private static NotificationManager mNotificationManager;

    private NotificationHandler () {}

    public static  NotificationHandler getInstance(Context context) {
        if(nHandler == null) {
            nHandler = new NotificationHandler();
            mNotificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return nHandler;
    }

    public void createSimpleNotification(Context context, String message) {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        PendingIntent resultPending = stackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("mapper")
                .setContentText(message)
                .setContentIntent(resultPending);

        mNotificationManager.notify(10, mBuilder.build());
    }
}
