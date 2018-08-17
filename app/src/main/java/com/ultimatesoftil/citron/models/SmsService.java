package com.ultimatesoftil.citron.models;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by Mike on 15/08/2018.
 */

public class SmsService extends JobIntentService {
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
       if(intent.getSerializableExtra("client")!=null){
           Client client=(Client) intent.getSerializableExtra("client");
           SmsManager sms = SmsManager.getDefault();
           sms.sendTextMessage(client.getPhone(), null, "ddddd", null, null);
       }else {
           Log.d("error","at sms sender receiver");
       }

    }
    public static void enqueueWork(Context context, Intent work){
        enqueueWork(context,SmsService.class,9,work);
    }
}
