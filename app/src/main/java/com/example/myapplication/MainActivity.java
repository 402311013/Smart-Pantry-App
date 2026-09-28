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
    private TextView txtItemCount, txtRecipeCount, tvExpiringSoon;

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
        txtRecipeCount = findViewById(R.id.txtRecipeCount);
        tvExpiringSoon = findViewById(R.id.tvExpiringSoon);

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
        // update dashboard counts and expiring items dynamically from database
        if (dbHelper != null) {
            List<PantryItem> items = dbHelper.getAllPantryItems();

            if (txtItemCount != null) {
                txtItemCount.setText(String.valueOf(items.size()));
            }

            if (txtRecipeCount != null) {
                List<Recipe> matchingRecipes = dbHelper.getStrictMatchingRecipes(items);
                txtRecipeCount.setText(String.valueOf(matchingRecipes.size()));
            }

            if (tvExpiringSoon != null) {
                StringBuilder sb = new StringBuilder();
                boolean hasExpiring = false;
                for (PantryItem item : items) {
                    if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
                        if (hasExpiring) {
                            sb.append(" • ");
                        }
                        sb.append(item.getName()).append(" (").append(item.getExpiryDate()).append(")");
                        hasExpiring = true;
                    }
                }
                if (hasExpiring) {
                    tvExpiringSoon.setText(sb.toString());
                } else {
                    tvExpiringSoon.setText("No expiry dates set for pantry items");
                }
            }
        }
    }
}
