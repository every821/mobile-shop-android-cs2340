package com.example.shoppingwithfriends;

public class User {

    String username, password;
    String low, high;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getLow() {
        return low;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getHigh() {
        return high;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
