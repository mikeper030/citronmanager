package com.ultimatesoftil.citron.models;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.ultimatesoftil.citron.R;
import com.ultimatesoftil.citron.ui.activities.MainActivity;

import java.util.Date;

import static android.support.v4.app.NotificationCompat.PRIORITY_MIN;

/**
 * Created by Mike on 14/08/2018.
 */

public class SmsSenderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String name=intent.getStringExtra("name");
        if(name!=null) {
            //send the sms
            Log.d("sms receiver start","");
            SmsService.enqueueWork(context, intent);

            //creating unique id for each specific notification
            int ID = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

            //notify admin for customer being late
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notification = new Intent(context, MainActivity.class);
            notification.putExtra("title", name);
            notification.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pi = PendingIntent.getActivity(context, ID, notification, PendingIntent.FLAG_UPDATE_CURRENT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder mynotification = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(name)
                    .setContentText("הלקוח מאחר נשלחה הודעה אוטומטית לתזכורת")
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .setSound(defaultSoundUri)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel("my_channel_01", "notice", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(mChannel);
                mynotification.setChannelId("my_channel_01");
            }
            notificationManager.notify(ID, mynotification.build());
            Log.d("random", String.valueOf(ID));
        }
    }
}