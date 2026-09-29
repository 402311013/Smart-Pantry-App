package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationHelper {
    public static void setupNavBar(Activity activity, int currentTabId) {
        View navHome = activity.findViewById(R.id.navHome);
        View navPantry = activity.findViewById(R.id.navPantry);
        View navRecipes = activity.findViewById(R.id.navRecipes);
        View navSettings = activity.findViewById(R.id.navSettings);

        highlightTab(activity, currentTabId);

        if (navHome != null && currentTabId != R.id.navHome) {
            navHome.setOnClickListener(v -> {
                if (!(activity instanceof MainActivity)) {
                    activity.startActivity(new Intent(activity, MainActivity.class));
                    activity.finish();
                }
            });
        }

        if (navPantry != null && currentTabId != R.id.navPantry) {
            navPantry.setOnClickListener(v -> {
                if (!(activity instanceof PantryActivity)) {
                    activity.startActivity(new Intent(activity, PantryActivity.class));
                    activity.finish();
                }
            });
        }

        if (navRecipes != null && currentTabId != R.id.navRecipes) {
            navRecipes.setOnClickListener(v -> {
                if (!(activity instanceof SuggestedRecipesActivity)) {
                    activity.startActivity(new Intent(activity, SuggestedRecipesActivity.class));
                    activity.finish();
                }
            });
        }

        if (navSettings != null && currentTabId != R.id.navSettings) {
            navSettings.setOnClickListener(v -> {
                if (!(activity instanceof SettingsActivity)) {
                    activity.startActivity(new Intent(activity, SettingsActivity.class));
                    activity.finish();
                }
            });
        }
    }

    private static void highlightTab(Activity activity, int activeId) {
        int[] allTabs = {R.id.navHome, R.id.navPantry, R.id.navRecipes, R.id.navSettings};
        for (int id : allTabs) {
            View tab = activity.findViewById(id);
            if (tab != null) {
                ImageView icon = (ImageView) ((ViewGroup) tab).getChildAt(0);
                TextView label = (TextView) ((ViewGroup) tab).getChildAt(1);
                boolean isActive = (id == activeId);
                int color = isActive ? Color.parseColor("#15803D") : Color.parseColor("#94A3B8");
                if (icon != null) icon.setColorFilter(color);
                if (label != null) {
                    label.setTextColor(color);
                    label.setTypeface(null, isActive ? Typeface.BOLD : Typeface.NORMAL);
                }
            }
        }
    }
}
