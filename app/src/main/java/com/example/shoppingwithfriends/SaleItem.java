package com.example.shoppingwithfriends;

import android.graphics.Bitmap;

@SuppressWarnings("ALL")
public class SaleItem {

    String username, item, location, price;
    Bitmap bmp;

    public SaleItem(String username, String item, String location, String price) {
        this.username = username;
        this.item = item;
        this.location = location;
        this.price = price;
    }

    public String getUsername() {
        return this.username;
    }
    public String getItem() {
        return this.item;
    }
    public String getLocation() {
        return this.location;
    }
    public String getPrice() {
        return this.price;
    }
    public void setBitmap(Bitmap bmp) {
        this.bmp = bmp;
    }
    public Bitmap getBitmap() {
        return this.bmp;
    }

}
