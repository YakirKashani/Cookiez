package com.example.cookiez.Model;

public class User {
    private String uid;
    private String UserName;

    public User(){

    }

    public String getUid() {
        return uid;
    }

    public User setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getUserName() {
        return UserName;
    }

    public User setUserName(String userName) {
        this.UserName = userName;
        return this;
    }
}
