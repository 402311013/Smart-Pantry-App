package com.example.myapplication;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView rvRecipes;
    private TextView tvNoRecipes, tvSubtitle;
    private View tvNoRecipesContainer;
    private Button btnTabStrict, btnTabAlmostThere;

    private List<Recipe> strictMatchingRecipes = new ArrayList<>();
    private List<Recipe> almostThereRecipes = new ArrayList<>();
    private boolean isStrictTabActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set layout for suggested recipes activity
        setContentView(R.layout.activity_suggested_recipes);

        // setup bottom navigation bar
        NavigationHelper.setupNavBar(this, R.id.navRecipes);

        dbHelper = new DatabaseHelper(this);
        rvRecipes = findViewById(R.id.rvRecipes);
        tvNoRecipes = findViewById(R.id.tvNoRecipes);
        tvNoRecipesContainer = findViewById(R.id.tvNoRecipesContainer);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        btnTabStrict = findViewById(R.id.btnTabStrict);
        btnTabAlmostThere = findViewById(R.id.btnTabAlmostThere);

        rvRecipes.setLayoutManager(new LinearLayoutManager(this));

        // handle strict tab click
        if (btnTabStrict != null) {
            btnTabStrict.setOnClickListener(v -> {
                isStrictTabActive = true;
                updateTabUI();
            });
        }

        // handle almost there tab click
        if (btnTabAlmostThere != null) {
            btnTabAlmostThere.setOnClickListener(v -> {
                isStrictTabActive = false;
                updateTabUI();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // load recipes from database
        loadRecipes();
    }

    // load matching recipes from database
    private void loadRecipes() {
        List<PantryItem> pantryItems = dbHelper.getAllPantryItems();
        strictMatchingRecipes = dbHelper.getStrictMatchingRecipes(pantryItems);
        almostThereRecipes = dbHelper.getAlmostThereRecipes(pantryItems);

        if (btnTabStrict != null) {
            btnTabStrict.setText("Strict " + strictMatchingRecipes.size());
        }
        if (btnTabAlmostThere != null) {
            btnTabAlmostThere.setText("Almost There " + almostThereRecipes.size());
        }

        updateTabUI();
    }

    // update tab ui elements
    private void updateTabUI() {
        int primaryColor = ContextCompat.getColor(this, R.color.primary);
        int cardBgColor = ContextCompat.getColor(this, R.color.card_bg);
        int textPrimary = ContextCompat.getColor(this, R.color.text_primary);
        int white = Color.WHITE;

        if (isStrictTabActive) {
            if (btnTabStrict != null) {
                btnTabStrict.setBackgroundTintList(ColorStateList.valueOf(primaryColor));
                btnTabStrict.setTextColor(white);
            }
            if (btnTabAlmostThere != null) {
                btnTabAlmostThere.setBackgroundTintList(ColorStateList.valueOf(cardBgColor));
                btnTabAlmostThere.setTextColor(textPrimary);
            }
            if (tvSubtitle != null) {
                tvSubtitle.setText("100 percent of required ingredients available in pantry");
            }
            displayList(strictMatchingRecipes, "No recipes match your pantry yet add more ingredients");
        } else {
            if (btnTabAlmostThere != null) {
                btnTabAlmostThere.setBackgroundTintList(ColorStateList.valueOf(primaryColor));
                btnTabAlmostThere.setTextColor(white);
            }
            if (btnTabStrict != null) {
                btnTabStrict.setBackgroundTintList(ColorStateList.valueOf(cardBgColor));
                btnTabStrict.setTextColor(textPrimary);
            }
            if (tvSubtitle != null) {
                tvSubtitle.setText("Recipes missing only 1 required ingredient");
            }
            displayList(almostThereRecipes, "No recipes found that are 1 ingredient away");
        }
    }

    // display recipe list in recycler view
    private void displayList(List<Recipe> recipes, String emptyMessage) {
        if (recipes == null || recipes.isEmpty()) {
            rvRecipes.setVisibility(View.GONE);
            tvNoRecipes.setText(emptyMessage);
            tvNoRecipes.setVisibility(View.VISIBLE);
            if (tvNoRecipesContainer != null) tvNoRecipesContainer.setVisibility(View.VISIBLE);
        } else {
            rvRecipes.setVisibility(View.VISIBLE);
            tvNoRecipes.setVisibility(View.GONE);
            if (tvNoRecipesContainer != null) tvNoRecipesContainer.setVisibility(View.GONE);

            RecipeAdapter adapter = new RecipeAdapter(recipes, recipe -> {
                Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
                intent.putExtra("recipe_name", recipe.getName());
                intent.putExtra("recipe_prep_time", recipe.getPrepTime());

                StringBuilder sb = new StringBuilder();
                for (RecipeIngredient ing : recipe.getIngredients()) {
                    sb.append("- ").append(dbHelper.formatQuantity(ing.getQuantity())).append(" ")
                            .append(ing.getUnit()).append(" ")
                            .append(ing.getName()).append("\n");
                }
                intent.putExtra("recipe_ingredients", sb.toString().trim());
                intent.putExtra("recipe_method", recipe.getMethod());
                intent.putExtra("recipe_missing", recipe.getMissingIngredientSummary());
                startActivity(intent);
            });
            rvRecipes.setAdapter(adapter);
        }
    }
}
