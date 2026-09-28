package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

// handles sqlite database setup and crud operations
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pantry.db";
    private static final int DATABASE_VERSION = 16;

    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_EXPIRY_DATE = "expiry_date";

    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create tables for pantry and recipes
        String createPantryQuery = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_QUANTITY + " TEXT, " +
                COLUMN_UNIT + " TEXT, " +
                COLUMN_EXPIRY_DATE + " TEXT" +
                ")";
        db.execSQL(createPantryQuery);

        String createRecipesQuery = "CREATE TABLE " + TABLE_RECIPES + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "prep_time TEXT, " +
                "method TEXT" +
                ")";
        db.execSQL(createRecipesQuery);

        String createRecipeIngQuery = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER, " +
                "ingredient_name TEXT NOT NULL, " +
                "quantity REAL, " +
                "unit TEXT" +
                ")";
        db.execSQL(createRecipeIngQuery);

        // pre populate pantry with ingredients matching local recipes
        insertInitialPantryItems(db);

        // seed authentic recipes into database
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop tables and recreate on upgrade
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        onCreate(db);
    }

    private void insertInitialPantryItems(SQLiteDatabase db) {
        insertPantryDirect(db, "Mutton", "2", "kg", "2025-12-31");
        insertPantryDirect(db, "Chicken", "2", "kg", "2025-12-31");
        insertPantryDirect(db, "Bread Loaf", "2", "loaf", "2025-12-31");
        insertPantryDirect(db, "Potatoes", "10", "pcs", "2025-12-31");
        insertPantryDirect(db, "Tomatoes", "10", "pcs", "2025-12-31");
        insertPantryDirect(db, "Onion", "5", "pcs", "2025-12-31");
        insertPantryDirect(db, "Garlic", "3", "bulb", "2025-12-31");
        insertPantryDirect(db, "Ginger", "2", "pc", "2025-12-31");
        insertPantryDirect(db, "Curry Powder", "5", "packs", "2026-06-30");
        insertPantryDirect(db, "Ghee", "3", "jar", "2026-06-30");
        insertPantryDirect(db, "Salt", "2", "pack", "2027-01-01");
        insertPantryDirect(db, "Sugar Beans", "2", "kg", "2026-06-30");
        insertPantryDirect(db, "Basmati Rice", "5", "kg", "2026-06-30");
        insertPantryDirect(db, "Lentils", "2", "kg", "2026-06-30");
        insertPantryDirect(db, "Mixed Vegetables", "3", "kg", "2025-12-31");
        insertPantryDirect(db, "Curd", "2", "pack", "2025-12-31");
        insertPantryDirect(db, "Biryani Masala", "3", "pack", "2026-06-30");
        insertPantryDirect(db, "Roti", "10", "pcs", "2025-12-31");
        insertPantryDirect(db, "Samp", "2", "kg", "2026-06-30");
        insertPantryDirect(db, "Crab", "2", "kg", "2025-12-31");
        insertPantryDirect(db, "Minced Meat", "2", "kg", "2025-12-31");
        insertPantryDirect(db, "Pastry Sheets", "3", "pack", "2025-12-31");
        insertPantryDirect(db, "Oil", "2", "bottle", "2026-06-30");
        insertPantryDirect(db, "Banana", "5", "pcs", "2025-12-31");
        insertPantryDirect(db, "Milk", "2", "litre", "2025-12-31");
        insertPantryDirect(db, "Sugar", "1", "kg", "2027-01-01");
        insertPantryDirect(db, "Avocado", "3", "pcs", "2025-12-31");
        insertPantryDirect(db, "Cheese", "2", "pack", "2025-12-31");
        insertPantryDirect(db, "Butter", "2", "block", "2025-12-31");
        insertPantryDirect(db, "Chilli", "10", "pcs", "2025-12-31");
    }

    private void insertPantryDirect(SQLiteDatabase db, String name, String qty, String unit, String expiry) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_QUANTITY, qty);
        values.put(COLUMN_UNIT, unit);
        values.put(COLUMN_EXPIRY_DATE, expiry);
        db.insert(TABLE_PANTRY, null, values);
    }

    // seed recipes and their ingredients
    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Cheese Toast", "10 mins", "Slice the tomatoes and onions thinly, slice fresh chillies. Place cheese and the tomato, onion, and chilli slices between bread slices, butter the outside, and toast in a pan until golden and crispy.",
                new String[]{"Bread Loaf", "Cheese", "Tomatoes", "Onion", "Chilli", "Butter"},
                new double[]{1, 1, 2, 1, 2, 1}, new String[]{"loaf", "pack", "pcs", "pcs", "pcs", "block"});

        addRecipe(db, "Avocado Sandwich", "5 mins", "Slice the avocado in half, remove the pit, scoop it into a bowl, and mash it up. Add three teaspoons of sugar and mix together until slightly lumpy. Spread the mixture onto bread to make a sandwich, slice it, and it is ready.",
                new String[]{"Avocado", "Sugar", "Bread Loaf"},
                new double[]{1, 3, 1}, new String[]{"pcs", "tsp", "loaf"});

        addRecipe(db, "Banana Milk", "5 mins", "Slice the banana into pieces, add one teaspoon of sugar, put it into a tumbler, and mix for 1 minute until ready.",
                new String[]{"Banana", "Milk", "Sugar"},
                new double[]{2, 1, 2}, new String[]{"pcs", "litre", "tsp"});

        addRecipe(db, "Mutton Curry", "45 mins", "Brown onions and garlic in ghee, add curry powder and tomatoes, brown the mutton pieces, add cubed potatoes and water, then simmer slowly until tender.",
                new String[]{"Mutton", "Potatoes", "Tomatoes", "Onion", "Garlic", "Ginger", "Curry Powder", "Ghee", "Salt"},
                new double[]{1, 4, 3, 2, 1, 1, 2, 1, 1}, new String[]{"kg", "pcs", "pcs", "pcs", "bulb", "pc", "packs", "jar", "pack"});

        addRecipe(db, "Chicken Curry", "35 mins", "Sauté onions and ginger-garlic paste in ghee, add curry powder and chopped tomatoes, add chicken pieces and potatoes, and simmer until cooked through.",
                new String[]{"Chicken", "Potatoes", "Tomatoes", "Onion", "Garlic", "Ginger", "Curry Powder", "Ghee", "Salt"},
                new double[]{1, 3, 3, 2, 1, 1, 2, 1, 1}, new String[]{"kg", "pcs", "pcs", "pcs", "bulb", "pc", "packs", "jar", "pack"});

        addRecipe(db, "Bean Bunny Chow", "30 mins", "Cook sugar beans with curry spices and tomatoes. Hollow out half a loaf of bread and pour the rich bean curry inside.",
                new String[]{"Sugar Beans", "Bread Loaf", "Onion", "Tomatoes", "Curry Powder", "Ghee", "Garlic", "Salt"},
                new double[]{1, 1, 2, 2, 1, 1, 1, 1}, new String[]{"kg", "loaf", "pcs", "pcs", "pack", "jar", "bulb", "pack"});

        addRecipe(db, "Mutton Bunny Chow", "50 mins", "Prepare rich mutton curry. Hollow out a fresh bread loaf and fill it to the brim with hot mutton curry.",
                new String[]{"Mutton", "Bread Loaf", "Potatoes", "Tomatoes", "Onion", "Curry Powder", "Ghee", "Garlic", "Salt"},
                new double[]{1, 1, 3, 3, 2, 2, 1, 1, 1}, new String[]{"kg", "loaf", "pcs", "pcs", "pcs", "packs", "jar", "bulb", "pack"});

        addRecipe(db, "Chicken Bunny Chow", "40 mins", "Cook spicy chicken curry. Scoop out the center of a bread loaf and fill with chicken curry.",
                new String[]{"Chicken", "Bread Loaf", "Potatoes", "Tomatoes", "Onion", "Curry Powder", "Ghee", "Garlic", "Salt"},
                new double[]{1, 1, 3, 3, 2, 2, 1, 1, 1}, new String[]{"kg", "loaf", "pcs", "pcs", "pcs", "packs", "jar", "bulb", "pack"});

        addRecipe(db, "Fish Curry", "25 mins", "Sauté onions and garlic in ghee, add curry powder and tomatoes with tamarind, then gently simmer fish pieces until cooked.",
                new String[]{"Fish", "Tomatoes", "Onion", "Garlic", "Tamarind", "Curry Powder", "Ghee", "Salt"},
                new double[]{1, 3, 2, 1, 1, 1, 1, 1}, new String[]{"kg", "pcs", "pcs", "bulb", "pack", "pack", "jar", "pack"});

        addRecipe(db, "Prawn Curry", "30 mins", "Sauté onions and garlic in ghee, add curry powder, tomatoes, and coconut milk, then simmer prawns until pink and tender.",
                new String[]{"Prawns", "Tomatoes", "Onion", "Garlic", "Ginger", "Curry Powder", "Ghee", "Coconut Milk", "Salt"},
                new double[]{1, 3, 2, 1, 1, 1, 1, 1, 1}, new String[]{"kg", "pcs", "pcs", "bulb", "pc", "pack", "jar", "pack", "pack"});

        addRecipe(db, "Vegetable Curry", "25 mins", "Sauté onions and garlic in ghee, add curry powder and chopped tomatoes, toss in mixed vegetables and potatoes, and simmer until tender.",
                new String[]{"Mixed Vegetables", "Potatoes", "Tomatoes", "Onion", "Garlic", "Curry Powder", "Ghee", "Salt"},
                new double[]{1, 2, 2, 2, 1, 1, 1, 1}, new String[]{"kg", "pcs", "pcs", "pcs", "bulb", "pack", "jar", "pack"});

        addRecipe(db, "Dhal and Rice", "30 mins", "Boil lentils with turmeric and salt, then temper with ghee-fried garlic and onions, served over basmati rice.",
                new String[]{"Lentils", "Basmati Rice", "Onion", "Garlic", "Turmeric Powder", "Ghee", "Salt"},
                new double[]{1, 1, 1, 1, 1, 1, 1}, new String[]{"kg", "kg", "pc", "bulb", "pack", "jar", "pack"});

        addRecipe(db, "Chicken Biryani", "50 mins", "Marinate chicken in spiced curd, layer with aromatic basmati rice, potatoes, and fried onions, then bake or stove-steam until done.",
                new String[]{"Chicken", "Basmati Rice", "Potatoes", "Onion", "Tomatoes", "Ghee", "Curd", "Biryani Masala", "Salt"},
                new double[]{1, 1, 3, 2, 2, 1, 1, 1, 1}, new String[]{"kg", "kg", "pcs", "pcs", "pcs", "jar", "pack", "pack", "pack"});

        addRecipe(db, "Roti and Curry", "40 mins", "Cook flavorful mutton curry and serve piping hot with soft flaky homemade rotis.",
                new String[]{"Mutton", "Roti", "Tomatoes", "Onion", "Curry Powder", "Ghee", "Garlic", "Salt"},
                new double[]{1, 4, 3, 2, 2, 1, 1, 1}, new String[]{"kg", "pcs", "pcs", "pcs", "packs", "jar", "bulb", "pack"});

        addRecipe(db, "Samp and Beans", "90 mins", "Slowly boil samp and sugar beans together until exceptionally soft and creamy, seasoned with butter or ghee and salt.",
                new String[]{"Samp", "Sugar Beans", "Onion", "Ghee", "Salt"},
                new double[]{1, 1, 1, 1, 1}, new String[]{"kg", "kg", "pc", "jar", "pack"});

        addRecipe(db, "Crab Curry", "35 mins", "Sauté onions, garlic, and ginger in ghee, add curry powder and tomatoes, add crab pieces, and simmer in rich gravy.",
                new String[]{"Crab", "Tomatoes", "Onion", "Garlic", "Ginger", "Curry Powder", "Ghee", "Salt"},
                new double[]{1, 3, 2, 1, 1, 1, 1, 1}, new String[]{"kg", "pcs", "pcs", "bulb", "pc", "pack", "jar", "pack"});

        addRecipe(db, "Samoosas", "45 mins", "Cook minced meat with onions and spices, fold into triangular pastry sheets, and fry until golden and crispy.",
                new String[]{"Minced Meat", "Pastry Sheets", "Onion", "Chilli Powder", "Turmeric Powder", "Salt", "Oil"},
                new double[]{1, 1, 2, 1, 1, 1, 1}, new String[]{"kg", "pack", "pcs", "pack", "pack", "pack", "bottle"});

        addRecipe(db, "Chutney Chicken", "30 mins", "Stew chicken pieces in a thick, tangy tomato and onion chutney gravy infused with chillies.",
                new String[]{"Chicken", "Tomatoes", "Onion", "Garlic", "Chilli Powder", "Ghee", "Salt"},
                new double[]{1, 4, 2, 1, 1, 1, 1}, new String[]{"kg", "pcs", "pcs", "bulb", "pack", "jar", "pack"});
    }

    // helper to insert recipe and ingredients
    private void addRecipe(SQLiteDatabase db, String name, String prepTime, String method,
                           String[] ingNames, double[] ingQtys, String[] ingUnits) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("prep_time", prepTime);
        cv.put("method", method);
        long recipeId = db.insert(TABLE_RECIPES, null, cv);

        for (int i = 0; i < ingNames.length; i++) {
            ContentValues icv = new ContentValues();
            icv.put("recipe_id", recipeId);
            icv.put("ingredient_name", ingNames[i]);
            icv.put("quantity", ingQtys[i]);
            icv.put("unit", ingUnits[i]);
            db.insert(TABLE_RECIPE_INGREDIENTS, null, icv);
        }
    }

    // add a new pantry item
    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put(COLUMN_UNIT, item.getUnit());
        values.put(COLUMN_EXPIRY_DATE, item.getExpiryDate());

        long id = db.insert(TABLE_PANTRY, null, values);
        item.setId(id);
        return id;
    }

    // fetch all pantry items from database
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_PANTRY + " ORDER BY " + COLUMN_ID + " ASC";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
            int nameIndex = cursor.getColumnIndexOrThrow(COLUMN_NAME);
            int qtyIndex = cursor.getColumnIndexOrThrow(COLUMN_QUANTITY);
            int unitIndex = cursor.getColumnIndexOrThrow(COLUMN_UNIT);
            int expiryIndex = cursor.getColumnIndexOrThrow(COLUMN_EXPIRY_DATE);

            do {
                long id = cursor.getLong(idIndex);
                String name = cursor.getString(nameIndex);
                String qty = cursor.getString(qtyIndex);
                String unit = cursor.getString(unitIndex);
                String expiry = cursor.getString(expiryIndex);

                PantryItem item = new PantryItem(id, name, qty, unit, expiry);
                itemList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return itemList;
    }

    // update pantry item
    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put(COLUMN_UNIT, item.getUnit());
        values.put(COLUMN_EXPIRY_DATE, item.getExpiryDate());

        return db.update(TABLE_PANTRY, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    // delete pantry item by id
    public int deletePantryItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PANTRY, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // get all recipes from database
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECIPES, null);

        if (cursor.moveToFirst()) {
            int idIdx = cursor.getColumnIndexOrThrow("_id");
            int nameIdx = cursor.getColumnIndexOrThrow("name");
            int prepIdx = cursor.getColumnIndexOrThrow("prep_time");
            int methodIdx = cursor.getColumnIndexOrThrow("method");

            do {
                long id = cursor.getLong(idIdx);
                String name = cursor.getString(nameIdx);
                String prepTime = cursor.getString(prepIdx);
                String method = cursor.getString(methodIdx);

                List<RecipeIngredient> ingredients = getIngredientsForRecipe(db, id);
                recipes.add(new Recipe(id, name, ingredients, method, prepTime));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recipes;
    }

    // get ingredients for a recipe
    private List<RecipeIngredient> getIngredientsForRecipe(SQLiteDatabase db, long recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECIPE_INGREDIENTS + " WHERE recipe_id = ?", new String[]{String.valueOf(recipeId)});
        if (cursor.moveToFirst()) {
            int nameIdx = cursor.getColumnIndexOrThrow("ingredient_name");
            int qtyIdx = cursor.getColumnIndexOrThrow("quantity");
            int unitIdx = cursor.getColumnIndexOrThrow("unit");

            do {
                String name = cursor.getString(nameIdx);
                double qty = cursor.getDouble(qtyIdx);
                String unit = cursor.getString(unitIdx);
                ingredients.add(new RecipeIngredient(name, qty, unit));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ingredients;
    }

    // strict matching rule logic
    public List<Recipe> getStrictMatchingRecipes(List<PantryItem> pantryItems) {
        List<Recipe> allRecipes = getAllRecipes();
        List<Recipe> matchedRecipes = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            boolean allIngredientsPresent = true;

            for (RecipeIngredient req : recipe.getIngredients()) {
                boolean foundAndSufficient = false;

                for (PantryItem pantry : pantryItems) {
                    if (isIngredientMatch(pantry.getName(), req.getName())) {
                        double pantryQty = parseQuantity(pantry.getQuantity());
                        if (pantryQty >= req.getQuantity()) {
                            foundAndSufficient = true;
                            break;
                        }
                    }
                }

                if (!foundAndSufficient) {
                    allIngredientsPresent = false;
                    break;
                }
            }

            if (allIngredientsPresent) {
                matchedRecipes.add(recipe);
            }
        }

        return matchedRecipes;
    }

    // helper to match ingredient names robustly
    private boolean isIngredientMatch(String pantryName, String recipeName) {
        if (pantryName == null || recipeName == null) return false;
        String p = pantryName.trim().toLowerCase();
        String r = recipeName.trim().toLowerCase();

        if (p.equals(r)) return true;

        if (p.endsWith("s") && p.substring(0, p.length() - 1).equals(r)) return true;
        if (r.endsWith("s") && r.substring(0, r.length() - 1).equals(p)) return true;
        if (p.endsWith("es") && p.substring(0, p.length() - 2).equals(r)) return true;
        if (r.endsWith("es") && r.substring(0, r.length() - 2).equals(p)) return true;

        return false;
    }

    // parse quantity string to double safely
    private double parseQuantity(String qtyStr) {
        if (qtyStr == null) return 1.0;
        try {
            return Double.parseDouble(qtyStr.trim());
        } catch (NumberFormatException e) {
            return 1.0;
        }
    }
}
