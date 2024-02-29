//package com.example.stopwatch.Service;
//
//import android.annotation.SuppressLint;
//import android.app.Dialog;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Build;
//import android.os.CountDownTimer;
//import android.os.IBinder;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.core.app.NotificationCompat;
//
//import com.example.stopwatch.R;
//import com.example.stopwatch.TimerActivity;
//import com.example.stopwatch.databinding.FragmentTimerBinding;
//
//import java.util.Locale;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class TimerService extends Service {
//
//    private CountDownTimer timer;
//    private long timeSelected;
//    private long timeProgress;
//    private long pauseOffset;
//    private boolean isStart;
//    private boolean isTimerRunning;
//    private FragmentTimerBinding binding;
//
//    // Notification
//    private NotificationManager notificationManager;
//    public static final String CHANNEL_ID = "Timer_Notifications";
//    private static final int NOTIFICATION_ID = 1;
//
//    // Service Actions
//    public static final String START = "START";
//    public static final String PAUSE = "PAUSE";
//    public static final String RESET = "RESET";
//    public static final String GET_STATUS = "GET_STATUS";
//
//    // Intent Extras
//    public static final String TIMER_ACTION = "TIMER_ACTION";
//    public static final String TIME_SELECTED = "TIME_SELECTED";
//    public static final String TIME_PROGRESS = "TIME_PROGRESS";
//    public static final String PAUSE_OFFSET = "PAUSE_OFFSET";
//    public static final String IS_TIMER_RUNNING = "IS_TIMER_RUNNING";
//
//    // Intent Actions
//    public static final String TIMER_TICK = "TIMER_TICK";
//    public static final String TIMER_STATUS = "TIMER_STATUS";
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent != null) {
//            String action = intent.getStringExtra(TIMER_ACTION);
//            if (action != null) {
//                switch (action) {
//                    case START:
//                        startTimer(intent.getLongExtra(TIME_SELECTED, 0),
//                                intent.getLongExtra(TIME_PROGRESS, 0),
//                                intent.getLongExtra(PAUSE_OFFSET, 0));
//                        break;
//                    case PAUSE:
//                        pauseTimer();
//                        break;
//                    case RESET:
//                        resetTimer();
//                        break;
//                    case GET_STATUS:
//                        sendStatus();
//                        break;
//                }
//            }
//        }
//        return START_STICKY;
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    private void startTimer(long selectedTime, long progress, long offset) {
//        timeSelected = selectedTime;
//        timeProgress = progress;
//        pauseOffset = offset;
//        isStart = true;
//        isTimerRunning = true;
//
//        timer = new CountDownTimer((timeSelected * 1000L) - (pauseOffset * 1000), 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                timeProgress++;
//                long totalSeconds = millisUntilFinished / 1000;
//                long progress = timeSelected - totalSeconds; // Calculate the progress
//
//                Intent intent = new Intent(TIMER_TICK);
//                intent.putExtra(TIME_SELECTED, timeSelected);
//                intent.putExtra(TIME_PROGRESS, progress);
//                sendBroadcast(intent);
//            }
//
//            @Override
//            public void onFinish() {
//                resetTimer();
//                Toast.makeText(TimerService.this, "Times Up!", Toast.LENGTH_SHORT).show();
//            }
//        }.start();
//
//        // Start foreground service
//        moveToForeground();
//    }
//
//    private void pauseTimer() {
//        if (timer != null) {
//            timer.cancel();
//            pauseOffset = timeSelected - timeProgress;
//            isTimerRunning = false;
//            sendStatus();
//            // Update notification
//            updateNotification();
//        }
//    }
//
//    private void resetTimer() {
//        if (timer != null) {
//            timer.cancel();
//            timeSelected = 0;
//            timeProgress = 0;
//            pauseOffset = 0;
//            isStart = true;
//            isTimerRunning = false;
//            sendStatus();
//            // Update notification
//            updateNotification();
//            stopForeground(true);
//            stopSelf();
//        }
//    }
//
//    private void sendStatus() {
//        Intent statusIntent = new Intent(TIMER_STATUS);
//        statusIntent.putExtra(IS_TIMER_RUNNING, isTimerRunning);
//        statusIntent.putExtra(TIME_SELECTED, timeSelected);
//        statusIntent.putExtra(TIME_PROGRESS, timeProgress);
//        statusIntent.putExtra(PAUSE_OFFSET, pauseOffset);
//        sendBroadcast(statusIntent);
//    }
//
//    private void createChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Timer Notifications",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            channel.enableLights(true);
//            channel.setLightColor(Color.BLUE);
//            channel.enableVibration(true);
//            channel.setDescription("Notifications for Timer");
//            getNotificationManager().createNotificationChannel(channel);
//        }
//    }
//
//    private NotificationManager getNotificationManager() {
//        if (notificationManager == null) {
//            notificationManager = getSystemService(NotificationManager.class);
//        }
//        return notificationManager;
//    }
//
//    private Notification buildNotification() {
//        String title = isTimerRunning ? "Timer is running!" : "Timer is paused!";
//        long totalSeconds = timeProgress;
//        long hours = totalSeconds / 3600;
//        long minutes = (totalSeconds % 3600) / 60;
//        long seconds = totalSeconds % 60;
//
//        Intent intent = new Intent(this, TimerActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        return new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle(title)
//                .setContentText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds))
//                .setSmallIcon(R.drawable.ic_timer)
//                .setContentIntent(pendingIntent)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setAutoCancel(true)
//                .build();
//    }
//
//    private void updateNotification() {
//        Notification notification = buildNotification();
//        getNotificationManager().notify(NOTIFICATION_ID, notification);
//    }
//
//    @SuppressLint({"ForegroundServiceType"})
//    private void moveToForeground() {
//        if (isTimerRunning) {
//            createChannel();
//            Notification notification = buildNotification();
//            startForeground(NOTIFICATION_ID, notification);
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (timer != null) {
//            timer.cancel();
//        }
//        stopForeground(true);
//    }
//}
