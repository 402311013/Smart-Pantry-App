package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set layout for recipe detail activity
        setContentView(R.layout.activity_recipe_detail);

        // get recipe extras from intent
        String name = getIntent().getStringExtra("recipe_name");
        String prepTime = getIntent().getStringExtra("recipe_prep_time");
        String ingredients = getIntent().getStringExtra("recipe_ingredients");
        String method = getIntent().getStringExtra("recipe_method");
        String missing = getIntent().getStringExtra("recipe_missing");

        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvPrepTime = findViewById(R.id.tvDetailPrepTime);
        TextView tvMissing = findViewById(R.id.tvDetailMissing);
        TextView tvIngredients = findViewById(R.id.tvDetailIngredients);
        TextView tvMethod = findViewById(R.id.tvDetailMethod);

        if (name != null) tvTitle.setText(name);
        if (prepTime != null) tvPrepTime.setText("Prep Time: " + prepTime);
        if (missing != null && !missing.trim().isEmpty()) {
            tvMissing.setText("Missing: " + missing);
            tvMissing.setVisibility(View.VISIBLE);
        } else {
            tvMissing.setVisibility(View.GONE);
        }
        if (ingredients != null) tvIngredients.setText(ingredients);
        if (method != null) tvMethod.setText(method);

        // finish activity on back click
        Button btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        View btnBackHeader = findViewById(R.id.btnBackHeader);
        if (btnBackHeader != null) {
            btnBackHeader.setOnClickListener(v -> finish());
        }
    }
}
