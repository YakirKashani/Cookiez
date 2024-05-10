package com.example.cookiez.Utilities;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Looper;
import android.os.Handler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BackgroundSound {
    private Context context;
    private Executor executor;
    private Handler handler;
    private MediaPlayer mediaPlayer;
    private int resID;
    public BackgroundSound(Context context, int resID) {
        this.context = context;
        this.resID = resID;
        this.executor = Executors.newSingleThreadExecutor();
        this.handler = new Handler(Looper.getMainLooper());
    }
    public void playSound() {
        executor.execute(() -> {
            mediaPlayer = MediaPlayer.create(context, this.resID);
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(1.0f, 1.0f);
            mediaPlayer.start();
        });
    }
    public void stopSound() {
        if (mediaPlayer != null) {
            executor.execute(() -> {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            });
        }
    }
}
