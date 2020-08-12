package aar92_22.library.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import aar92_22.library.Database.BookDataBase;
import aar92_22.library.Fragments.SettingsFragment;
import aar92_22.library.Interfaces.LibraryListener;
import aar92_22.library.R;
import aar92_22.library.Utilities.AppExecutors;
import aar92_22.library.Utilities.Dialogs;

public class SettingsActivity extends AppCompatActivity implements
        SettingsFragment.PreferenceClickListener, LibraryListener {


    BookDataBase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Load ad
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null ) actionBar.setDisplayHomeAsUpEnabled(true);

        mDb = BookDataBase.getsInstance(this);

        SettingsFragment settingsFragment = new SettingsFragment(this, this);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, settingsFragment).commit();

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onPause();
    }


    @Override
    public void preferenceClicked(String key) {
        if(key.equals(getString(R.string.edit_category_key))){
            Intent intent = new Intent(this, EditCategoryActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if(key.equals(getString(R.string.delete_library_key))){
            Dialogs.deleteLibraryConfirmationDialog(this, this);
        }

    }


    @Override
    public void deleteLibraryListener() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.clearAllTables();
            }
        });
    }
    @Override
    public void deleteBookListener() {}

}
