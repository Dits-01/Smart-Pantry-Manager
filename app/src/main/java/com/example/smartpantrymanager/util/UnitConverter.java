package com.example.smartpantrymanager.util;

import java.util.HashMap;
import java.util.Map;

public class UnitConverter {
    private static final Map<String, Double> volumeToMl = new HashMap<>();
    private static final Map<String, Double> weightToG = new HashMap<>();

    static {
        volumeToMl.put("ml", 1.0);
        volumeToMl.put("l", 1000.0);
        volumeToMl.put("liter", 1000.0);
        volumeToMl.put("liters", 1000.0);
        volumeToMl.put("cup", 240.0);
        volumeToMl.put("cups", 240.0);
        volumeToMl.put("tbsp", 15.0);
        volumeToMl.put("tablespoon", 15.0);
        volumeToMl.put("tablespoons", 15.0);
        volumeToMl.put("tsp", 5.0);
        volumeToMl.put("teaspoon", 5.0);
        volumeToMl.put("teaspoons", 5.0);
        volumeToMl.put("floz", 29.5735);

        weightToG.put("g", 1.0);
        weightToG.put("gram", 1.0);
        weightToG.put("grams", 1.0);
        weightToG.put("kg", 1000.0);
        weightToG.put("kilogram", 1000.0);
        weightToG.put("kilograms", 1000.0);
        weightToG.put("oz", 28.3495);
        weightToG.put("ounce", 28.3495);
        weightToG.put("ounces", 28.3495);
        weightToG.put("lb", 453.592);
        weightToG.put("lbs", 453.592);
        weightToG.put("pound", 453.592);
        weightToG.put("pounds", 453.592);
    }

    public static double convert(double amount, String fromUnit, String toUnit) {
        if (fromUnit == null || toUnit == null || fromUnit.equalsIgnoreCase(toUnit)) {
            return amount;
        }
        String f = fromUnit.trim().toLowerCase();
        String t = toUnit.trim().toLowerCase();

        if (volumeToMl.containsKey(f) && volumeToMl.containsKey(t)) {
            Double fVal = volumeToMl.get(f);
            Double tVal = volumeToMl.get(t);
            if (fVal != null && tVal != null && tVal != 0.0) {
                double ml = amount * fVal;
                return ml / tVal;
            }
        }

        if (weightToG.containsKey(f) && weightToG.containsKey(t)) {
            Double fVal = weightToG.get(f);
            Double tVal = weightToG.get(t);
            if (fVal != null && tVal != null && tVal != 0.0) {
                double g = amount * fVal;
                return g / tVal;
            }
        }

        return amount;
    }
}
