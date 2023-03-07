package com.ds.bot;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BotService extends Service {

    private static final String TAG = "BotService";
    public static final String CMD = "chatbot";
    private NotificationManager notificationManager;
    private NotificationDecorator notificationDecorator;
    public static final int GENERATE_MSG = 1;
    public static final int STOP_SERVICE = 2;
    public static final String KEY_USER_NAME = "Username";
    private String userName;
    private final String id = "51";
    private String presDate;


    public BotService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationDecorator = new NotificationDecorator(this, notificationManager);

        Date date = new Date();
        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
        presDate= DateFor.format(date);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(intent != null) {
            Bundle data = intent.getExtras();
            handleData(data);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopSelf();
        super.onDestroy();
    }

    private void handleData(Bundle data) {
        int command = data.getInt(CMD);
        userName = data.getString(KEY_USER_NAME);
        if (command == GENERATE_MSG) {
            generatingMessage();
        } else if (command == STOP_SERVICE) {
            stopMessage();
            stopSelf();
        } else {
            Log.e(TAG, "Invalid Command");
        }
    }

    public void generatingMessage(){
        String[] msg = {"Hello "+userName+"!","How are you?", "Good Bye "+userName+"!"};
        Intent intent1 = new Intent();
        intent1.setAction("com.ds.bot");
        intent1.putExtra("messages", msg);
        intent1.putExtra("stopping", "no");
        sendBroadcast(intent1);
        for(int i=0; i<3;i++){
            notificationDecorator.displayNotification("New Message", msg[i], String.format("Dated: %s", presDate), i);
        }
    }

    public void stopMessage(){
        String[] msg = new String[0];
        Intent intent1 = new Intent();
        intent1.setAction("com.ds.bot");
        intent1.putExtra("messages", msg);
        intent1.putExtra("stopping","yes");
        sendBroadcast(intent1);
        notificationDecorator.displayNotification("Service Stopped", "ChatBot Stopped: "+ id, presDate, 3);
    }
}