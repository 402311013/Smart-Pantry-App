package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView txtItemCount, txtRecipeCount, tvExpiringSoon;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

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
        // update dashboard counts and expiring items from database
        if (dbHelper != null) {
            List<PantryItem> items = dbHelper.getAllPantryItems();

            // load preferences
            SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
            boolean expiryAlertsEnabled = prefs.getBoolean("expiry_alerts", true);
            boolean suggestionsEnabled = prefs.getBoolean("recipe_suggestions", true);

            if (txtItemCount != null) {
                txtItemCount.setText(String.valueOf(items.size()));
            }

            if (txtRecipeCount != null) {
                if (suggestionsEnabled) {
                    List<Recipe> matchingRecipes = dbHelper.getStrictMatchingRecipes(items);
                    txtRecipeCount.setText(String.valueOf(matchingRecipes.size()));
                } else {
                    txtRecipeCount.setText("-");
                }
            }

            if (tvExpiringSoon != null) {
                if (!expiryAlertsEnabled) {
                    tvExpiringSoon.setText("Expiry alerts are disabled in Settings");
                } else {
                    StringBuilder sb = new StringBuilder();
                    boolean hasExpiring = false;
                    for (PantryItem item : items) {
                        String expiry = item.getExpiryDate();
                        if (!TextUtils.isEmpty(expiry)) {
                            int daysRemaining = getDaysUntilExpiry(expiry);
                            // display items expiring within 7 days
                            if (daysRemaining >= 0 && daysRemaining <= 7) {
                                if (hasExpiring) {
                                    sb.append("\n");
                                }
                                String dueLabel;
                                if (daysRemaining == 0) {
                                    dueLabel = "Expires today";
                                } else if (daysRemaining == 1) {
                                    dueLabel = "Expires tomorrow 1 day";
                                } else {
                                    dueLabel = "Expires in " + daysRemaining + " days " + expiry;
                                }
                                sb.append("- ").append(item.getName()).append(" -- ").append(dueLabel);
                                hasExpiring = true;
                            }
                        }
                    }
                    if (hasExpiring) {
                        tvExpiringSoon.setText(sb.toString());
                    } else {
                        tvExpiringSoon.setText("No items expiring within the upcoming 7 days");
                    }
                }
            }
        }
    }

    // calculate days until expiry
    private int getDaysUntilExpiry(String expiryDateStr) {
        if (TextUtils.isEmpty(expiryDateStr)) return 999;
        try {
            Date expiryDate = dateFormat.parse(expiryDateStr.trim());
            if (expiryDate == null) return 999;

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            Calendar expCal = Calendar.getInstance();
            expCal.setTime(expiryDate);
            expCal.set(Calendar.HOUR_OF_DAY, 0);
            expCal.set(Calendar.MINUTE, 0);
            expCal.set(Calendar.SECOND, 0);
            expCal.set(Calendar.MILLISECOND, 0);

            long diffMillis = expCal.getTimeInMillis() - today.getTimeInMillis();
            return (int) (diffMillis / (1000L * 60 * 60 * 24));
        } catch (Exception e) {
            return 999;
        }
    }
}
