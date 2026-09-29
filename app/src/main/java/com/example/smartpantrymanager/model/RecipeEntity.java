package com.example.smartpantrymanager.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "recipes")
public class RecipeEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;
    private String prepTime;
    private String ingredients; // comma separated with amounts, e.g. "2 eggs, 1 tbsp butter, salt"
    private String instructions;

    public RecipeEntity(String title, String prepTime, String ingredients, String instructions) {
        this.title = title;
        this.prepTime = prepTime;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPrepTime() { return prepTime; }
    public void setPrepTime(String prepTime) { this.prepTime = prepTime; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public List<String> getRequiredIngredientsList() {
        if (ingredients == null || ingredients.isEmpty()) {
            return new ArrayList<>();
        }
        String[] parts = ingredients.split(",");
        List<String> list = new ArrayList<>();
        for (String p : parts) {
            String trimmed = p.trim();
            if (!trimmed.isEmpty()) {
                list.add(trimmed);
            }
        }
        return list;
    }

    public List<RecipeIngredient> getRecipeIngredients() {
        if (ingredients == null || ingredients.isEmpty()) {
            return new ArrayList<>();
        }
        String[] parts = ingredients.split(",");
        List<RecipeIngredient> list = new ArrayList<>();
        for (String p : parts) {
            RecipeIngredient ri = new RecipeIngredient(p);
            if (!ri.getName().isEmpty()) {
                list.add(ri);
            }
        }
        return list;
    }
}
