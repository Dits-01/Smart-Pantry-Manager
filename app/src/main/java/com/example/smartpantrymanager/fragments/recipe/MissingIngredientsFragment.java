package com.example.smartpantrymanager.fragments.recipe;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MissingIngredientsFragment extends Fragment {

    private LinearLayout layoutMissingRecipes;
    private List<RecipeEntity> allRecipes;
    private List<ShelfItem> allPantryItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_missing_ingredients, container, false);

        layoutMissingRecipes = view.findViewById(R.id.layout_missing_recipes);

        ShelfViewModel shelfViewModel = new ViewModelProvider(this).get(ShelfViewModel.class);

        shelfViewModel.getAllItems().observe(getViewLifecycleOwner(), items -> {
            allPantryItems = items;
            evaluateMissingIngredients();
        });

        shelfViewModel.getAllRecipes().observe(getViewLifecycleOwner(), recipes -> {
            allRecipes = recipes;
            evaluateMissingIngredients();
        });

        return view;
    }

    private void evaluateMissingIngredients() {
        if (allRecipes == null || allPantryItems == null || layoutMissingRecipes == null) return;

        Map<String, Double> pantryMap = new HashMap<>();
        for (ShelfItem item : allPantryItems) {
            pantryMap.put(item.getName().trim().toLowerCase(), item.getQuantity());
        }

        layoutMissingRecipes.removeAllViews();

        for (RecipeEntity recipe : allRecipes) {
            List<String> missingList = new ArrayList<>();
            List<RecipeIngredient> required = recipe.getRecipeIngredients();
            for (RecipeIngredient req : required) {
                Double availableQty = pantryMap.get(req.getName());
                double have = availableQty != null ? availableQty : 0.0;
                if (have < req.getAmount()) {
                    double needed = req.getAmount() - have;
                    String neededFmt = needed == (long) needed ? String.format(Locale.getDefault(), "%d", (long) needed) : String.valueOf(needed);
                    String haveFmt = have == (long) have ? String.format(Locale.getDefault(), "%d", (long) have) : String.valueOf(have);
                    missingList.add(neededFmt + "x " + req.getName() + (have > 0 ? " (have " + haveFmt + ")" : ""));
                }
            }

            if (!missingList.isEmpty()) {
                addMissingRecipeCard(recipe, missingList);
            }
        }

        if (layoutMissingRecipes.getChildCount() == 0) {
            TextView tv = new TextView(getContext());
            tv.setText(R.string.missing_ingredients_no_missing_message);
            layoutMissingRecipes.addView(tv);
        }
    }

    private void addMissingRecipeCard(RecipeEntity recipe, List<String> missingList) {
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

        TextView countView = new TextView(getContext());
        countView.setText(MessageFormat.format("{0}{1}{2}","Missing ", missingList.size(), " ingredient(s)"));
        countView.setTextSize(14);
        countView.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
        countView.setPadding(0, 4, 0, 0);

        StringBuilder sb = new StringBuilder("Missing items: ");
        for (int i = 0; i < missingList.size(); i++) {
            sb.append(missingList.get(i));
            if (i < missingList.size() - 1) sb.append(", ");
        }

        TextView detailsView = new TextView(getContext());
        detailsView.setText(sb.toString());
        detailsView.setTextSize(14);
        detailsView.setPadding(0, 4, 0, 0);

        innerLayout.addView(titleView);
        innerLayout.addView(countView);
        innerLayout.addView(detailsView);
        card.addView(innerLayout);

        card.setOnClickListener(v -> {
            RecipeDetailFragment detailFragment = new RecipeDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("recipe_id", recipe.getId());
            bundle.putString("title", recipe.getTitle());
            bundle.putString("time", recipe.getPrepTime());
            StringBuilder ingSb = new StringBuilder();
            for (RecipeIngredient ing : recipe.getRecipeIngredients()) {
                double amt = ing.getAmount();
                String amtFmt = amt == (long) amt ? String.format(Locale.getDefault(), "%d", (long) amt) : String.valueOf(amt);
                String u = ing.getUnit();
                ingSb.append("• ").append(amtFmt).append(u != null && !u.isEmpty() ? " " + u : "").append(" ").append(ing.getName()).append("\n");
            }
            bundle.putString("ingredients", ingSb.toString().trim());
            bundle.putString("recipe_ingredients_raw", recipe.getIngredients());
            bundle.putString("instructions", recipe.getInstructions());
            detailFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        layoutMissingRecipes.addView(card);
    }
}
