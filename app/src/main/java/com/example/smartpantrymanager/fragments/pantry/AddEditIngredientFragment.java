package com.example.smartpantrymanager.fragments.pantry;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.ShelfItem;
import com.example.smartpantrymanager.util.UnitConverter;
import com.example.smartpantrymanager.viewmodel.ShelfViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class AddEditIngredientFragment extends Fragment {
    private ShelfViewModel shelfViewModel;
    private TextInputEditText editTextName, editTextCategory, editTextQuantity, editTextLocation, editTextNotes;
    private Spinner spinnerUnit;
    private boolean isEditMode = false;
    private long itemId = -1;
    private static final String[] UNITS = {"", "ml", "L", "cup", "tbsp", "tsp", "g", "kg", "oz", "lb", "pcs"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_ingredient, container, false);

        shelfViewModel = new ViewModelProvider(this).get(ShelfViewModel.class);

        TextView textHeader = view.findViewById(R.id.text_header_title);
        editTextName = view.findViewById(R.id.edit_text_item_name);
        editTextCategory = view.findViewById(R.id.edit_text_item_category);
        editTextQuantity = view.findViewById(R.id.edit_text_item_quantity);
        editTextLocation = view.findViewById(R.id.edit_text_item_location);
        editTextNotes = view.findViewById(R.id.edit_text_item_notes);
        spinnerUnit = view.findViewById(R.id.spinner_item_unit);
        Button buttonSave = view.findViewById(R.id.button_save_item);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, UNITS
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(adapter);

        if (getArguments() != null) {
            itemId = getArguments().getLong("item_id", -1);
            if (itemId != -1) {
                isEditMode = true;
                if (textHeader != null) {
                    textHeader.setText(R.string.add_edit_ingredient_title_edit);
                }
                editTextName.setText(getArguments().getString("item_name", ""));
                editTextCategory.setText(getArguments().getString("item_category", ""));
                double q = getArguments().getDouble("item_quantity", 0.0);
                editTextQuantity.setText(q == (long) q ? String.format(Locale.getDefault(), "%d", (long) q) : String.valueOf(q));
                editTextLocation.setText(getArguments().getString("item_location", ""));
                editTextNotes.setText(getArguments().getString("item_notes", ""));

                String unit = getArguments().getString("item_unit", "");
                for (int i = 0; i < UNITS.length; i++) {
                    if (UNITS[i].equalsIgnoreCase(unit)) {
                        spinnerUnit.setSelection(i);
                        break;
                    }
                }

                buttonSave.setText(R.string.add_edit_ingredient_button_update);
            }
        }

        buttonSave.setOnClickListener(v -> saveItem());

        return view;
    }

    private void saveItem() {
        String name = editTextName.getText() != null ? editTextName.getText().toString().trim() : "";
        String category = editTextCategory.getText() != null ? editTextCategory.getText().toString().trim() : "";
        String quantityStr = editTextQuantity.getText() != null ? editTextQuantity.getText().toString().trim() : "";
        String location = editTextLocation.getText() != null ? editTextLocation.getText().toString().trim() : "";
        String notes = editTextNotes.getText() != null ? editTextNotes.getText().toString().trim() : "";
        String unit = spinnerUnit.getSelectedItem() != null ? spinnerUnit.getSelectedItem().toString() : "";

        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Please enter ingredient name");
            return;
        }
        if (TextUtils.isEmpty(category)) {
            editTextCategory.setError("Please enter category");
            return;
        }
        if (TextUtils.isEmpty(quantityStr)) {
            editTextQuantity.setError("Please enter quantity");
            return;
        }
        if (TextUtils.isEmpty(location)) {
            editTextLocation.setError("Please enter pantry shelf location");
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(quantityStr);
        } catch (NumberFormatException e) {
            editTextQuantity.setError("Invalid quantity number");
            return;
        }

        SharedPreferences prefs = requireActivity().getSharedPreferences("shelf_prefs", Context.MODE_PRIVATE);
        String prefUnitSys = prefs.getString("preferred_unit", "metric");
        if (prefUnitSys.equals("metric")) {
            if (unit.equalsIgnoreCase("oz")) {
                quantity = UnitConverter.convert(quantity, "oz", "g");
                unit = "g";
            } else if (unit.equalsIgnoreCase("lb")) {
                quantity = UnitConverter.convert(quantity, "lb", "g");
                unit = "g";
            } else if (unit.equalsIgnoreCase("cup") || unit.equalsIgnoreCase("cups")) {
                quantity = UnitConverter.convert(quantity, "cup", "ml");
                unit = "ml";
            } else if (unit.equalsIgnoreCase("floz")) {
                quantity = UnitConverter.convert(quantity, "floz", "ml");
                unit = "ml";
            }
        } else {
            if (unit.equalsIgnoreCase("g") && quantity >= 453.592) {
                quantity = UnitConverter.convert(quantity, "g", "lb");
                unit = "lb";
            } else if (unit.equalsIgnoreCase("ml") && quantity >= 240) {
                quantity = UnitConverter.convert(quantity, "ml", "cup");
                unit = "cup";
            }
        }

        ShelfItem item = new ShelfItem(name, category, quantity, unit, location, notes);
        if (isEditMode) {
            item.setId(itemId);
            shelfViewModel.update(item);
            Toast.makeText(getContext(), "Ingredient updated in pantry!", Toast.LENGTH_SHORT).show();
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        } else {
            shelfViewModel.insert(item);
            Toast.makeText(getContext(), "Ingredient saved to pantry!", Toast.LENGTH_SHORT).show();
            editTextName.setText("");
            editTextCategory.setText("");
            editTextQuantity.setText("");
            editTextLocation.setText("");
            editTextNotes.setText("");
            spinnerUnit.setSelection(0);
        }
    }
}
