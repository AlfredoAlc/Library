package aar92_22.library.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import aar92_22.library.Database.AppDataBase;
import aar92_22.library.Database.BookEntry;
import aar92_22.library.R;
import aar92_22.library.ViewModel.MainViewModel;

public class FilterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    public static final String AUTHOR_FILTER = "author_filter";
    private boolean filterActivated = false;

    Spinner mAuthor;
    Spinner mCategory;
    Spinner mSeries;


    AppDataBase mDb;

    List<String> authorList;

    Intent parentActivity;

    String authorFilter;
    int position;

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

        mAuthor = findViewById(R.id.author_spinner);
        mCategory = findViewById(R.id.category_spinner);
        mSeries = findViewById(R.id.series_spinner);

        Intent intent = getIntent();

        if (intent.hasExtra(AUTHOR_FILTER)){
            authorFilter = intent.getStringExtra(AUTHOR_FILTER);
            filterActivated = true;
        }

        setUpViewModel();




    }


    private void setUpViewModel(){
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getBooks().observe(this, new Observer<List<BookEntry>>() {
            @Override
            public void onChanged(@Nullable List<BookEntry> bookEntries) {

                authorList = new ArrayList<>();
                authorList.add("All");

                if(bookEntries != null) {

                    for (int i = 0; i < bookEntries.size(); i++) {
                        BookEntry entry = bookEntries.get(i);

                        authorList.add(entry.getAuthor());


                        if(entry.getAuthor().equals(authorFilter)){
                            position = i + 1;
                        }

                    }

                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        FilterActivity.this, android.R.layout.simple_spinner_item,authorList );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                mAuthor.setAdapter(adapter);
                mAuthor.setOnItemSelectedListener(FilterActivity.this);

                if(filterActivated){
                    mAuthor.setSelection(position);

                }

            }



        });




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

        if(position > 0) {
            filterActivated = true;
            authorFilter = parent.getItemAtPosition(position).toString();
        } else if(position == 0){
            filterActivated = false;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void returnResult(){

        if(filterActivated) {
            parentActivity = new Intent();
            parentActivity.putExtra(AUTHOR_FILTER, authorFilter);
            setResult(RESULT_OK, parentActivity);
            mAuthor.onSaveInstanceState();
        } else {
            setResult(RESULT_CANCELED);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.filter_activity_menu, menu);

        return true;
    }



}
