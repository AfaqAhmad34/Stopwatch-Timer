package com.example.stopwatch.Fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.example.stopwatch.R;
import java.time.LocalDateTime;

public class ClockFragment extends Fragment {

    private final Handler handler = new Handler();
    FrameLayout analogClock;
    ImageView ivHour, ivMinute, ivSecond;
    LocalDateTime dateTime;
    int hh, mm, ss;
    int update;
    public ClockFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_clock, container, false);

        analogClock = view.findViewById(R.id.analogClock);
        ivHour = view.findViewById(R.id.ivHour);
        ivMinute = view.findViewById(R.id.ivMinute);
        ivSecond = view.findViewById(R.id.ivSecond);

        startClockUpdate();

        return view;
    }

    private void startClockUpdate() {
        handler.postDelayed(updateClickRunnable, 0);
    }


    //  Runnable Method for the Clock
    private final Runnable updateClickRunnable = new Runnable() {
        @Override
        public void run() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dateTime = LocalDateTime.now();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                hh = dateTime.getHour();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mm = dateTime.getMinute();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ss = dateTime.getSecond();
            }

            ivHour.setRotation((hh * 30) + (mm * 0.5F) + (ss * 0.0083333333333333F));
            ivMinute.setRotation((mm * 6) + (ss *  0.1F));
            ivSecond.setRotation(ss * 6);

            handler.postDelayed(this, update);
        }
    };
}