package com.example.cookiez;

import android.app.Application;

import com.example.cookiez.Utilities.SignalManager;

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        SignalManager.init(this);
    }
}
