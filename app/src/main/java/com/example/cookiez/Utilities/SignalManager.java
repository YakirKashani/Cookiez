package com.example.cookiez.Utilities;

import android.content.Context;
import android.widget.Toast;

public class SignalManager {
    private static  SignalManager instance = null;
    private Context context;

    private SignalManager(Context context){
        this.context = context;
    }

    public static void init(Context context){
        synchronized (SignalManager.class){
            if(instance == null){
                instance = new SignalManager(context);
            }
        }
    }
    public static SignalManager getInstance(){
        return instance;
    }

    public void toast(String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
}
