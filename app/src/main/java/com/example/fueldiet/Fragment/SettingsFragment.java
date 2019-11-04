package com.example.fueldiet.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.fueldiet.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        updateDefaultLang();
        //other conf
    }

    private void updateDefaultLang() {
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        boolean overrideLang = prefs.getBoolean("enable_language", false);
        if (!overrideLang) {
            String langCode = getContext().getResources().getConfiguration().getLocales().get(0).getLanguage();
            ListPreference choseLang = findPreference("language_select");
            switch (langCode) {
                case "sl":
                    editor.putString("language_select", "slovene").apply();
                    choseLang.setValueIndex(1);
                    break;
                default:
                    editor.putString("language_select", "english").apply();
                    choseLang.setValueIndex(0);
                    break;
            }
        }
    }
}