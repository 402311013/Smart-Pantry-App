package com.example.myapplication;

import java.util.List;

public class Recipe {
    private long id;
    private String name;
    private List<RecipeIngredient> ingredients;
    private String method;
    private String prepTime;

    public Recipe(long id, String name, List<RecipeIngredient> ingredients, String method, String prepTime) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.method = method;
        this.prepTime = prepTime;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public String getMethod() {
        return method;
    }

    public String getPrepTime() {
        return prepTime;
    }
}
