package com.example.smartpantrymanager.fragments.recipe;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.RecipeEntity;
import com.example.smartpantrymanager.model.RecipeIngredient;
import com.example.smartpantrymanager.model.ShelfItem;
import com.example.smartpantrymanager.viewmodel.ShelfViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class RecipeDetailFragment extends Fragment {
    private ShelfViewModel shelfViewModel;
    private long recipeId = -1;
    private String recipeTitle = "";
    private String recipeTime = "";
    private String recipeIngredientsRaw = "";
    private String recipeInstructions = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context = getContext();
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        shelfViewModel = new ViewModelProvider(this).get(ShelfViewModel.class);

        TextView textTitle = view.findViewById(R.id.text_recipe_title);
        TextView textTime = view.findViewById(R.id.text_recipe_time);
        TextView textIngredients = view.findViewById(R.id.text_recipe_ingredients);
        TextView textInstructions = view.findViewById(R.id.text_recipe_instructions);
        MaterialButton buttonRemoveIngredients = view.findViewById(R.id.btn_remove_ingredients);
        MaterialButton buttonEditRecipe = view.findViewById(R.id.btn_edit_recipe);
        MaterialButton buttonDeleteRecipe = view.findViewById(R.id.btn_delete_recipe);

        if (getArguments() != null && context != null) {
            recipeId = getArguments().getLong("recipe_id", -1);
            recipeTitle = getArguments().getString("title", "Recipe Title");
            recipeTime = getArguments().getString("time", "- mins");
            String ingredientsFormatted = getArguments().getString("ingredients", "");
            recipeIngredientsRaw = getArguments().getString("recipe_ingredients_raw", "");
            recipeInstructions = getArguments().getString("instructions", "");

            textTitle.setText(recipeTitle);
            textTime.setText(MessageFormat.format("{0}{1}", context.getString(R.string.recipe_detail_prep_time), recipeTime));
            textIngredients.setText(ingredientsFormatted);
            textInstructions.setText(recipeInstructions);

            buttonRemoveIngredients.setOnClickListener(v -> {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Remove Ingredients")
                        .setMessage("Are you sure you want to remove the required ingredients for this recipe from your pantry?")
                        .setPositiveButton("Remove", (dialog, which) -> removeIngredients())
                        .setNegativeButton("Cancel", null)
                        .show();
            });

            buttonEditRecipe.setOnClickListener(v -> editRecipe());
            buttonDeleteRecipe.setOnClickListener(v -> deleteRecipe());
        }

        return view;
    }

    private void editRecipe() {
        if (recipeId == -1) return;
        AddEditRecipeFragment editFragment = new AddEditRecipeFragment();
        Bundle args = new Bundle();
        args.putLong("recipe_id", recipeId);
        args.putString("recipe_title", recipeTitle);
        args.putString("recipe_time", recipeTime);
        args.putString("recipe_ingredients_raw", recipeIngredientsRaw);
        args.putString("recipe_instructions", recipeInstructions);
        editFragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editFragment)
                .addToBackStack(null)
                .commit();
    }

    private void deleteRecipe() {
        if (recipeId == -1) return;
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.recipe_detail_button_delete)
                .setMessage("Are you sure you want to delete \"" + recipeTitle + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    RecipeEntity recipeEntity = new RecipeEntity(recipeTitle, recipeTime, recipeIngredientsRaw, recipeInstructions);
                    recipeEntity.setId(recipeId);
                    shelfViewModel.deleteRecipe(recipeEntity);
                    Toast.makeText(getContext(), "Recipe deleted", Toast.LENGTH_SHORT).show();
                    if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                        getParentFragmentManager().popBackStack();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void removeIngredients() {
        if (recipeIngredientsRaw == null || recipeIngredientsRaw.isEmpty()) {
            Toast.makeText(getContext(), "No ingredients to remove", Toast.LENGTH_SHORT).show();
            return;
        }

        List<RecipeIngredient> required = new ArrayList<>();
        for (String p : recipeIngredientsRaw.split(",")) {
            RecipeIngredient ri = new RecipeIngredient(p.trim());
            if (!ri.getName().isEmpty()) {
                required.add(ri);
            }
        }

        shelfViewModel.getAllItems().observe(getViewLifecycleOwner(), pantryItems -> {
            if (pantryItems != null) {
                for (RecipeIngredient req : required) {
                    String reqName = req.getName();
                    double reqAmount = req.getAmount();
                    for (ShelfItem item : pantryItems) {
                        if (item.getName().trim().toLowerCase().equals(reqName)) {
                            double newQty = item.getQuantity() - reqAmount;
                            if (newQty > 0) {
                                item.setQuantity(newQty);
                                shelfViewModel.update(item);
                            } else {
                                shelfViewModel.delete(item);
                            }
                        }
                    }
                }
                Toast.makeText(getContext(), "Required ingredients removed from pantry!", Toast.LENGTH_SHORT).show();
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                }
            }
        });
    }
}
