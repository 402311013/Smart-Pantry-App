package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView txtItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set layout for main activity
        setContentView(R.layout.activity_main);

        // setup bottom navigation bar
        NavigationHelper.setupNavBar(this, R.id.navHome);

        dbHelper = new DatabaseHelper(this);

        // find layout views
        CardView btnPantry = findViewById(R.id.btnPantry);
        Button btnAddIngredient = findViewById(R.id.btnAddIngredient);
        Button btnRecipes = findViewById(R.id.btnRecipes);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnViewRecipe = findViewById(R.id.btnViewRecipe);
        txtItemCount = findViewById(R.id.txtItemCount);

        // open pantry screen
        if (btnPantry != null) {
            btnPantry.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, PantryActivity.class)));
        }

        if (btnAddIngredient != null) {
            btnAddIngredient.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, PantryActivity.class)));
        }

        // open suggested recipes screen
        if (btnRecipes != null) {
            btnRecipes.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, SuggestedRecipesActivity.class)));
        }

        if (btnViewRecipe != null) {
            btnViewRecipe.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, SuggestedRecipesActivity.class)));
        }

        // open settings screen
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // update pantry item count from database
        if (dbHelper != null && txtItemCount != null) {
            List<PantryItem> items = dbHelper.getAllPantryItems();
            txtItemCount.setText(String.valueOf(items.size()));
        }
    }
}
