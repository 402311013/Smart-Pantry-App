package com.example.myapplication;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

// instrumented unit tests for sqlite database helper crud functions
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
    public void testInitialDatabaseIsEmpty() {
        // verify database starts empty on creation
        List<PantryItem> items = dbHelper.getAllPantryItems();
        assertNotNull(items);
        assertTrue(items.isEmpty());
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
        PantryItem item = new PantryItem("Salt", "500", "g", "2027-01-01");
        long id = dbHelper.addPantryItem(item);

        item.setName("Sea Salt");
        item.setQuantity("1");
        item.setUnit("kg");
        item.setExpiryDate("2028-01-01");

        int rowsUpdated = dbHelper.updatePantryItem(item);
        assertEquals(1, rowsUpdated);

        List<PantryItem> items = dbHelper.getAllPantryItems();
        boolean updatedFound = false;
        for (PantryItem fetched : items) {
            if (fetched.getId() == id) {
                assertEquals("Sea Salt", fetched.getName());
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
        PantryItem item = new PantryItem("Sugar", "1", "kg", "2026-05-01");
        long id = dbHelper.addPantryItem(item);

        int rowsDeleted = dbHelper.deletePantryItem(id);
        assertEquals(1, rowsDeleted);

        List<PantryItem> items = dbHelper.getAllPantryItems();
        for (PantryItem fetched : items) {
            assertTrue(fetched.getId() != id);
        }
    }
}
