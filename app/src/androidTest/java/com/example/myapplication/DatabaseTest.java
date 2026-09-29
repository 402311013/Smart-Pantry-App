package com.example.myapplication;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

// instrumented unit tests for sqlite database helper, crud operations, and matching logic
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    private DatabaseHelper dbHelper;
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // delete database before each test to start fresh
        context.deleteDatabase("pantry.db");
        dbHelper = new DatabaseHelper(context);
    }

    @After
    public void tearDown() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        // clean up database file after testing
        context.deleteDatabase("pantry.db");
    }

    @Test
    public void testInitialPreSeededData() {
        // verify recipes seeded on creation
        List<Recipe> recipes = dbHelper.getAllRecipes();
        assertNotNull(recipes);
        assertEquals(18, recipes.size());

        // verify initial pantry items seeded on creation
        List<PantryItem> items = dbHelper.getAllPantryItems();
        assertNotNull(items);
        assertEquals(30, items.size());
    }

    @Test
    public void testAddAndReadPantryItem() {
        // test create and read operations
        PantryItem newItem = new PantryItem("Olive Oil", "1", "bottle", "2026-12-31");
        long id = dbHelper.addPantryItem(newItem);
        assertTrue(id > 0);

        List<PantryItem> items = dbHelper.getAllPantryItems();
        boolean found = false;
        for (PantryItem item : items) {
            if (item.getId() == id && "Olive Oil".equals(item.getName())) {
                found = true;
                assertEquals("1", item.getQuantity());
                assertEquals("bottle", item.getUnit());
                assertEquals("2026-12-31", item.getExpiryDate());
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testUpdatePantryItem() {
        // test update operation
        PantryItem item = new PantryItem("Paprika", "500", "g", "2027-01-01");
        long id = dbHelper.addPantryItem(item);

        item.setName("Smoked Paprika");
        item.setQuantity("1");
        item.setUnit("kg");
        item.setExpiryDate("2028-01-01");

        int rowsUpdated = dbHelper.updatePantryItem(item);
        assertEquals(1, rowsUpdated);

        List<PantryItem> items = dbHelper.getAllPantryItems();
        boolean updatedFound = false;
        for (PantryItem fetched : items) {
            if (fetched.getId() == id) {
                assertEquals("Smoked Paprika", fetched.getName());
                assertEquals("1", fetched.getQuantity());
                assertEquals("kg", fetched.getUnit());
                assertEquals("2028-01-01", fetched.getExpiryDate());
                updatedFound = true;
                break;
            }
        }
        assertTrue(updatedFound);
    }

    @Test
    public void testDeletePantryItem() {
        // test delete operation
        PantryItem item = new PantryItem("Fresh Thyme", "1", "pack", "2026-05-01");
        long id = dbHelper.addPantryItem(item);

        int rowsDeleted = dbHelper.deletePantryItem(id);
        assertEquals(1, rowsDeleted);

        List<PantryItem> items = dbHelper.getAllPantryItems();
        for (PantryItem fetched : items) {
            assertTrue(fetched.getId() != id);
        }
    }

    @Test
    public void testStrictMatchingExcludesMissingIngredient() {
        // Pantry with only 2 of 3 ingredients for Avocado Sandwich (needs Avocado, Sugar, Bread Loaf)
        List<PantryItem> limitedPantry = new ArrayList<>();
        limitedPantry.add(new PantryItem("Avocado", "1", "pcs"));
        limitedPantry.add(new PantryItem("Sugar", "3", "tsp"));

        List<Recipe> matches = dbHelper.getStrictMatchingRecipes(limitedPantry);
        for (Recipe r : matches) {
            assertFalse("Avocado Sandwich should not match when Bread Loaf is missing",
                    "Avocado Sandwich".equalsIgnoreCase(r.getName()));
        }
    }

    @Test
    public void testStrictMatchingIncludesFullySatisfiedRecipe() {
        // Pantry with all 3 ingredients in sufficient quantity
        List<PantryItem> fullPantry = new ArrayList<>();
        fullPantry.add(new PantryItem("Avocado", "2", "pcs"));
        fullPantry.add(new PantryItem("Sugar", "5", "tsp"));
        fullPantry.add(new PantryItem("Bread Loaf", "1", "loaf"));

        List<Recipe> matches = dbHelper.getStrictMatchingRecipes(fullPantry);
        boolean found = false;
        for (Recipe r : matches) {
            if ("Avocado Sandwich".equalsIgnoreCase(r.getName())) {
                found = true;
                break;
            }
        }
        assertTrue("Avocado Sandwich must be returned when all ingredients are present", found);
    }

    @Test
    public void testAlmostThereIdentifiesSingleMissingIngredient() {
        // Pantry has Avocado and Sugar, but missing Bread Loaf
        List<PantryItem> partialPantry = new ArrayList<>();
        partialPantry.add(new PantryItem("Avocado", "1", "pcs"));
        partialPantry.add(new PantryItem("Sugar", "3", "tsp"));

        List<Recipe> almostThere = dbHelper.getAlmostThereRecipes(partialPantry);
        boolean found = false;
        for (Recipe r : almostThere) {
            if ("Avocado Sandwich".equalsIgnoreCase(r.getName())) {
                found = true;
                assertNotNull(r.getMissingIngredientSummary());
                assertTrue(r.getMissingIngredientSummary().contains("Bread Loaf"));
                break;
            }
        }
        assertTrue("Avocado Sandwich should be detected as Almost There when missing 1 ingredient", found);
    }

    @Test
    public void testPantryDuplicateAggregationAndUnitConversion() {
        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem("Chicken", "500", "g"));
        pantry.add(new PantryItem("Chicken", "1", "kg"));

        double totalKg = dbHelper.getAvailableQuantity(pantry, "Chicken", "kg");
        assertEquals(1.5, totalKg, 0.001);

        double totalG = dbHelper.getAvailableQuantity(pantry, "Chicken", "g");
        assertEquals(1500.0, totalG, 0.001);
    }

    @Test
    public void testExpiredPantryItemExcludedFromStrictMatching() {
        // Pantry with expired Avocado and fresh Sugar + Bread Loaf
        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem("Avocado", "2", "pcs", "2020-01-01")); // Expired
        pantry.add(new PantryItem("Sugar", "5", "tsp", "2099-01-01")); // Fresh
        pantry.add(new PantryItem("Bread Loaf", "1", "loaf", "2099-01-01")); // Fresh

        List<Recipe> matches = dbHelper.getStrictMatchingRecipes(pantry);
        for (Recipe r : matches) {
            assertFalse("Recipe requiring expired Avocado should not match",
                    "Avocado Sandwich".equalsIgnoreCase(r.getName()));
        }
    }
}
