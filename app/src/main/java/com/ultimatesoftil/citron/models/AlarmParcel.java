package com.ultimatesoftil.citron.models;

import android.app.PendingIntent;

import java.io.Serializable;

/**
 * Created by Mike on 28/08/2018.
 */

public class AlarmParcel implements Serializable{
long time;
String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    String phone;

    public int getRequest() {
        return request;
    }

    public void setRequest(int request) {
        this.request = request;
    }

    int request;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


}
