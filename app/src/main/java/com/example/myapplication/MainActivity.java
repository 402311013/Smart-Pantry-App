package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // main layout setup
        setContentView(R.layout.activity_main);

        // get buttons from layout
        Button btnPantry = findViewById(R.id.btnPantry);
        Button btnRecipes = findViewById(R.id.btnRecipes);
        Button btnSettings = findViewById(R.id.btnSettings);

        // open pantry
        btnPantry.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, PantryActivity.class)));

        // open suggested recipes
        btnRecipes.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SuggestedRecipesActivity.class)));

        // open settings
        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
    }
}
