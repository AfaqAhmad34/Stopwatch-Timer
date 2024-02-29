package com.example.stopwatch.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stopwatch.R;
import com.example.stopwatch.Service.StopwatchService;
import com.example.stopwatch.databinding.FragmentStopWatchBinding;

import java.util.Locale;

public class StopWatchFragment extends Fragment {



    private FragmentStopWatchBinding binding;
    private BroadcastReceiver statusReceiver;
    private BroadcastReceiver timeReceiver;
    boolean  isStopwatchRunning;
    public StopWatchFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =FragmentStopWatchBinding.inflate(inflater,container,false);
        View view = binding.getRoot();


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
        return view;
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
        requireContext().registerReceiver(statusReceiver, statusFilter);

        IntentFilter timeFilter = new IntentFilter();
        timeFilter.addAction(StopwatchService.STOPWATCH_TICK);
        timeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long elapsedTimeMillis = intent.getLongExtra(StopwatchService.TIME_ELAPSED, 0);
                updateStopwatch(elapsedTimeMillis);
            }
        };
        requireContext().registerReceiver(timeReceiver, timeFilter);
    }

    private void updateStopwatch(long elapsedTimeMillis) {
        long milliseconds = elapsedTimeMillis % 1000;
        long totalSeconds = elapsedTimeMillis / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        binding.stopwatchValueTextView.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d:%03d", hours, minutes, seconds, milliseconds));
    }
    private void updateLayout(boolean isStopwatchRunning) {

        if (isStopwatchRunning) {
            binding.toggleButton.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_pause));
            binding.resetImageView.setVisibility(View.VISIBLE);
        } else {
            binding.toggleButton.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_play));
            binding.resetImageView.setVisibility(View.VISIBLE);
        }
    }

    private void getStopwatchStatus() {
        Intent stopwatchService = new Intent(requireContext(), StopwatchService.class);
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, StopwatchService.GET_STATUS);
        requireContext().startService(stopwatchService);
    }
    private void startStopwatch() {
        Intent stopwatchService = new Intent(requireContext(), StopwatchService.class);
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, StopwatchService.START);
        requireContext().startService(stopwatchService);
    }

    private void pauseStopwatch() {
        Intent stopwatchService = new Intent(requireContext(), StopwatchService.class);
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, StopwatchService.PAUSE);
        requireContext().startService(stopwatchService);
    }

    private void resetStopwatch() {
        Intent stopwatchService = new Intent(requireContext(), StopwatchService.class);
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, StopwatchService.RESET);
        requireContext().startService(stopwatchService);
    }

    private void moveToForeground() {
        Intent stopwatchService = new Intent(requireContext(), StopwatchService.class);
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, StopwatchService.MOVE_TO_FOREGROUND);
        requireContext().startService(stopwatchService);
    }

    private void moveToBackground() {
        Intent stopwatchService = new Intent(requireContext(), StopwatchService.class);
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, StopwatchService.MOVE_TO_BACKGROUND);
        requireContext().startService(stopwatchService);
    }

    @Override
    public void onStart() {
        super.onStart();
        moveToBackground();
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(statusReceiver);
        requireContext().unregisterReceiver(timeReceiver);
        moveToForeground();
    }
}



//
//package com.example.stopwatch.Fragments;
//
//import android.app.Dialog;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//
//import android.os.CountDownTimer;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.example.stopwatch.R;
//import com.example.stopwatch.databinding.FragmentTimerBinding;
//
//
//import java.util.Locale;
//
//public class TimerFragment extends Fragment {
//
//
//
//    private FragmentTimerBinding binding;
//
//
//    private int timeSelected = 0;
//    private CountDownTimer timeCountDown = null;
//    private int timeProgress = 0;
//    private long pauseOffset = 0;
//    private boolean isStart = true;
//    private boolean isTimerRunning = false;
//
//    public TimerFragment() {
//    }
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        binding = FragmentTimerBinding.inflate(inflater,container,false);
//        View view = binding.getRoot();
//
//        binding.addBtn.setOnClickListener(v -> setTimeFunction());
//
//        binding.startBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startTimerSetup();
//            }
//        });
//        binding.resetBtn.setOnClickListener(v -> resetTime());
//
//        binding.textsec.setOnClickListener(v -> addExtraTime());
//
//        binding.textsec.setVisibility(View.INVISIBLE);
//
//
//        return view;
//    }
//    private void setTimeFunction () {
//        Dialog timeDialog = new Dialog(requireContext());
//        timeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        timeDialog.setContentView(R.layout.bottomsheetdialog);
//        EditText timeSet = timeDialog.findViewById(R.id.getTime);
//
//        timeDialog.findViewById(R.id.btnOk).setOnClickListener(v -> {
//            String input = timeSet.getText().toString();
//            if (input.isEmpty()) {
//                Toast.makeText(requireContext(), "Enter Time Duration", Toast.LENGTH_SHORT).show();
//            } else {
//                int second = Integer.parseInt(input);
//                if (second<=0){
//                    Toast.makeText(requireContext(), "please add the positive number", Toast.LENGTH_SHORT).show();
//                }else {
//
//                    int seconds = Integer.parseInt(timeSet.getText().toString());
//                    int hours = seconds / 3600;
//                    int minutes = (seconds % 3600) / 60;
//                    seconds = seconds % 60;
//
//                    String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
//                    binding.tvTimeLeft.setText(timeFormatted);
////                    binding.tvTimeLeft.setText(timeSet.getText());
//
//                    binding.startBtn.setText("Start");
//                    timeSelected = Integer.parseInt(timeSet.getText().toString());
//
//                    binding.pbTimer.setMax(timeSelected);
//
//                    Log.d("TimerActivity", "timeSelected: " + timeSelected + ", ProgressBar max: " + binding.pbTimer.getMax());
//                }
//
//            }
//            timeDialog.dismiss();
//        });
//        timeDialog.show();
//        timeDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        timeDialog.getWindow().getAttributes().windowAnimations = R.style.DilogAnimation;
//        timeDialog.getWindow().setGravity(Gravity.BOTTOM);
//    }
//    private void startTimer ( final long[] pauseOffsetL){
//        timeCountDown = new CountDownTimer((timeSelected * 1000L) - (pauseOffsetL[0] * 1000), 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                timeProgress++;
//                long totalSeconds = millisUntilFinished / 1000;
//                long progress = timeSelected - totalSeconds; // Calculate the progress
//                binding.pbTimer.setProgress((int) progress);
//                long hours = totalSeconds / 3600;
//                long minutes = (totalSeconds % 3600) / 60;
//                long seconds = totalSeconds % 60;
//
//                String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
//                binding.tvTimeLeft.setText(timeFormatted);
//
////                    pauseOffsetL[0] = timeSelected - (millisUntilFinished / 1000);
////                    binding.pbTimer.setProgress((int) (timeSelected - timeProgress));
////
////                    binding.tvTimeLeft.setText(String.valueOf(timeSelected - timeProgress));
//            }
//
//            @Override
//            public void onFinish() {
//                resetTime();
//                binding.addBtn.setVisibility(View.VISIBLE);
//                Toast.makeText(requireContext(), "Times Up!", Toast.LENGTH_SHORT).show();
//            }
//        }.start();
//    }
//    private void startTimerSetup () {
//        if (timeSelected > timeProgress) {
//            if (isStart) {
//                startTimer(new long[]{pauseOffset});
//                isStart = false;
//                isTimerRunning = true;
//                binding.startBtn.setText("Pause");
//                binding.addBtn.setVisibility(View.INVISIBLE);
//            } else {
//                isStart = true;
//                isTimerRunning = false;
//                binding.startBtn.setText("Resume");
//                timePause();
//            }
//            binding.textsec.setVisibility(View.VISIBLE);
//        } else {
//            binding.textsec.setVisibility(View.INVISIBLE);
//            Toast.makeText(requireContext(), "Enter Time ", Toast.LENGTH_SHORT).show();
//        }
//    }
//
////    private void startTimerSetup () {
////        if (timeSelected > timeProgress) {
////            if (isStart) {
////                resumeTimer(new long[]{pauseOffset});
//////                startTimer(new long[]{pauseOffset});
//////                isStart = false;
//////                isTimerRunning = true;
//////                binding.startBtn.setText("Pause");
//////                binding.addBtn.setVisibility(View.INVISIBLE);
////
////            } else {
////                pauseTimer();
//////                isStart = true;
//////                isTimerRunning = false;
//////                binding.startBtn.setText("Resume");
//////                timePause();
//////                pauseOffset = timeSelected - timeProgress;
////            }
////            binding.textsec.setVisibility(View.VISIBLE);
////        } else {
////            binding.textsec.setVisibility(View.INVISIBLE);
////            Toast.makeText(requireContext(), "Enter TIme ", Toast.LENGTH_SHORT).show();
////        }
////    }
//
//    private void resetTime () {
//        if (timeCountDown != null) {
//            binding.textsec.setVisibility(View.INVISIBLE);
//            timeCountDown.cancel();
//            timeProgress = 0;
//            timeSelected = 0;
//            pauseOffset = 0;
//            timeCountDown = null;
//            binding.startBtn.setText("start");
//            isStart = true;
//            binding.pbTimer.setProgress(0);
//            binding.tvTimeLeft.setText("0");
//            binding.addBtn.setVisibility(View.VISIBLE);
//        }
//    }
//    private void pauseTimer() {
//        if (timeCountDown != null) {
//            timeCountDown.cancel();
//            isTimerRunning = false;
//
//            pauseOffset = timeSelected - timeProgress;
//            binding.startBtn.setText("Resume");
//        }
//    }
//
//    private void resumeTimer(long[] pauseOffsetL) {
//        startTimer(new long[]{pauseOffsetL[0]});
//        isStart = false;
//        isTimerRunning = true;
//        binding.startBtn.setText("Pause");
//        binding.addBtn.setVisibility(View.INVISIBLE);
//    }
//
//    private void addExtraTime() {
//        if (timeCountDown != null) {
//            timeCountDown.cancel();
//
//            timeSelected += 15;
//            timeProgress = 0;
//            pauseOffset = 0;
//
//            startTimer(new long[]{pauseOffset});
//            binding.pbTimer.setMax(timeSelected);
//            binding.textsec.setVisibility(View.VISIBLE);
//            Toast.makeText(requireContext(), "15 sec added", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (timeCountDown != null) {
//            timeCountDown.cancel();
//            timeProgress = 0;
//        }
//    }
//    private void timePause () {
//        if (timeCountDown != null) {
//            timeCountDown.cancel();
//        }
//    }
//