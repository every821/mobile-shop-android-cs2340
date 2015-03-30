package com.example.shoppingwithfriends;

@SuppressWarnings("ALL")
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

// --Commented out by Inspection START (3/29/2015 8:14 PM):
//    public String getUsername() {
//        return username;
//    }
// --Commented out by Inspection STOP (3/29/2015 8:14 PM)

// --Commented out by Inspection START (3/29/2015 8:14 PM):
//    public String getPassword() {
//        return password;
//    }
// --Commented out by Inspection STOP (3/29/2015 8:14 PM)

}
