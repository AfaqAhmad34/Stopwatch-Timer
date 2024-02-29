package com.example.stopwatch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.stopwatch.Service.StopwatchService;
import com.example.stopwatch.databinding.ActivityMainBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;
    private BroadcastReceiver statusReceiver;
    private BroadcastReceiver timeReceiver;
    boolean  isStopwatchRunning;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        });
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
        } else {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }


        binding.btnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,TimerActivity.class));
            }
        });

        binding.toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStopwatchRunning) {
                    pauseStopwatch();
                } else {
                    startStopwatch();
                }
            }
        });

        binding.resetImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetStopwatch();
            }
        });



        Toast.makeText(this, "onCreate Call ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        getStopwatchStatus();

        IntentFilter statusFilter = new IntentFilter();
        statusFilter.addAction(StopwatchService.STOPWATCH_STATUS);
        statusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isRunning = intent.getBooleanExtra(StopwatchService.IS_STOPWATCH_RUNNING, false);
                isStopwatchRunning = isRunning;
               // int timeElapsed = intent.getIntExtra(StopwatchService.TIME_ELAPSED, 0);
                long elapsedTimeMillis = intent.getLongExtra(StopwatchService.TIME_ELAPSED, 0);
                updateLayout(isStopwatchRunning);
                updateStopwatch(elapsedTimeMillis);
            }
        };
        registerReceiver(statusReceiver, statusFilter);

        IntentFilter timeFilter = new IntentFilter();
        timeFilter.addAction(StopwatchService.STOPWATCH_TICK);
        timeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long elapsedTimeMillis = intent.getLongExtra(StopwatchService.TIME_ELAPSED, 0);
                updateStopwatch(elapsedTimeMillis);
            }
        };
        registerReceiver(timeReceiver, timeFilter);
    }
    private void updateStopwatch(long elapsedTimeMillis) {
        long milliseconds = elapsedTimeMillis % 1000;
        long totalSeconds = elapsedTimeMillis / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        binding.stopwatchValueTextView.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d:%03d", hours, minutes, seconds, milliseconds));
    }
//    private void updateStopwatchValue(int timeElapsed) {
//
//        int hours = (timeElapsed / 60) / 60;
//        int minutes = timeElapsed / 60;
//        int seconds = timeElapsed % 60;
//        int milliseconds = (int) (timeElapsed % 36000);
//        Log.d("milliSecond", "" + milliseconds);
//        binding.stopwatchValueTextView.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d:%02d", hours, minutes, seconds,minutes));
//    }

    private void updateLayout(boolean isStopwatchRunning) {

        if (isStopwatchRunning) {
            binding.toggleButton.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_pause));
            binding.resetImageView.setVisibility(View.VISIBLE);
        } else {
            binding.toggleButton.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_play));
            binding.resetImageView.setVisibility(View.VISIBLE);
        }
    }

    private void getStopwatchStatus() {
        Intent stopwatchService = new Intent(this, StopwatchService.class);
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, StopwatchService.GET_STATUS);
        startService(stopwatchService);
    }

    private void startStopwatch() {
        Intent stopwatchService = new Intent(this, StopwatchService.class);
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, StopwatchService.START);
        startService(stopwatchService);
    }

    private void pauseStopwatch() {
        Intent stopwatchService = new Intent(this, StopwatchService.class);
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, StopwatchService.PAUSE);
        startService(stopwatchService);
    }

    private void resetStopwatch() {
        Intent stopwatchService = new Intent(this, StopwatchService.class);
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, StopwatchService.RESET);
        startService(stopwatchService);
    }

    private void moveToForeground() {
        Intent stopwatchService = new Intent(this, StopwatchService.class);
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, StopwatchService.MOVE_TO_FOREGROUND);
        startService(stopwatchService);
    }

    private void moveToBackground() {
        Intent stopwatchService = new Intent(this, StopwatchService.class);
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, StopwatchService.MOVE_TO_BACKGROUND);
        startService(stopwatchService);
    }

    @Override
    protected void onStart() {
        super.onStart();
        moveToBackground();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(statusReceiver);
        unregisterReceiver(timeReceiver);
        moveToForeground();
    }
}