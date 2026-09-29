package com.example.smartpantrymanager.fragments.pantry;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.adapter.ShelfAdapter;
import com.example.smartpantrymanager.model.ShelfItem;
import com.example.smartpantrymanager.viewmodel.ShelfViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class PantryListFragment extends Fragment {
    private ShelfViewModel shelfViewModel;
    private ShelfAdapter adapter;
    private TextView emptyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pantry_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_shelf);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        emptyView = view.findViewById(R.id.text_empty_view);
        EditText editTextSearch = view.findViewById(R.id.edit_text_search);

        adapter = new ShelfAdapter();
        recyclerView.setAdapter(adapter);

        shelfViewModel = new ViewModelProvider(this).get(ShelfViewModel.class);
        shelfViewModel.getAllItems().observe(getViewLifecycleOwner(), items -> {
            adapter.submitList(items);
            if (items == null || items.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
        });

        adapter.setOnItemClickListener(new ShelfAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ShelfItem item) {
                AddEditIngredientFragment editFragment = new AddEditIngredientFragment();
                Bundle args = new Bundle();
                args.putLong("item_id", item.getId());
                args.putString("item_name", item.getName());
                args.putString("item_category", item.getCategory());
                args.putDouble("item_quantity", item.getQuantity());
                args.putString("item_unit", item.getUnit());
                args.putString("item_location", item.getShelfLocation());
                args.putString("item_notes", item.getNotes());
                editFragment.setArguments(args);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, editFragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onDeleteClick(ShelfItem item) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Delete Ingredient")
                        .setMessage("Are you sure you want to delete \"" + item.getName() + "\"?")
                        .setPositiveButton("Delete", (dialog, which) -> shelfViewModel.delete(item))
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    shelfViewModel.getAllItems().observe(getViewLifecycleOwner(), items -> adapter.submitList(items));
                } else {
                    shelfViewModel.searchItems(query).observe(getViewLifecycleOwner(), items -> adapter.submitList(items));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }
}
