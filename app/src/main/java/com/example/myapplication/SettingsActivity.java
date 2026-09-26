package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set layout for settings screen
        setContentView(R.layout.activity_settings);

        // setup bottom navigation bar
        NavigationHelper.setupNavBar(this, R.id.navSettings);

        // save preferences button
        Button btnSaveSettings = findViewById(R.id.btnSaveSettings);
        if (btnSaveSettings != null) {
            btnSaveSettings.setOnClickListener(v ->
                    Toast.makeText(SettingsActivity.this, "Settings saved", Toast.LENGTH_SHORT).show());
        }

        // back button
        Button btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}
