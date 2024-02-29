package com.example.stopwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stopwatch.databinding.ActivityTimeerBinding;

import java.util.Locale;

public class TimerActivity extends AppCompatActivity {


    ActivityTimeerBinding binding;



    private int timeSelected = 0;
    private CountDownTimer timeCountDown = null;
    private int timeProgress = 0;
    private long pauseOffset = 0;
    private boolean isStart = true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimeerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addBtn.setOnClickListener(v -> setTimeFunction());

     binding.startBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             startTimerSetup();
         }
     });
        binding.resetBtn.setOnClickListener(v -> resetTime());

        binding.textsec.setOnClickListener(v -> addExtraTime());

        binding.textsec.setVisibility(View.INVISIBLE);
    }

        private void timePause () {
            if (timeCountDown != null) {
                timeCountDown.cancel();
            }
        }
        private void setTimeFunction () {
            Dialog timeDialog = new Dialog(this);
            timeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            timeDialog.setContentView(R.layout.bottomsheetdialog);
            EditText timeSet = timeDialog.findViewById(R.id.getTime);


            timeDialog.findViewById(R.id.btnOk).setOnClickListener(v -> {

                String input = timeSet.getText().toString();
                if (input.isEmpty()) {
                    Toast.makeText(this, "Enter Time Duration", Toast.LENGTH_SHORT).show();
                } else {
                    int second = Integer.parseInt(input);
                    if (second<=0){
                        Toast.makeText(this, "please add the positive number", Toast.LENGTH_SHORT).show();
                    }else {

                        int seconds = Integer.parseInt(timeSet.getText().toString());
                        int hours = seconds / 3600;
                        int minutes = (seconds % 3600) / 60;
                        seconds = seconds % 60;

                        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                        binding.tvTimeLeft.setText(timeFormatted);
//                    binding.tvTimeLeft.setText(timeSet.getText());

                        binding.startBtn.setText("Start");
                        timeSelected = Integer.parseInt(timeSet.getText().toString());

                        binding.pbTimer.setMax(timeSelected);

                        Log.d("TimerActivity", "timeSelected: " + timeSelected + ", ProgressBar max: " + binding.pbTimer.getMax());
                    }

                }
                timeDialog.dismiss();
            });
            timeDialog.show();
            timeDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timeDialog.getWindow().getAttributes().windowAnimations = R.style.DilogAnimation;
            timeDialog.getWindow().setGravity(Gravity.BOTTOM);
        }
        private void startTimer ( final long[] pauseOffsetL){
            timeCountDown = new CountDownTimer((timeSelected * 1000L) - (pauseOffsetL[0] * 1000), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeProgress++;
                    long totalSeconds = millisUntilFinished / 1000;
                    long progress = timeSelected - totalSeconds; // Calculate the progress
                    binding.pbTimer.setProgress((int) progress);
                    long hours = totalSeconds / 3600;
                    long minutes = (totalSeconds % 3600) / 60;
                    long seconds = totalSeconds % 60;

                    String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                    binding.tvTimeLeft.setText(timeFormatted);

//                    pauseOffsetL[0] = timeSelected - (millisUntilFinished / 1000);
//                    binding.pbTimer.setProgress((int) (timeSelected - timeProgress));
//
//                    binding.tvTimeLeft.setText(String.valueOf(timeSelected - timeProgress));
                }

                @Override
                public void onFinish() {
                    resetTime();
                    binding.addBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(TimerActivity.this, "Times Up!", Toast.LENGTH_SHORT).show();
                }
            }.start();
        }

        private void startTimerSetup () {
            if (timeSelected > timeProgress) {
                if (isStart) {
                    binding.startBtn.setText("Pause");
                    binding.addBtn.setVisibility(View.INVISIBLE);
                    startTimer(new long[]{pauseOffset});
                    isStart = false;
                } else {
                    isStart = true;
                    binding.startBtn.setText("Resume");
                    timePause();
                }
                binding.textsec.setVisibility(View.VISIBLE);
            } else {
                binding.textsec.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Enter TIme ", Toast.LENGTH_SHORT).show();
            }
        }

        private void resetTime () {
            if (timeCountDown != null) {
                binding.textsec.setVisibility(View.INVISIBLE);
                timeCountDown.cancel();
                timeProgress = 0;
                timeSelected = 0;
                pauseOffset = 0;
                timeCountDown = null;
                binding.startBtn.setText("start");
                isStart = true;
                binding.pbTimer.setProgress(0);
                binding.tvTimeLeft.setText("0");
                binding.addBtn.setVisibility(View.VISIBLE);
            }
        }


        private void addExtraTime() {
    if (timeCountDown != null) {
        timeCountDown.cancel();

        timeSelected += 15;
        timeProgress = 0;
        pauseOffset = 0;

        startTimer(new long[]{pauseOffset});
        binding.pbTimer.setMax(timeSelected);
        binding.textsec.setVisibility(View.VISIBLE);
        Toast.makeText(this, "15 sec added", Toast.LENGTH_SHORT).show();
    }
}

        @Override
        protected void onDestroy () {
            super.onDestroy();
            if (timeCountDown != null) {
                timeCountDown.cancel();
                timeProgress = 0;
            }
        }

}