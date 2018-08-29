package com.ultimatesoftil.citron.models;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.telephony.SmsManager;
import android.util.Log;

import com.ultimatesoftil.citron.R;

/**
 * Created by Mike on 15/08/2018.
 */

public class SmsService extends JobIntentService {
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
       String name=intent.getStringExtra("name");
       String phone =intent.getStringExtra("phone");
        if(name!=null&&phone!=null){

           SmsManager sms = SmsManager.getDefault();

           String content= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("alertcon", "null");
            if(content.equals("null")){
                content=getResources().getString(R.string.late);
            }


           Log.d("message","sent");
           Log.d("message",phone);
           Log.d("message",content);
            sms.sendTextMessage(phone, null, content, null, null);
       }else {
           Log.d("error","at sms sender receiver");
       }

    }
    public static void enqueueWork(Context context, Intent work){
        enqueueWork(context,SmsService.class,9,work);
    }
}
