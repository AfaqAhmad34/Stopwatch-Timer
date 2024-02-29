package com.example.stopwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.stopwatch.Fragments.ClockFragment;
import com.example.stopwatch.Fragments.StopWatchFragment;
import com.example.stopwatch.Fragments.TimerFragment;
import com.example.stopwatch.databinding.ActivityNavigationBinding;
import com.google.android.material.navigation.NavigationBarView;

public class NavigationActivity extends AppCompatActivity {


    ActivityNavigationBinding binding;

    private StopWatchFragment stopWatchFragment;
    private TimerFragment timerFragment;
    private ClockFragment clockFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        stopWatchFragment = new StopWatchFragment();
        timerFragment = new TimerFragment();
        clockFragment = new ClockFragment();


        loadFragment(stopWatchFragment);

        binding.navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                try {
                    if (item.getItemId() == R.id.nav_stop) {
                        loadFragment(stopWatchFragment);
                        return true;
                    } else if (item.getItemId() == R.id.nav_timer) {
                        loadFragment(timerFragment);
                        return true;
                    } else if (item.getItemId() == R.id.nav_clock) {
                        loadFragment(clockFragment);
                        return true;
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    throw e;
                }
            }
        });
    }
    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }


}