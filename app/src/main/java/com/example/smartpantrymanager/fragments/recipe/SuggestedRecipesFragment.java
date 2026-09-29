package com.example.smartpantrymanager.fragments.recipe;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.RecipeEntity;
import com.example.smartpantrymanager.model.RecipeIngredient;
import com.example.smartpantrymanager.model.ShelfItem;
import com.example.smartpantrymanager.viewmodel.ShelfViewModel;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SuggestedRecipesFragment extends Fragment {
    private LinearLayout layoutStrict;
    private List<RecipeEntity> allRecipes;
    private List<ShelfItem> allPantryItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suggested_recipes, container, false);

        layoutStrict = view.findViewById(R.id.layout_strict_recipes);
        Button buttonViewMissing = view.findViewById(R.id.button_view_missing);
        Button buttonAddRecipe = view.findViewById(R.id.button_add_recipe);

        buttonViewMissing.setOnClickListener(v -> {
            MissingIngredientsFragment missingFragment = new MissingIngredientsFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, missingFragment)
                    .addToBackStack(null)
                    .commit();
        });

        buttonAddRecipe.setOnClickListener(v -> {
            AddEditRecipeFragment addRecipeFragment = new AddEditRecipeFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, addRecipeFragment)
                    .addToBackStack(null)
                    .commit();
        });

        ShelfViewModel shelfViewModel = new ViewModelProvider(this).get(ShelfViewModel.class);

        shelfViewModel.getAllItems().observe(getViewLifecycleOwner(), items -> {
            allPantryItems = items;
            evaluateRecipeMatches();
        });

        shelfViewModel.getAllRecipes().observe(getViewLifecycleOwner(), recipes -> {
            allRecipes = recipes;
            evaluateRecipeMatches();
        });

        return view;
    }

    private void evaluateRecipeMatches() {
        if (allRecipes == null || allPantryItems == null || layoutStrict == null) return;

        Map<String, Double> pantryMap = new HashMap<>();
        for (ShelfItem item : allPantryItems) {
            pantryMap.put(item.getName().trim().toLowerCase(), item.getQuantity());
        }

        layoutStrict.removeAllViews();

        for (RecipeEntity recipe : allRecipes) {
            boolean hasAll = true;
            List<RecipeIngredient> required = recipe.getRecipeIngredients();
            for (RecipeIngredient req : required) {
                Double availableQty = pantryMap.get(req.getName());
                if (availableQty == null || availableQty < req.getAmount()) {
                    hasAll = false;
                    break;
                }
            }

            if (hasAll) {
                addRecipeCard(recipe, layoutStrict);
            }
        }

        if (layoutStrict.getChildCount() == 0) {
            TextView tv = new TextView(getContext());
            tv.setText(R.string.suggested_recipes_no_recipe_message);
            layoutStrict.addView(tv);
        }
    }

    private void addRecipeCard(RecipeEntity recipe, LinearLayout parentLayout) {
        if (getContext() == null) return;

        CardView card = new CardView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        card.setLayoutParams(params);
        card.setRadius(12);
        card.setCardElevation(3);
        card.setClickable(true);
        card.setFocusable(true);

        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        card.setForeground(AppCompatResources.getDrawable(getContext(), outValue.resourceId));

        LinearLayout innerLayout = new LinearLayout(getContext());
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        innerLayout.setPadding(16, 16, 16, 16);

        TextView titleView = new TextView(getContext());
        titleView.setText(recipe.getTitle());
        titleView.setTextSize(18);
        titleView.setTypeface(null, Typeface.BOLD);

        TextView subView = new TextView(getContext());
        subView.setText(MessageFormat.format("{0}{1} • {2}", "Prep: ", recipe.getPrepTime(), "All ingredients available"));
        subView.setTextSize(14);
        subView.setTextColor(getResources().getColor(android.R.color.darker_gray, null));
        subView.setPadding(0, 4, 0, 0);

        innerLayout.addView(titleView);
        innerLayout.addView(subView);
        card.addView(innerLayout);

        card.setOnClickListener(v -> {
            RecipeDetailFragment detailFragment = new RecipeDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("recipe_id", recipe.getId());
            bundle.putString("title", recipe.getTitle());
            bundle.putString("time", recipe.getPrepTime());
            StringBuilder sb = new StringBuilder();
            for (RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
                double amount = recipeIngredient.getAmount();
                String amountFormatAmount = amount == (long) amount ? String.format(Locale.getDefault(), "%d", (long) amount) : String.valueOf(amount);
                String unit = recipeIngredient.getUnit();
                sb.append("• ").append(amountFormatAmount).append(unit != null && !unit.isEmpty() ? " " + unit : "").append(" ").append(recipeIngredient.getName()).append("\n");
            }
            bundle.putString("ingredients", sb.toString().trim());
            bundle.putString("recipe_ingredients_raw", recipe.getIngredients());
            bundle.putString("instructions", recipe.getInstructions());
            detailFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        parentLayout.addView(card);
    }
}
