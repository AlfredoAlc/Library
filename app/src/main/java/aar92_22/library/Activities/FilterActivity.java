package aar92_22.library.Activities;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import aar92_22.library.Database.AppDataBase;
import aar92_22.library.Database.BookEntry;
import aar92_22.library.R;
import aar92_22.library.ViewModel.MainViewModel;

public class FilterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    public static final String AUTHOR_FILTER = "author_filter";
    public static final String CATEGORY_FILTER = "category_filter";
    public static final String SERIES_FILTER = "series_filter";

    public static final String AUTHOR_FILTER_ACTIVATED = "author_filter_activated";
    public static final String CATEGORY_FILTER_ACTIVATED = "category_filter_activated";
    public static final String SERIES_FILTER_ACTIVATED = "series_filter_activated";

    private boolean authorFilterActivated = false;
    private boolean categoryFilterActivated = false;
    private boolean seriesFilterActivated = false;

    Spinner mAuthor;
    Spinner mCategory;
    Spinner mSeries;

    List<String> authorList;
    List<String> categoryList;
    List<String> seriesList;


    String authorFilter;
    String categoryFilter;
    String seriesFilter;

    int positionAuthor;
    int positionCategory;
    int positionSeries;

    SharedPreferences sharedPreferences;

    AppDataBase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null ){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDb = AppDataBase.getsInstance(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        mAuthor = findViewById(R.id.author_spinner);
        mCategory = findViewById(R.id.category_spinner);
        mSeries = findViewById(R.id.series_spinner);

        Intent intent = getIntent();

        if (intent.hasExtra(AUTHOR_FILTER)){
            authorFilter = intent.getStringExtra(AUTHOR_FILTER);
            authorFilterActivated = true;
        }
        if(intent.hasExtra(CATEGORY_FILTER)){
            categoryFilter = intent.getStringExtra(CATEGORY_FILTER);
            categoryFilterActivated = true;
        }
        if (intent.hasExtra(SERIES_FILTER)){
            seriesFilter = intent.getStringExtra(SERIES_FILTER);
            seriesFilterActivated = true;
        }

        setUpViewModel(sharedPreferences);




    }


    private void setUpViewModel(SharedPreferences sharedPreferences){
        String sortBy = sharedPreferences.getString(getString(R.string.sort_by_key), getString(R.string.title) );
        MainViewModel viewModel = new MainViewModel(getApplication(),sortBy);
       // MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
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


                if(authorFilter != null) {
                    if (!authorFilter.equals("") && authorFilterActivated) {
                        for (int k = 0; k < authorList.size(); k++) {
                            if (authorList.get(k).equals(authorFilter)) {
                                positionAuthor = k ;
                            }
                        }
                        mAuthor.setSelection(positionAuthor);
                    }
                }

                if(categoryFilter != null) {

                    if (!categoryFilter.equals("") && categoryFilterActivated) {
                        for (int k = 0; k < categoryList.size(); k++) {
                            if (categoryList.get(k).equals(categoryFilter)) {
                                positionCategory = k ;
                            }
                        }
                        mCategory.setSelection(positionCategory);
                    }

                }


                if(seriesFilter != null) {
                    if (!seriesFilter.equals("") && seriesFilterActivated) {
                        for (int k = 0; k < seriesList.size(); k++) {
                            if (seriesList.get(k).equals(seriesFilter)) {
                                positionSeries = k ;
                            }
                        }
                        mSeries.setSelection(positionSeries);
                    }
                }



            }





        });




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
        super.onBackPressed();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(parent.equals(mAuthor)){
            if(position > 0) {
                authorFilterActivated = true;
                authorFilter = parent.getItemAtPosition(position).toString();
            } else if(position == 0){
                authorFilterActivated = false;
            }
        } else if(parent.equals(mCategory)){
            if(position > 0) {
                categoryFilterActivated = true;
                categoryFilter = parent.getItemAtPosition(position).toString();
            } else if(position == 0){
                categoryFilterActivated = false;
            }
        } else if(parent.equals(mSeries)){
            if(position > 0) {
                seriesFilterActivated = true;
                seriesFilter = parent.getItemAtPosition(position).toString();
            } else if(position == 0){
                seriesFilterActivated = false;
            }
        }



    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void returnResult(){

        Intent parentActivity = new Intent();

        if(authorFilterActivated) {
            parentActivity.putExtra(AUTHOR_FILTER_ACTIVATED, authorFilterActivated);
            parentActivity.putExtra(AUTHOR_FILTER, authorFilter);
            mAuthor.onSaveInstanceState();
        }
        if (categoryFilterActivated){
            parentActivity.putExtra(CATEGORY_FILTER_ACTIVATED, categoryFilterActivated);
            parentActivity.putExtra(CATEGORY_FILTER, categoryFilter);
            mCategory.onSaveInstanceState();
        }
        if(seriesFilterActivated){
            parentActivity.putExtra(SERIES_FILTER_ACTIVATED, seriesFilterActivated);
            parentActivity.putExtra(SERIES_FILTER, seriesFilter);
            mSeries.onSaveInstanceState();
        } if(authorFilterActivated || categoryFilterActivated || seriesFilterActivated){
            setResult(RESULT_OK, parentActivity);

        }
        if(!authorFilterActivated && !categoryFilterActivated && !seriesFilterActivated) {
            setResult(RESULT_CANCELED);
        }


    }






}
