package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Connect this activity to the main layout file
        setContentView(R.layout.activity_main);

        // Grab button references from the layout
        Button btnPantry = findViewById(R.id.btnPantry);
        Button btnRecipes = findViewById(R.id.btnRecipes);
        Button btnSettings = findViewById(R.id.btnSettings);

        // Go to pantry screen
        btnPantry.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, PantryActivity.class)));

        // Go to suggested recipes screen
        btnRecipes.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SuggestedRecipesActivity.class)));

        // Go to settings screen
        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
    }
}
