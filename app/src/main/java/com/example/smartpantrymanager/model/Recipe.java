package com.example.smartpantrymanager.model;

import java.util.List;

public class Recipe {
    private final String title;
    private final String prepTime;
    private final List<String> requiredIngredients;
    private final String instructions;

    public Recipe(String title, String prepTime, List<String> requiredIngredients, String instructions) {
        this.title = title;
        this.prepTime = prepTime;
        this.requiredIngredients = requiredIngredients;
        this.instructions = instructions;
    }

    public String getTitle() { return title; }
    public String getPrepTime() { return prepTime; }
    public List<String> getRequiredIngredients() { return requiredIngredients; }
    public String getInstructions() { return instructions; }
}
