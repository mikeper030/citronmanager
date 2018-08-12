package com.ultimatesoftil.citron.models;

import android.graphics.Bitmap;

import java.io.Serializable;

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

    public long getNotification() {
        return notification;
    }

    public void setNotification(long notification) {
        this.notification = notification;
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
}
