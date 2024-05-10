package com.example.cookiez;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cookiez.Utilities.BackgroundSound;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CookingRecipeActivity extends AppCompatActivity {

    private ArrayList<String> Steps;
    private MaterialTextView CookingRecipe_MTV_CookingStep;
    private MaterialTextView CookingRecipe_MTV_Timer;
    private FloatingActionButton CookingRecipe_FAB_Stop;
    private FloatingActionButton CookingRecipe_FAB_Play;
    private MaterialButton CookingRecipe_MB_Next;
    int i;
    private BackgroundSound backgroundSound;
    private static final long DELAY = 1000;
    private long startTime;
    private boolean timerOn = false;
    private Timer timer;
    private Pattern currentPattern;
    private String[] patterns = {
            "wait \\d+ hours and \\d+ minutes and \\d+ seconds",
            "wait \\d+ hours and \\d+ minutes",
            "wait \\d+ hours and \\d+ seconds",
            "wait \\d+ minutes and \\d+ seconds",
            "wait \\d+ seconds",
            "wait \\d+ minutes",
            "wait \\d+ hours",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cooking_recipe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent previousIntent = getIntent();
        Steps = previousIntent.getStringArrayListExtra("StepsListKey");
        findViews();
        initViews();
    }

    private void findViews() {
        CookingRecipe_MTV_CookingStep = findViewById(R.id.CookingRecipe_MTV_CookingStep);
        CookingRecipe_MTV_Timer = findViewById(R.id.CookingRecipe_MTV_Timer);
        CookingRecipe_FAB_Stop = findViewById(R.id.CookingRecipe_FAB_Stop);
        CookingRecipe_FAB_Play = findViewById(R.id.CookingRecipe_FAB_Play);
        CookingRecipe_MB_Next = findViewById(R.id.CookingRecipe_MB_Next);

    }

    private void initViews() {
        i = -1;
        CookingRecipe_MTV_CookingStep.setText("Press start when you ready");
        CookingRecipe_MB_Next.setText("Start");
        CookingRecipe_MTV_Timer.setVisibility(View.INVISIBLE);
        CookingRecipe_FAB_Stop.setVisibility(View.INVISIBLE);
        CookingRecipe_FAB_Play.setVisibility(View.INVISIBLE);
        CookingRecipe_MB_Next.setOnClickListener(v -> {
            if(timerOn)
                stopTimer(false);// In case the timer is running and didn't finish his runtime

            if(backgroundSound!=null)
                backgroundSound.stopSound();
            CookingRecipe_MB_Next.setText("next");
            i++;
            if (i >= Steps.size())
                finish();
            else {
                CookingRecipe_MTV_CookingStep.setText(Steps.get(i));
                boolean isMatched = false;
                for (String pattern : patterns) {
                    Pattern p = Pattern.compile(pattern);
                    Matcher m = p.matcher(Steps.get(i));
                    if (m.find()) {
                        isMatched = true;
                        currentPattern = p;
                        break;
                    }
                }
                if (isMatched) {
                    CookingRecipe_MTV_Timer.setVisibility(View.VISIBLE);
                    CookingRecipe_FAB_Stop.setVisibility(View.VISIBLE);
                    CookingRecipe_FAB_Play.setVisibility(View.VISIBLE);
                    CookingRecipe_FAB_Play.setOnClickListener(z -> {
                        Matcher m2 = currentPattern.matcher(Steps.get(i));
                        if (m2.find()) {
                            String matchPatter = m2.group();
                            int seconds = extractSeconds(matchPatter);
                            startTimer(seconds);
                        }
                    });
                    CookingRecipe_FAB_Stop.setOnClickListener(q -> stopTimer(false));

                } else {
                    CookingRecipe_MTV_Timer.setVisibility(View.INVISIBLE);
                    CookingRecipe_FAB_Stop.setVisibility(View.INVISIBLE);
                    CookingRecipe_FAB_Play.setVisibility(View.INVISIBLE);
                }
            }

        });
    }

    private void UpdateTimerUI(long remainingTimeMillis) {
        int seconds = (int) (remainingTimeMillis / 1000);
        int minutes = seconds / 60;
        seconds %= 60;
        int hours = minutes / 60;
        minutes %= 60;
        hours %= 24;
        CookingRecipe_MTV_Timer.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    private void startTimer(int seconds) {
        if (!timerOn) {
            timerOn = true;
            long currentTime = System.currentTimeMillis();
            startTime = currentTime;
            long endTime = currentTime + (seconds * 1000);
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    long remainingTime = endTime - System.currentTimeMillis();
                    if (remainingTime <= 0) {
                        stopTimer(true);
                        return;
                    }
                    runOnUiThread(() -> UpdateTimerUI(remainingTime));
                }
            }, 0, DELAY);
        }
    }

    private void stopTimer(boolean playSound) {
        timerOn = false;
        timer.cancel();
        if(playSound) {
            backgroundSound = new BackgroundSound(this, R.raw.succsess);
            backgroundSound.playSound();
        }
    }

    private int extractSeconds(String pattern) {
        int seconds = 0;
        Pattern p = Pattern.compile(
                "wait (\\d+) hours and (\\d+) minutes and (\\d+) seconds|" +
                "wait (\\d+) hours and (\\d+) minutes|" +
                "wait (\\d+) hours and (\\d+) seconds|" +
                "wait (\\d+) minutes and (\\d+) seconds|"+
                        "wait (\\d+) seconds|" +
                        "wait (\\d+) minutes|" +
                        "wait (\\d+) hours|");
        Matcher m = p.matcher(pattern);
        if (m.find()) {
            if (m.group(1) != null && m.group(2) != null && m.group(3) != null) {
                seconds += Integer.parseInt(m.group(1)) * 60 * 60;  // hours
                seconds += Integer.parseInt(m.group(2)) * 60;       // minutes
                seconds += Integer.parseInt(m.group(3));            // seconds
            } else if (m.group(4) != null && m.group(5) != null) {
                seconds += Integer.parseInt(m.group(4)) * 60 * 60;  // hours
                seconds += Integer.parseInt(m.group(5)) * 60;       // minutes
            } else if (m.group(6) != null && m.group(7) != null) {
                seconds += Integer.parseInt(m.group(6)) * 60 * 60;  // hours
                seconds += Integer.parseInt(m.group(7));            // seconds
            } else if (m.group(8) != null && m.group(9) != null) {
                seconds += Integer.parseInt(m.group(8)) * 60;       // minutes
                seconds += Integer.parseInt(m.group(9));            // seconds
            } else if (m.group(10) != null) {
                seconds += Integer.parseInt(m.group(10));           // seconds
            } else if (m.group(11) != null) {
                seconds += Integer.parseInt(m.group(11)) * 60;      // minutes
            } else if (m.group(12) != null) {
                seconds += Integer.parseInt(m.group(12)) * 60 * 60; // hours
            }

        }
        return seconds;
    }
}