package com.example.stopwatch.Fragments;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.stopwatch.R;
import com.example.stopwatch.databinding.FragmentTimerBinding;

import java.util.Locale;

public class TimerFragment extends Fragment {

    private FragmentTimerBinding binding;
    private boolean isTimerRunning = false;
    private long timeSelected = 0;
    private long timeProgress = 0;
    private long pauseOffset = 0;
    private CountDownTimer countDownTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTimerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Set click listeners
        binding.addBtn.setOnClickListener(v -> setTimeFunction());
        binding.startBtn.setOnClickListener(v -> toggleTimer());
        binding.resetBtn.setOnClickListener(v -> resetTimer());
        binding.textsec.setOnClickListener(v -> addExtraTime());



        // Register BroadcastReceiver for timer tick
        binding.textsec.setVisibility(View.INVISIBLE);
        return view;
    }

    private void setTimeFunction() {
        Dialog timeDialog = new Dialog(requireContext());
        timeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        timeDialog.setContentView(R.layout.bottomsheetdialog);
        EditText timeSet = timeDialog.findViewById(R.id.getTime);
        ProgressBar progressBar = timeDialog.findViewById(R.id.pbTimer);
        timeDialog.findViewById(R.id.btnOk).setOnClickListener(v -> {
            String input = timeSet.getText().toString();
            if (input.isEmpty()) {
                Toast.makeText(requireContext(), "Enter Time Duration", Toast.LENGTH_SHORT).show();
            } else {
                int second = Integer.parseInt(input);
                if (second<=0){
                    Toast.makeText(requireContext(), "please add the positive number", Toast.LENGTH_SHORT).show();
                }else {
                    timeSelected = Long.parseLong(input);
                    updateTimerUI(timeSelected);
                    binding.pbTimer.setMax((int) timeSelected);

                    Log.d("TimerActivity", "timeSelected: " + timeSelected + ", ProgressBar max: " + binding.pbTimer.getMax());
                }
            }
            timeDialog.dismiss();
        });

        timeDialog.show();
        timeDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timeDialog.getWindow().getAttributes().windowAnimations = R.style.DilogAnimation;
        timeDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void toggleTimer() {
        if (!isTimerRunning) {
            startTimer();
        } else {
            pauseTimer();
        }
    }

    private void startTimer() {
        if (timeSelected>0){
            countDownTimer = new CountDownTimer((timeSelected - timeProgress) * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeProgress++;
                    long progress = timeSelected - timeProgress;
                    binding.pbTimer.setProgress((int) progress);
                    updateTimerUI(progress);
                }

                @Override
                public void onFinish() {
                    resetTimer();
                    Toast.makeText(requireContext(), "Timer Finished", Toast.LENGTH_SHORT).show();
                    binding.tvTimeLeft.setText("0");
                }
            }.start();
            isTimerRunning = true;
            binding.startBtn.setText("Pause");
            binding.addBtn.setVisibility(View.INVISIBLE);
            binding.textsec.setVisibility(View.VISIBLE);
        }else {
            Toast.makeText(requireContext(), "Select the time!!", Toast.LENGTH_SHORT).show();
        }

    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            pauseOffset = timeSelected - timeProgress;
            isTimerRunning = false;
            binding.startBtn.setText("Start");
        }
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timeSelected = 0;
        timeProgress = 0;
        pauseOffset = 0;
        isTimerRunning = false;
        binding.startBtn.setText("Start");
        updateTimerUI(timeSelected);
        binding.tvTimeLeft.setText("0");
        binding.pbTimer.setProgress(0);
        binding.addBtn.setVisibility(View.VISIBLE);
        binding.textsec.setVisibility(View.INVISIBLE);
    }

    private void updateTimerUI(long secondsRemaining) {
        long hours = secondsRemaining / 3600;
        long minutes = (secondsRemaining % 3600) / 60;
        long seconds = secondsRemaining % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        binding.tvTimeLeft.setText(timeFormatted);
    }

    private void addExtraTime(){

            if (countDownTimer != null) {
                countDownTimer.cancel();
                timeSelected += 15;
                timeProgress = 0;
                pauseOffset = 0;

                 startTimer();
                binding.pbTimer.setMax((int) timeSelected);

                Toast.makeText(requireContext(), "15 seconds added", Toast.LENGTH_SHORT).show();
            }

    }

    @Override
    public void onStop() {
        super.onStop();
        resetTimer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}