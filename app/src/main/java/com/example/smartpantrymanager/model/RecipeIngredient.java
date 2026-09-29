package com.example.smartpantrymanager.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RecipeIngredient {
    private final String name;
    private final double amount;
    private final String unit;
    private final String raw;

    private static final Set<String> UNITS = new HashSet<>(Arrays.asList(
            "cup", "cups", "tbsp", "tsp", "tablespoon", "tablespoons", "teaspoon", "teaspoons",
            "g", "kg", "ml", "l", "oz", "lb", "pinch", "clove", "cloves", "can", "cans",
            "slice", "slices", "piece", "pieces", "gram", "grams", "packet", "packets"
    ));

    public RecipeIngredient(String raw) {
        this.raw = raw != null ? raw.trim() : "";
        double parsedAmount = 1.0;
        String parsedUnit = "";
        String cleanName = this.raw;

        if (!this.raw.isEmpty()) {
            String[] parts = this.raw.split("\\s+");
            try {
                parsedAmount = Double.parseDouble(parts[0]);
                int startIndex = 1;
                if (parts.length > 1 && UNITS.contains(parts[1].toLowerCase())) {
                    parsedUnit = parts[1];
                    startIndex = 2;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = startIndex; i < parts.length; i++) {
                    sb.append(parts[i]).append(" ");
                }
                cleanName = sb.toString().trim();
                if (cleanName.isEmpty() && parts.length > 1) {
                    cleanName = parts[1];
                }
            } catch (NumberFormatException e) {
                parsedAmount = 1.0;
                cleanName = this.raw;
            }
        }
        this.amount = Math.max(0.1, parsedAmount);
        this.unit = parsedUnit;
        this.name = cleanName.trim().toLowerCase();
    }

    public String getName() { return name; }
    public double getAmount() { return amount; }
    public String getUnit() { return unit; }
    public String getRaw() { return raw; }
}
