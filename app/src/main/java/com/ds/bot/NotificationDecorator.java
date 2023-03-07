package com.ds.bot;

import static java.lang.System.currentTimeMillis;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.ds.bot.MainActivity;

public class NotificationDecorator {

    private static final String TAG = "NotificationDecorator";
    private final Context context;
    public static final String CHANNEL_ID = "Channel_Id";
    private final NotificationManager notificationMgr;


    public NotificationDecorator(Context context, NotificationManager notificationManager) {
        this.context = context;
        this.notificationMgr = notificationManager;
        createChannel(notificationManager, CHANNEL_ID);
    }

    private void createChannel(NotificationManager notificationManager, String channelId){
        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(channelId, "BotChat Channel",
                    NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }

    public void displayNotification(String title, String contentText, String date, Integer notificationId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_IMMUTABLE);
        // notification message
        try {

            RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
            notificationLayout.setTextViewText(R.id.heading, title);
            notificationLayout.setTextViewText(R.id.description, contentText);
            notificationLayout.setTextViewText(R.id.date, date);

            Notification notify = new NotificationCompat.Builder(context)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle(title)
                    .setContentText(contentText)
                    .setContentIntent(contentIntent)
                    .setCustomBigContentView(notificationLayout)
                    .setWhen(currentTimeMillis())
                    .setChannelId(CHANNEL_ID)
                    .build();

            notify.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationMgr.notify(notificationId, notify);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}


