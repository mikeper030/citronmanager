package com.ultimatesoftil.citron.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ultimatesoftil.citron.models.AlarmParcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import static com.ultimatesoftil.citron.util.Constants.FILE_EXTENSION;

/**
 * Created by Mike on 13/08/2018.
 */

public class Utils {
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(20);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
    public static String FormatMillis(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"
                , Locale.getDefault());
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(new Date(time));
    }
    public static String FormatCalendar(Calendar time) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"
                , Locale.getDefault());
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(time.getTime());
    }
    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
    public static int randomNum(){
        Random rand = new Random();
        return rand.nextInt(10000000);
    }
    public static boolean deleteNotification(AlarmParcel alarm, Context context) {

            File file = new File(context.getFilesDir(),String.valueOf(alarm.getTime()+FILE_EXTENSION) );
            if (!file.exists() || file.isDirectory()) {
                return false;
            }
            return file.delete();

    }
    public static void saveNotification(AlarmParcel alarm, Context context) {

        String filename = String.valueOf(alarm.getTime()) + FILE_EXTENSION;
        try {
            FileOutputStream fos = context.getApplicationContext().openFileOutput(filename, 0);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(alarm);
            oos.close();
            fos.close();
            Log.d("utils", filename);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public static ArrayList<AlarmParcel> getAllAlarms(Context context) {
       Exception e;
        ArrayList<AlarmParcel> items = new ArrayList();
        File filesdir = context.getApplicationContext().getFilesDir();
        ArrayList<String> itemsfiles = new ArrayList();
        for (String file : filesdir.list()) {

            if (file.endsWith(FILE_EXTENSION)) {
                itemsfiles.add(file);
            }
            Log.d("importsize",String.valueOf(itemsfiles.size()));
        }
        int i = 0;
        while (i < itemsfiles.size()) {
            try {
                FileInputStream fis = context.openFileInput((String) itemsfiles.get(i));
                ObjectInputStream ois = new ObjectInputStream(fis);
                items.add((AlarmParcel) ois.readObject());
                fis.close();
                ois.close();
                i++;
            } catch (IOException e2) {
                e = e2;
            } catch (ClassNotFoundException e3) {
                e = e3;
            }
        }
        for(int j=0;j<100;j++);
        Log.d("items fianl size",String.valueOf(items.size()));
        return items;
        //e.printStackTrace();
        // return null;
    }
   public static boolean isTabletDevice(Activity activity) {
       DisplayMetrics metrics = new DisplayMetrics();
       activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

       float yInches = metrics.heightPixels / metrics.ydpi;
       float xInches = metrics.widthPixels / metrics.xdpi;
       double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
       if (diagonalInches >= 6.5) {
           return true;
       }

           return false;

   }
}
