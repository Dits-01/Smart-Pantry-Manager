package com.example.smartpantrymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.smartpantrymanager.fragments.pantry.AddEditIngredientFragment;
import com.example.smartpantrymanager.fragments.pantry.PantryListFragment;
import com.example.smartpantrymanager.fragments.recipe.SuggestedRecipesFragment;
import com.example.smartpantrymanager.fragments.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("shelf_prefs", Context.MODE_PRIVATE);
        String accent = prefs.getString("accent_color", "blue");
        switch (accent) {
            case "indigo":
                setTheme(R.style.Theme_SmartPantryManager_Indigo);
                break;
            case "teal":
                setTheme(R.style.Theme_SmartPantryManager_Teal);
                break;
            case "purple":
                setTheme(R.style.Theme_SmartPantryManager_Purple);
                break;
            case "orange":
                setTheme(R.style.Theme_SmartPantryManager_Orange);
                break;
            default:
                setTheme(R.style.Theme_SmartPantryManager);
                break;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_pantry_list) {
                selectedFragment = new PantryListFragment();
            } else if (itemId == R.id.navigation_add_ingredient) {
                selectedFragment = new AddEditIngredientFragment();
            } else if (itemId == R.id.navigation_recipes) {
                selectedFragment = new SuggestedRecipesFragment();
            } else if (itemId == R.id.navigation_settings) {
                selectedFragment = new SettingsFragment();
            } else {
                return false;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.navigation_pantry_list);
        }
    }
}
