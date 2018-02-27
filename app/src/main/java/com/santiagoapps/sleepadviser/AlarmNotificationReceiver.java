package com.santiagoapps.sleepadviser;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Ian on 1/20/2018.
 */
public class AlarmNotificationReceiver extends BroadcastReceiver {
    public final String TAG = "Dormie (" + AlarmNotificationReceiver.class.getSimpleName() + ")" ;

    @Override
    public void onReceive(Context context, Intent intent) {


        Log.d(TAG,"Broadcast recieved!");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.logo_dormie_happy)
                .setContentTitle("Dormie Reminder")
                .setContentText("How are you? It's sleeping time! - update")
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentInfo("Info");

        NotificationManager notificationManager =  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());

        //For alarm:
//        MediaPlayer mediaPlayer = MediaPlayer.create(context,
//                Settings.System.DEFAULT_RINGTONE_URI);
//        mediaPlayer.start();


    }
}
