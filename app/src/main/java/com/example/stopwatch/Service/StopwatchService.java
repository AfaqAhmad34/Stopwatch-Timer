package com.example.stopwatch.Service;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.stopwatch.Constants;
import com.example.stopwatch.NavigationActivity;
import com.example.stopwatch.R;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class StopwatchService extends Service {

    Timer stopwatchTimer,updateTimer;
    int timeElapsed;
    boolean isStopWatchRunning;
    private long totalElapsedTimeMillis = 0;
    private long lastTickTimeMillis = 0;
    NotificationManager notificationManager;
    public static final String CHANNEL_ID = "Stopwatch_Notifications";

    // Service Actions
    public static final String START = "START";
    public static final String PAUSE = "PAUSE";
    public static final String RESET = "RESET";
    public static final String GET_STATUS = "GET_STATUS";
    public static final String MOVE_TO_FOREGROUND = "MOVE_TO_FOREGROUND";
    public static final String MOVE_TO_BACKGROUND = "MOVE_TO_BACKGROUND";

    // Intent Extras
    public static final String STOPWATCH_ACTION = "STOPWATCH_ACTION";
    public static final String TIME_ELAPSED = "TIME_ELAPSED";
    public static final String TIME_MILLI = "TIME_MILLI";
    public static final String IS_STOPWATCH_RUNNING = "IS_STOPWATCH_RUNNING";

    // Intent Actions
    public static final String STOPWATCH_TICK = "STOPWATCH_TICK";
    public static final String STOPWATCH_STATUS = "STOPWATCH_STATUS";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent !=null) {
            createChannel();
            getNotificationManager();

            String action = intent.getStringExtra(Constants.STOPWATCH_ACTION);

            Log.d("Stopwatch", "onStartCommand Action: " + action);

            if (action != null) {
                switch (action) {
                    case START:
                        startStopwatch();
                        break;
                    case PAUSE:
                        pauseStopwatch();
                        break;
                    case RESET:
                        resetStopwatch();
                        break;
                    case GET_STATUS:
                        sendStatus();
                        break;
                    case MOVE_TO_FOREGROUND:
                        moveToForeground();
                        break;
                    case MOVE_TO_BACKGROUND:
                        moveToBackground();
                        break;
                }
            }
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Stopwatch",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationChannel.setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI, null);
            notificationChannel.setShowBadge(true);
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void getNotificationManager() {
        notificationManager = getSystemService(NotificationManager.class);
    }
    private void sendStatus() {
        Intent statusIntent = new Intent();
        statusIntent.setAction(STOPWATCH_STATUS);
        statusIntent.putExtra(IS_STOPWATCH_RUNNING, isStopWatchRunning);
        statusIntent.putExtra(TIME_ELAPSED, totalElapsedTimeMillis);
        sendBroadcast(statusIntent);
    }
private void startStopwatch() {
    isStopWatchRunning = true;
    sendStatus();
    stopwatchTimer = new Timer();

    lastTickTimeMillis = System.currentTimeMillis();

    stopwatchTimer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
            long currentTimeMillis = System.currentTimeMillis();
            long elapsedTimeSinceLastTick = currentTimeMillis - lastTickTimeMillis;

            totalElapsedTimeMillis += elapsedTimeSinceLastTick;
            lastTickTimeMillis = currentTimeMillis;
            Intent stopwatchIntent = new Intent();
            stopwatchIntent.setAction(STOPWATCH_TICK);
            stopwatchIntent.putExtra(TIME_ELAPSED, totalElapsedTimeMillis);
            sendBroadcast(stopwatchIntent);
        }
    }, 0, 10);
}

    //timeElapsed++;
//                Intent stopwatchIntent = new Intent();
//                stopwatchIntent.setAction(STOPWATCH_TICK);
//                stopwatchIntent.putExtra(TIME_ELAPSED, timeElapsed);
//                sendBroadcast(stopwatchIntent);
//private void startStopwatch() {
//    isStopWatchRunning = true;
//    sendStatus();
//
//    startTime = System.currentTimeMillis(); // Record the start time
//
//    stopwatchTimer = new Timer();
//    stopwatchTimer.scheduleAtFixedRate(new TimerTask() {
//        @Override
//        public void run() {
//            long currentTime = System.currentTimeMillis();
//            long elapsedTime = currentTime - startTime - pausedTime; // Calculate elapsed time
//            int seconds = (int) (elapsedTime / 1000);
//            int milliseconds = (int) (elapsedTime % 1000);
//
//            Intent stopwatchIntent = new Intent();
//            stopwatchIntent.setAction(STOPWATCH_TICK);
//            stopwatchIntent.putExtra(TIME_ELAPSED, seconds);
//            stopwatchIntent.putExtra(TIME_MILLI, milliseconds);
//            sendBroadcast(stopwatchIntent);
//        }
//    }, 0, 10);
//}

    private void pauseStopwatch() {
        if (stopwatchTimer !=null){
            stopwatchTimer.cancel();
        }
        isStopWatchRunning = false;
        sendStatus();

    }
    private void resetStopwatch() {
        pauseStopwatch();
        totalElapsedTimeMillis  = 0L;
        lastTickTimeMillis = System.currentTimeMillis();
        sendStatus();
    }

    private Notification buildNotification() {
        String title = isStopWatchRunning ? "Stopwatch is running!" : "Stopwatch is paused!";
        long totalSeconds = totalElapsedTimeMillis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        long milliseconds = totalElapsedTimeMillis % 1000;
//        int hours = timeElapsed / 3600;
//        int minutes = (timeElapsed % 3600) / 60;
//        int seconds = timeElapsed % 60;


        Intent intent = new Intent(this, NavigationActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setOngoing(true)
                .setContentText(String.format("%02d:%02d", minutes, seconds))
                .setColorized(true)
                .setColor(Color.parseColor("#BEAEE2"))
                .setSmallIcon(R.drawable.ic_clock)
                .setOnlyAlertOnce(true)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();
    }

    private void updateNotification() {
        notificationManager.notify(1, buildNotification());
    }
    @SuppressLint("ForegroundServiceType")
    private void moveToForeground() {
        if (isStopWatchRunning) {
            startForeground(1, buildNotification());
            updateTimer = new Timer();

            updateTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updateNotification();
                }
            }, 0, 1000);
        }
    }

    private void moveToBackground() {
        if (updateTimer !=null) {
            updateTimer.cancel();
            stopForeground(true);
        }
    }
}



