package com.ultimatesoftil.citron.models;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.paracamera.Camera;
import com.ultimatesoftil.citron.R;

import com.ultimatesoftil.citron.models.AlarmParcel;
import com.ultimatesoftil.citron.models.Client;
import com.ultimatesoftil.citron.models.Order;
import com.ultimatesoftil.citron.models.Product;
import com.ultimatesoftil.citron.models.SmsSenderReceiver;
import com.ultimatesoftil.citron.ui.activities.MainActivity;
import com.ultimatesoftil.citron.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mike on 25/08/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context,Intent intent) {
        // /Get Firebase auth instance

        ArrayList<AlarmParcel> allNotifications= Utils.getAllAlarms(context);
        for(int j=0;j<allNotifications.size();j++){
            Log.d("boot",String.valueOf(allNotifications.get(j).getName()));

        }
        if(allNotifications!=null&&allNotifications.size()>0){
            Log.d("boot started",String.valueOf(allNotifications.size()));
            AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

            for(int i=0;i<allNotifications.size();i++){

                AlarmParcel parcel=allNotifications.get(i);
                Intent in = new Intent(context, SmsSenderReceiver.class);
                in.putExtra("name",parcel.getName());
                in.putExtra("phone",parcel.getPhone());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),parcel.getRequest(), in, 0);
                if(parcel.getTime()>System.currentTimeMillis()) {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, parcel.getTime(), pendingIntent);

                }
                 else{

                }
                  Log.d("boot","ddd");
            }

        }else {

        }
   }
}
