package com.example.currencyconverter; // Keep your package name!

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat; // THIS IS THE FIX FOR YOUR CRASH

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        // This takes over that purple bar and renames it to "Settings"
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Settings");
        }

        SwitchCompat switchTheme = findViewById(R.id.switchTheme);
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);

        // Set switch to saved state
        switchTheme.setChecked(prefs.getBoolean("dark_mode", false));

        // Listen for the toggle
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }
}