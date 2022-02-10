package com.luckynum.data;

import android.app.Notification;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.luckynum.MainActivity;

public class NotificationListener extends NotificationListenerService {

    @Override
    public IBinder onBind(Intent intent) {

        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        //Log.i("hey notif get"," : "+ sbn.getPackageName() + " TITLE " + sbn.getNotification().extras.get(Notification.EXTRA_TITLE) + " TEXT " + sbn.getNotification().extras.get(Notification.EXTRA_TEXT));

        String appPckName = sbn.getPackageName();
        String notifTitle = sbn.getNotification().extras.get(Notification.EXTRA_TITLE).toString();
        String notifText = sbn.getNotification().extras.get(Notification.EXTRA_TEXT).toString();
        Intent intent = new Intent(this, MainActivity.MyBroadcastReceiver.class);
        intent.setAction("com.lucknum.data.GETNOTIF");
        intent.putExtra("appPckName", appPckName);
        intent.putExtra("notifTitle",notifTitle);
        intent.putExtra("notifText",notifText);

        sendBroadcast(intent);


    }


}
