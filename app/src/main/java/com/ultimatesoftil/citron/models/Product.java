package com.ultimatesoftil.citron.models;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Mike on 06/08/2018.
 */

public class Product implements Serializable{
    public Product() {
    }

    public double getPrice() {

        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPicLink() {
        return picLink;
    }

    public void setPicLink(String picLink) {
        this.picLink = picLink;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getDue() {
        return due;
    }

    public void setDue(double due) {
        this.due = due;
    }
    long notification;

    public  long getNotification() {
        return notification;
    }

    public void setNotification(long notification) {
        this.notification = notification;

    }

    private PendingIntent intent;

    public PendingIntent getIntent() {
        return intent;
    }

    public void setIntent(PendingIntent intent) {
        this.intent = intent;
    }

    double price;
    String picLink;
    int status;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Bitmap getRawImage() {
        return rawImage;
    }

    public void setRawImage(Bitmap rawImage) {
        this.rawImage = rawImage;
    }

    Bitmap rawImage;
    String kind;
    double due;

    public boolean isNotification_checked() {
        return notification_checked;
    }

    public void setNotification_checked(boolean notification_checked) {
        this.notification_checked = notification_checked;
    }

    boolean notification_checked=true;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    long time;
}
