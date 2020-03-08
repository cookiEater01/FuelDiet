package com.fueldiet.fueldiet.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.fueldiet.fueldiet.R;

import java.util.Map;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        updateDefaultLang();
        //other conf

        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        Map<String, ?> all = prefs.getAll();
        String vehicleName = prefs.getString("selected_vehicle_name", "No vehicle selected");
        Preference selectedVehicle = findPreference("selected_vehicle");
        selectedVehicle.setTitle(vehicleName);
        Log.e("SettingFragment", vehicleName);

    }

    private void updateDefaultLang() {
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        boolean overrideLang = prefs.getBoolean("enable_language", false);
        if (!overrideLang) {
            String langCode = getContext().getResources().getConfiguration().getLocales().get(0).getLanguage();
            ListPreference choseLang = findPreference("language_select");
            if ("sl".equals(langCode)) {
                editor.putString("language_select", "slovene").apply();
                choseLang.setValueIndex(1);
            } else {
                editor.putString("language_select", "english").apply();
                choseLang.setValueIndex(0);
            }
        }
    }
}