package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREF_NAME = "AppSettings";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_RECIPE_SUGGESTIONS = "recipe_suggestions";

    private SwitchCompat switchExpiryAlerts, switchRecipeSuggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        NavigationHelper.setupNavBar(this, R.id.navSettings);

        switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);
        switchRecipeSuggestions = findViewById(R.id.switchRecipeSuggestions);

        // load saved preferences
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean expiryAlertsOn = prefs.getBoolean(KEY_EXPIRY_ALERTS, true);
        boolean recipeSuggestionsOn = prefs.getBoolean(KEY_RECIPE_SUGGESTIONS, true);

        if (switchExpiryAlerts != null) switchExpiryAlerts.setChecked(expiryAlertsOn);
        if (switchRecipeSuggestions != null) switchRecipeSuggestions.setChecked(recipeSuggestionsOn);

        Button btnSaveSettings = findViewById(R.id.btnSaveSettings);
        if (btnSaveSettings != null) {
            btnSaveSettings.setOnClickListener(v -> {
                boolean expiryVal = switchExpiryAlerts != null && switchExpiryAlerts.isChecked();
                boolean recipeVal = switchRecipeSuggestions != null && switchRecipeSuggestions.isChecked();

                SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
                editor.putBoolean(KEY_EXPIRY_ALERTS, expiryVal);
                editor.putBoolean(KEY_RECIPE_SUGGESTIONS, recipeVal);
                editor.apply();

                Toast.makeText(SettingsActivity.this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
