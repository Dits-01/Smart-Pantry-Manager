package com.example.smartpantrymanager.fragments.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.smartpantrymanager.R;

public class SettingsFragment extends Fragment {
    private static final String PREF_NAME = "shelf_prefs";
    private static final String KEY_ACCENT = "accent_color";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_UNIT = "preferred_unit";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        RadioGroup radioGroupAccent = view.findViewById(R.id.radio_group_accent);
        RadioGroup radioGroupUnit = view.findViewById(R.id.radio_group_unit);
        SwitchCompat switchDarkMode = view.findViewById(R.id.switch_dark_mode);

        SharedPreferences prefs = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String currentAccent = prefs.getString(KEY_ACCENT, "blue");
        switch (currentAccent) {
            case "indigo":
                radioGroupAccent.check(R.id.radio_indigo);
                break;
            case "teal":
                radioGroupAccent.check(R.id.radio_teal);
                break;
            case "purple":
                radioGroupAccent.check(R.id.radio_purple);
                break;
            case "orange":
                radioGroupAccent.check(R.id.radio_orange);
                break;
            default:
                radioGroupAccent.check(R.id.radio_blue);
                break;
        }

        String currentUnitSys = prefs.getString(KEY_UNIT, "metric");
        if (currentUnitSys.equals("imperial")) {
            radioGroupUnit.check(R.id.radio_unit_imperial);
        } else {
            radioGroupUnit.check(R.id.radio_unit_metric);
        }

        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        switchDarkMode.setChecked(isDarkMode);

        radioGroupAccent.setOnCheckedChangeListener((group, checkedId) -> {
            String accent;
            if (checkedId == R.id.radio_indigo) {
                accent = "indigo";
            } else if (checkedId == R.id.radio_teal) {
                accent = "teal";
            } else if (checkedId == R.id.radio_purple) {
                accent = "purple";
            } else if (checkedId == R.id.radio_orange) {
                accent = "orange";
            } else {
                accent = "blue";
            }

            prefs.edit().putString(KEY_ACCENT, accent).apply();
            requireActivity().recreate();
        });

        radioGroupUnit.setOnCheckedChangeListener((group, checkedId) -> {
            String unitSys = (checkedId == R.id.radio_unit_imperial) ? "imperial" : "metric";
            prefs.edit().putString(KEY_UNIT, unitSys).apply();
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        return view;
    }
}
