package aar92_22.library.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import aar92_22.library.Activities.EditCategoryActivity;
import aar92_22.library.AppExecutors;
import aar92_22.library.Database.AppDataBase;
import aar92_22.library.Database.BookEntry;
import aar92_22.library.R;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {
    AppDataBase mDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        mDb = AppDataBase.getsInstance(getActivity());
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

        Preference deleteLibrary = findPreference(getString(R.string.delete_library_key));
        if(deleteLibrary != null){
            deleteLibrary.setOnPreferenceClickListener(this);
        }


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
        if(preference.getKey().equals(getString(R.string.delete_library_key))){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.delete_library_message);
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    mDb.clearAllTables();
                                }
                            });

                            Toast.makeText(getContext(),getString(R.string.delete_library_confirmed), Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        }
                    });

            builder.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }


        return true;
    }
}
