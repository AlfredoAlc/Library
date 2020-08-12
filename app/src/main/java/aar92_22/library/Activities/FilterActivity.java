package aar92_22.library.Activities;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.lifecycle.Observer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import aar92_22.library.Database.BookEntry;
import aar92_22.library.R;
import aar92_22.library.Utilities.PreferenceUtilities;
import aar92_22.library.ViewModel.MainViewModel;

import static aar92_22.library.Utilities.PreferenceUtilities.AUTHOR_FILTER_ACTIVATED;
import static aar92_22.library.Utilities.PreferenceUtilities.CATEGORY_FILTER_ACTIVATED;
import static aar92_22.library.Utilities.PreferenceUtilities.SERIES_FILTER_ACTIVATED;

public class FilterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {




    private Spinner mAuthor;
    private Spinner mCategory;
    private Spinner mSeries;

    private List<String> authorList;
    private List<String> categoryList;
    private List<String> seriesList;

    private String authorFilter;
    private String categoryFilter;
    private String seriesFilter;

    private Boolean isAuthorFilter;
    private Boolean isCategoryFilter;
    private Boolean isSeriesFilter;

    private int positionAuthor;
    private int positionCategory;
    private int positionSeries;

    PreferenceUtilities preferenceUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        //Load ad
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null ) actionBar.setDisplayHomeAsUpEnabled(true);

        mAuthor = findViewById(R.id.author_spinner);
        mCategory = findViewById(R.id.category_spinner);
        mSeries = findViewById(R.id.series_spinner);

        preferenceUtilities = new PreferenceUtilities(this);
        preferenceUtilities.sharedPreferencesChange().registerOnSharedPreferenceChangeListener(this);

        isAuthorFilter = preferenceUtilities.isAuthorFilter();
        isCategoryFilter = preferenceUtilities.isCategoryFilter();
        isSeriesFilter = preferenceUtilities.isSeriesFilter();

        authorFilter = preferenceUtilities.getAuthorFilter();
        categoryFilter = preferenceUtilities.getCategoryFilter();
        seriesFilter = preferenceUtilities.getSeriesFilter();

        setUpViewModel();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.filter_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                returnResult();
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_refresh:
                mAuthor.setSelection(0);
                mCategory.setSelection(0);
                mSeries.setSelection(0);
                return true;

            default: return super.onOptionsItemSelected(item);

        }



    }


    @Override
    public void onBackPressed() {
        returnResult();
        NavUtils.navigateUpFromSameTask(this);
    }
    @Override
    protected void onPause() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        preferenceUtilities.sharedPreferencesChange().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(parent.equals(mAuthor)){
            if(position > 0) {
                preferenceUtilities.setAuthorFilterActivated(true);
                preferenceUtilities.setAuthorFilter(parent.getItemAtPosition(position).toString());
            } else if(position == 0){
                preferenceUtilities.setAuthorFilterActivated(false);
            }
        } else if(parent.equals(mCategory)){
            if(position > 0) {
                preferenceUtilities.setCategoryFilterActivated(true);
                preferenceUtilities.setCategoryFilter(parent.getItemAtPosition(position).toString());
            } else if(position == 0){
                preferenceUtilities.setCategoryFilterActivated(false);
            }
        } else if(parent.equals(mSeries)){
            if(position > 0) {
                preferenceUtilities.setSeriesFilterActivated(true);
                preferenceUtilities.setSeriesFilter(parent.getItemAtPosition(position).toString());
            } else if(position == 0){
                preferenceUtilities.setSeriesFilterActivated(false);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String preferenceKey) {
        switch (preferenceKey){
            case AUTHOR_FILTER_ACTIVATED:
                isAuthorFilter = preferenceUtilities.isAuthorFilter();
                break;

            case CATEGORY_FILTER_ACTIVATED:
                isCategoryFilter = preferenceUtilities.isCategoryFilter();
                break;

            case SERIES_FILTER_ACTIVATED:
                isSeriesFilter = preferenceUtilities.isSeriesFilter();
                break;
        }


    }

    private void setUpViewModel(){
        MainViewModel viewModel = new MainViewModel(getApplication(),getString(R.string.title));
        viewModel.getBooks().observe(this, new Observer<List<BookEntry>>() {
            @Override
            public void onChanged(@Nullable List<BookEntry> bookEntries) {

                authorList = new ArrayList<>();
                authorList.add("All");

                categoryList = new ArrayList<>();
                categoryList.add("All");

                seriesList = new ArrayList<>();
                seriesList.add("All");

                if(bookEntries != null) {
                    for (int i = 0; i < bookEntries.size(); i++) {
                        BookEntry entry = bookEntries.get(i);

                        if(!(entry.getLastName().equals(""))) {
                            boolean authorDuplicated = false;

                            for (int j = 0; j < authorList.size(); j++) {

                                if (entry.getLastName().equals(authorList.get(j))) {
                                    authorDuplicated = true;
                                    break;
                                }
                            }

                            if(!authorDuplicated){
                                authorList.add(entry.getLastName());
                            }

                        }


                        boolean categoryDuplicated = false;

                        for (int j = 0; j < categoryList.size(); j++){

                            if(entry.getCategory().equals(categoryList.get(j))) {
                                categoryDuplicated = true;
                                break;
                            }
                        }

                        if(!categoryDuplicated){
                            categoryList.add(entry.getCategory());
                        }


                        if(!(entry.getSeries().equals(""))) {
                            boolean seriesDuplicated = false;

                            for (int j = 0; j < seriesList.size(); j++) {

                                if (entry.getSeries().equals(seriesList.get(j))) {
                                    seriesDuplicated = true;
                                    break;
                                }
                            }

                            if(!seriesDuplicated){
                                seriesList.add(entry.getSeries());
                            }

                        }
                    }

                }

                ArrayAdapter<String> authorAdapter = new ArrayAdapter<>(
                        FilterActivity.this, android.R.layout.simple_spinner_item,authorList );
                authorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                        FilterActivity.this, android.R.layout.simple_spinner_item,categoryList );
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                ArrayAdapter<String> seriesAdapter = new ArrayAdapter<>(
                        FilterActivity.this, android.R.layout.simple_spinner_item,seriesList );
                seriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                mAuthor.setAdapter(authorAdapter);
                mAuthor.setOnItemSelectedListener(FilterActivity.this);

                mCategory.setAdapter(categoryAdapter);
                mCategory.setOnItemSelectedListener(FilterActivity.this);

                mSeries.setAdapter(seriesAdapter);
                mSeries.setOnItemSelectedListener(FilterActivity.this);


                if (!authorFilter.equals("") && isAuthorFilter) {
                    for (int k = 0; k < authorList.size(); k++) {
                        if (authorList.get(k).equals(authorFilter)) {
                            positionAuthor = k ;
                        }
                    }
                    mAuthor.setSelection(positionAuthor);
                }


                if (!categoryFilter.equals("") && isCategoryFilter) {
                    for (int k = 0; k < categoryList.size(); k++) {
                        if (categoryList.get(k).equals(categoryFilter)) {
                            positionCategory = k ;
                        }
                    }
                    mCategory.setSelection(positionCategory);
                }

                if (!seriesFilter.equals("") && isSeriesFilter) {
                    for (int k = 0; k < seriesList.size(); k++) {
                        if (seriesList.get(k).equals(seriesFilter)) {
                            positionSeries = k ;
                        }
                    }
                    mSeries.setSelection(positionSeries);
                }
            }

        });

    }



    private void returnResult(){
        mAuthor.onSaveInstanceState();
        mCategory.onSaveInstanceState();
        mSeries.onSaveInstanceState();

        Intent parentActivity = new Intent();

        if(isAuthorFilter || isCategoryFilter || isSeriesFilter){
            setResult(RESULT_OK, parentActivity);
        }else{
            setResult(RESULT_CANCELED);
        }
    }

}
