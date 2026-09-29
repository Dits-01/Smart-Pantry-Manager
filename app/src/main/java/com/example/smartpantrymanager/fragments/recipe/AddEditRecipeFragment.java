package com.example.smartpantrymanager.fragments.recipe;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.RecipeEntity;
import com.example.smartpantrymanager.model.RecipeIngredient;
import com.example.smartpantrymanager.viewmodel.ShelfViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddEditRecipeFragment extends Fragment {

    private ShelfViewModel shelfViewModel;
    private TextInputEditText editTextTitle, editTextPrepTime, editTextIngredientName, editTextIngredientAmount, editTextInstructions;
    private Spinner spinnerRecipeIngredientUnit;
    private TextView textHeader;
    private LinearLayout layoutIngredientsList;

    private boolean isEditMode = false;
    private long recipeId = -1;
    private final List<RecipeIngredient> recipeIngredients = new ArrayList<>();

    private static final String[] UNITS = {"", "ml", "L", "cup", "tbsp", "tsp", "g", "kg", "oz", "lb", "pcs"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_recipe, container, false);

        shelfViewModel = new ViewModelProvider(this).get(ShelfViewModel.class);

        textHeader = view.findViewById(R.id.text_recipe_header_title);
        editTextTitle = view.findViewById(R.id.edit_text_recipe_title);
        editTextPrepTime = view.findViewById(R.id.edit_text_recipe_prep_time);
        editTextIngredientName = view.findViewById(R.id.edit_text_ingredient_name);
        editTextIngredientAmount = view.findViewById(R.id.edit_text_ingredient_amount);
        editTextInstructions = view.findViewById(R.id.edit_text_recipe_instructions);
        spinnerRecipeIngredientUnit = view.findViewById(R.id.spinner_recipe_ingredient_unit);
        layoutIngredientsList = view.findViewById(R.id.layout_recipe_ingredients_list);
        Button buttonAddIngredient = view.findViewById(R.id.button_add_ingredient_item);
        Button buttonSave = view.findViewById(R.id.button_save_recipe);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, UNITS
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRecipeIngredientUnit.setAdapter(adapter);

        if (getArguments() != null) {
            recipeId = getArguments().getLong("recipe_id", -1);
            if (recipeId != -1) {
                isEditMode = true;
                if (textHeader != null) {
                    textHeader.setText(R.string.add_edit_recipe_title_edit);
                }
                editTextTitle.setText(getArguments().getString("recipe_title", ""));
                editTextPrepTime.setText(getArguments().getString("recipe_time", ""));
                editTextInstructions.setText(getArguments().getString("recipe_instructions", ""));

                String rawIngs = getArguments().getString("recipe_ingredients_raw", "");
                if (!rawIngs.isEmpty()) {
                    for (String p : rawIngs.split(",")) {
                        RecipeIngredient ri = new RecipeIngredient(p.trim());
                        if (!ri.getName().isEmpty()) {
                            recipeIngredients.add(ri);
                        }
                    }
                }
                buttonSave.setText(R.string.add_edit_recipe_title_update);
            }
        }

        buttonAddIngredient.setOnClickListener(v -> addIngredientItem());
        buttonSave.setOnClickListener(v -> saveRecipe());

        renderIngredientsList();

        return view;
    }

    private void addIngredientItem() {
        String name = editTextIngredientName.getText() != null ? editTextIngredientName.getText().toString().trim() : "";
        String amountStr = editTextIngredientAmount.getText() != null ? editTextIngredientAmount.getText().toString().trim() : "";
        String unit = spinnerRecipeIngredientUnit.getSelectedItem() != null ? spinnerRecipeIngredientUnit.getSelectedItem().toString() : "";

        if (TextUtils.isEmpty(name)) {
            editTextIngredientName.setError("Enter ingredient name");
            return;
        }

        double amount = 1.0;
        if (!TextUtils.isEmpty(amountStr)) {
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                amount = 1.0;
            }
        }

        String combined = amount + (unit.isEmpty() ? "" : " " + unit) + " " + name;
        recipeIngredients.add(new RecipeIngredient(combined));

        editTextIngredientName.setText("");
        editTextIngredientAmount.setText("");
        spinnerRecipeIngredientUnit.setSelection(0);
        editTextIngredientName.requestFocus();

        renderIngredientsList();
    }

    private void renderIngredientsList() {
        if (layoutIngredientsList == null || getContext() == null) return;
        layoutIngredientsList.removeAllViews();

        if (recipeIngredients.isEmpty()) {
            TextView emptyTv = new TextView(getContext());
            emptyTv.setText(R.string.add_edit_recipe_no_ingrdient_message);
            emptyTv.setPadding(8, 8, 8, 8);
            layoutIngredientsList.addView(emptyTv);
            return;
        }

        for (int i = 0; i < recipeIngredients.size(); i++) {
            final int index = i;
            RecipeIngredient ri = recipeIngredients.get(i);

            View row = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, layoutIngredientsList, false);
            TextView text1 = row.findViewById(android.R.id.text1);
            TextView text2 = row.findViewById(android.R.id.text2);

            text1.setText(ri.getName());
            double amt = ri.getAmount();
            String amtFormatted = amt == (long) amt ? String.format(Locale.getDefault(), "%d", (long) amt) : String.valueOf(amt);
            String unitStr = ri.getUnit() != null && !ri.getUnit().isEmpty() ? " " + ri.getUnit() : "";
            text2.setText(MessageFormat.format("Amount: {0}{1} (Tap to remove)", amtFormatted, unitStr));

            row.setOnClickListener(v -> {
                recipeIngredients.remove(index);
                renderIngredientsList();
            });

            layoutIngredientsList.addView(row);
        }
    }

    private void saveRecipe() {
        String title = editTextTitle.getText() != null ? editTextTitle.getText().toString().trim() : "";
        String prepTime = editTextPrepTime.getText() != null ? editTextPrepTime.getText().toString().trim() : "";
        String instructions = editTextInstructions.getText() != null ? editTextInstructions.getText().toString().trim() : "";

        if (TextUtils.isEmpty(title)) {
            editTextTitle.setError("Please enter recipe title");
            return;
        }
        if (TextUtils.isEmpty(prepTime)) {
            editTextPrepTime.setError("Please enter prep time");
            return;
        }
        if (recipeIngredients.isEmpty()) {
            Toast.makeText(getContext(), "Please add at least one ingredient", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(instructions)) {
            editTextInstructions.setError("Please enter instructions");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < recipeIngredients.size(); i++) {
            RecipeIngredient ri = recipeIngredients.get(i);
            double amt = ri.getAmount();
            String amtFormatted = amt == (long) amt ? String.format(Locale.getDefault(), "%d", (long) amt) : String.valueOf(amt);
            String u = ri.getUnit();
            sb.append(amtFormatted).append(u != null && !u.isEmpty() ? " " + u : "").append(" ").append(ri.getName());
            if (i < recipeIngredients.size() - 1) {
                sb.append(", ");
            }
        }
        String ingredientsCsv = sb.toString();

        RecipeEntity recipe = new RecipeEntity(title, prepTime, ingredientsCsv, instructions);
        if (isEditMode) {
            recipe.setId(recipeId);
            shelfViewModel.updateRecipe(recipe);
            Toast.makeText(getContext(), "Recipe Updated!", Toast.LENGTH_SHORT).show();
        } else {
            shelfViewModel.insertRecipe(recipe);
            Toast.makeText(getContext(), "Recipe Saved!", Toast.LENGTH_SHORT).show();
        }

        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        }
    }
}
