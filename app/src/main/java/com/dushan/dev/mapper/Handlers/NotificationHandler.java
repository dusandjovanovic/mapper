package com.dushan.dev.mapper.Handlers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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

    public void createSimpleNotification(Context context, String message) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Mapper")
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        mNotificationManager.notify(11, mBuilder.build());
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Mapper";
            String description = "Mapper_app";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            mNotificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(channel);
        }
        mNotificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
