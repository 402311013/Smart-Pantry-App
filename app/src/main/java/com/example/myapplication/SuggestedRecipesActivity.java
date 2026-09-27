package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView rvRecipes;
    private TextView tvNoRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        NavigationHelper.setupNavBar(this, R.id.navRecipes);

        dbHelper = new DatabaseHelper(this);
        rvRecipes = findViewById(R.id.rvRecipes);
        tvNoRecipes = findViewById(R.id.tvNoRecipes);

        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStrictMatchingRecipes();
    }

    private void loadStrictMatchingRecipes() {
        List<PantryItem> pantryItems = dbHelper.getAllPantryItems();
        List<Recipe> matchingRecipes = dbHelper.getStrictMatchingRecipes(pantryItems);

        if (matchingRecipes.isEmpty()) {
            rvRecipes.setVisibility(View.GONE);
            tvNoRecipes.setVisibility(View.VISIBLE);
        } else {
            rvRecipes.setVisibility(View.VISIBLE);
            tvNoRecipes.setVisibility(View.GONE);

            RecipeAdapter adapter = new RecipeAdapter(matchingRecipes, recipe -> {
                Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
                intent.putExtra("recipe_name", recipe.getName());
                intent.putExtra("recipe_prep_time", recipe.getPrepTime());

                StringBuilder sb = new StringBuilder();
                for (RecipeIngredient ing : recipe.getIngredients()) {
                    sb.append("• ").append(ing.getQuantity()).append(" ").append(ing.getUnit()).append(" ").append(ing.getName()).append("\n");
                }
                intent.putExtra("recipe_ingredients", sb.toString().trim());
                intent.putExtra("recipe_method", recipe.getMethod());
                startActivity(intent);
            });
            rvRecipes.setAdapter(adapter);
        }
    }
}
