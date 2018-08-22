package com.ultimatesoftil.citron.models;

import android.app.PendingIntent;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Mike on 01/08/2018.
 */

public class Order implements Serializable{
    //all products must be same per order
    ArrayList<Product> products;
    String comment;
    double due;
    int paymenttype;
    String img1;
    String img2;
    long updated=0;





    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {

        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    String img3;

    public double getDue() {
        return due;
    }

    public void setDue(double due) {
        this.due = due;
    }

    public int getPaymenttype() {
        return paymenttype;
    }

    public void setPaymenttype(int paymenttype) {
        this.paymenttype = paymenttype;
    }

    double total;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    int quantity;
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    long time;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

//    @Exclude
//    public String getDateTimeFormatted() {
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"
//                , Locale.getDefault());
//        formatter.setTimeZone(TimeZone.getDefault());
//        return formatter.format(new Date(time));
  //  }





}
