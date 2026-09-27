package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        String name = getIntent().getStringExtra("recipe_name");
        String prepTime = getIntent().getStringExtra("recipe_prep_time");
        String ingredients = getIntent().getStringExtra("recipe_ingredients");
        String method = getIntent().getStringExtra("recipe_method");

        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvPrepTime = findViewById(R.id.tvDetailPrepTime);
        TextView tvIngredients = findViewById(R.id.tvDetailIngredients);
        TextView tvMethod = findViewById(R.id.tvDetailMethod);

        if (name != null) tvTitle.setText(name);
        if (prepTime != null) tvPrepTime.setText("Prep Time: " + prepTime);
        if (ingredients != null) tvIngredients.setText(ingredients);
        if (method != null) tvMethod.setText(method);

        Button btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}
