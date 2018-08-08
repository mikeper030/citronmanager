package com.ultimatesoftil.citron.models;

/**
 * Created by Mike on 06/08/2018.
 */

public class Product {
    public Product() {
    }

    public String getPrice() {

        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPicLink() {
        return picLink;
    }

    public void setPicLink(String picLink) {
        this.picLink = picLink;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    String price;
    String picLink;
    String status;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    String kind;
    double due;
}
