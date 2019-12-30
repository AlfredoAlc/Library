package aar92_22.library.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import aar92_22.library.Activities.EditCategoryActivity;
import aar92_22.library.Activities.SettingsActivity;
import aar92_22.library.R;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        addPreferencesFromResource(R.xml.pref_main_activity);

        CheckBoxPreference category = findPreference(getString(R.string.check_first_category_list_key));
        if(category != null) {
            category.setVisible(false);
        }

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        PreferenceScreen preferenceScreen = getPreferenceScreen();

        int prefScreenCount = preferenceScreen.getPreferenceCount();



        for ( int i = 0; i < prefScreenCount; i++){
            Preference p = preferenceScreen.getPreference(i);
            if(!(p instanceof CheckBoxPreference)) {

                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);

            }

        }

        Preference libraryName = findPreference(getString(R.string.library_name_key));
        if(libraryName != null){
            libraryName.setOnPreferenceChangeListener(this);
        }

        Preference editCategory = findPreference(getString(R.string.edit_category_key));
        if(editCategory !=  null){
            editCategory.setOnPreferenceClickListener(this);
        }


    }



    private void setPreferenceSummary (Preference preference , String value){

        if(preference.getKey().equals(getString(R.string.edit_category_key))){
            preference.setSummary(getString(R.string.edit_category_summary));
        } else if(preference.getKey().equals("test_two_preference_key")){
            preference.setSummary("Test two preference active");
        }


        if(preference instanceof ListPreference){
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);
            if(prefIndex >=0){
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else if (preference instanceof EditTextPreference) {
            preference.setSummary(value);
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference preference = findPreference(key);
        if (null != preference) {

            if(!(preference instanceof CheckBoxPreference)) {

                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);

            }
        }




    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {


        return true;
    }



    @Override
    public void onDestroy() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference.getKey().equals(getString(R.string.edit_category_key))){
            Intent intent = new Intent(getContext(), EditCategoryActivity.class);
            startActivity(intent);
        }

        return true;
    }
}
