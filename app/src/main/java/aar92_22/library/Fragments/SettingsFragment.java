package aar92_22.library.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import aar92_22.library.R;
import aar92_22.library.Utilities.PreferenceUtilities;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener{


    private PreferenceUtilities preferenceUtilities;

    Context context;
    PreferenceClickListener preferenceClickListener;


    public SettingsFragment(Context context, PreferenceClickListener preferenceClickListener){
        this.context = context;
        this.preferenceClickListener = preferenceClickListener;
        preferenceUtilities = new PreferenceUtilities(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_main_activity);

        SharedPreferences sharedPreferences = preferenceUtilities.sharedPreferencesChange();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int prefScreenCount = preferenceScreen.getPreferenceCount();

        for (int i = 0; i < prefScreenCount; i++){
            Preference p = preferenceScreen.getPreference(i);
            String value = sharedPreferences.getString(p.getKey(), "");
            setPreferenceSummary(p, value);
        }

        Preference editCategory = findPreference(getString(R.string.edit_category_key));
        if(editCategory !=  null){
            editCategory.setOnPreferenceClickListener(this);
        }

        Preference deleteLibrary = findPreference(getString(R.string.delete_library_key));
        if(deleteLibrary != null){
            deleteLibrary.setOnPreferenceClickListener(this);
        }


    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference != null) {
            String value = sharedPreferences.getString(preference.getKey(), "");
            setPreferenceSummary(preference, value);
        }
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        preferenceClickListener.preferenceClicked(preference.getKey());
        return true;
    }



    @Override
    public void onDestroy() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }




    private void setPreferenceSummary (Preference preference , String value){

        if(preference.getKey().equals(getString(R.string.edit_category_key))){
            preference.setSummary(getString(R.string.edit_category_summary));
        }
        if(preference.getKey().equals(getString(R.string.delete_library_key))){
            preference.setSummary(getString(R.string.delete_library_summary));
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


    public interface PreferenceClickListener{
        void preferenceClicked(String key);
    }

}
