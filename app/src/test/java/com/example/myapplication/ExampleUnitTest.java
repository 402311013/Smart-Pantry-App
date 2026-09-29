package com.example.myapplication;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ExampleUnitTest {

    @Test
    public void testPantryItemModel() {
        PantryItem item = new PantryItem(10, "Chicken Breast", "500", "g", "2026-12-31");
        assertEquals(10, item.getId());
        assertEquals("Chicken Breast", item.getName());
        assertEquals("500", item.getQuantity());
        assertEquals("g", item.getUnit());
        assertEquals("2026-12-31", item.getExpiryDate());

        item.setName("Chicken Thighs");
        item.setQuantity("1");
        item.setUnit("kg");
        assertEquals("Chicken Thighs", item.getName());
        assertEquals("1", item.getQuantity());
        assertEquals("kg", item.getUnit());
    }

    @Test
    public void testRecipeModelAndMissingSummary() {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        ingredients.add(new RecipeIngredient("Bread Loaf", 1, "loaf"));
        ingredients.add(new RecipeIngredient("Cheese", 1, "pack"));

        Recipe recipe = new Recipe(1, "Cheese Toast", ingredients, "Toast bread with cheese.", "10 mins");
        assertEquals("Cheese Toast", recipe.getName());
        assertEquals(2, recipe.getIngredients().size());
        assertEquals("10 mins", recipe.getPrepTime());

        recipe.setMissingIngredientSummary("Missing: 1 pack Cheese");
        assertEquals("Missing: 1 pack Cheese", recipe.getMissingIngredientSummary());
    }

    @Test
    public void testExpiredItemsExcludedFromRecipeAvailability() {
        DatabaseHelper helper = new DatabaseHelper(null);
        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem(1, "Milk", "2", "litre", "2020-01-01")); // Expired item
        pantry.add(new PantryItem(2, "Sugar", "1", "kg", "2099-01-01")); // Fresh item

        assertTrue(helper.isExpired("2020-01-01"));
        assertFalse(helper.isExpired("2099-01-01"));
        assertFalse(helper.isExpired(""));
        assertFalse(helper.isExpired(null));

        // Available quantity of expired milk should be 0.0
        assertEquals(0.0, helper.getAvailableQuantity(pantry, "Milk", "litre"), 0.001);
        // Available quantity of fresh sugar should be 1.0
        assertEquals(1.0, helper.getAvailableQuantity(pantry, "Sugar", "kg"), 0.001);
    }
}