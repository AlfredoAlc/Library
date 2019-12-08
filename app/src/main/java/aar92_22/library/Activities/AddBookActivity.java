package aar92_22.library.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;


import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import aar92_22.library.AppExecutors;
import aar92_22.library.Database.AppDataBase;
import aar92_22.library.Database.BookEntry;
import aar92_22.library.Database.CategoryDataBase;
import aar92_22.library.Database.CategoryEntry;
import aar92_22.library.R;
import aar92_22.library.ViewModel.AddBookFactoryModel;
import aar92_22.library.ViewModel.AddBookViewModel;

import static com.google.android.gms.ads.AdSize.SMART_BANNER;

public class AddBookActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    public static final String BOOK_ID = "book_id";
    public static final String INSTANCE_BOOK_ID = "instance_book_id";

    private static final int DEFAULT_BOOK_ID = -1 ;


    private int bookId = DEFAULT_BOOK_ID;

    String mCategory;

    EditText mTitle;
    EditText mLastName;
    EditText mFirstName;
    EditText mLastName2;
    EditText mFirstName2;
    EditText mLastName3;
    EditText mFirstName3;
    EditText mPublisher;
    EditText mPublishedDate;
    EditText mNumberPages;
    EditText mSeries;
    EditText mVolume;
    EditText mSummary;

    Spinner category;

    Button camera;

    LinearLayout author2;
    LinearLayout author3;

    AppDataBase mDb;

    ArrayList <String> categoryList;
    CategoryDataBase categoryDataBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        //Load ad
        AdView mAdView;
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setAdSize(SMART_BANNER);
        mAdView.setAdUnitId(String.valueOf(R.string.banner_ad_unit_id));
        mAdView.loadAd(adRequest);

        mTitle = findViewById(R.id.title_input);
        mLastName = findViewById(R.id.last_name_input);
        mFirstName = findViewById(R.id.first_name_input);
        mLastName2 = findViewById(R.id.last_name_input2);
        mFirstName2 = findViewById(R.id.first_name_input2);
        mLastName3 = findViewById(R.id.last_name_input3);
        mFirstName3 = findViewById(R.id.first_name_input3);
        mPublisher = findViewById(R.id.publisher_input);
        mPublishedDate = findViewById(R.id.published_date_input);
        mNumberPages = findViewById(R.id.number_pages_input);
        mSeries = findViewById(R.id.series_input);
        mVolume = findViewById(R.id.volume_input);
        mSummary = findViewById(R.id.summary_input);

        category = findViewById(R.id.category_spinner);

        author2 = findViewById(R.id.author_layout2);
        author3 = findViewById(R.id.author_layout3);


        camera = findViewById(R.id.take_photo);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppExecutors.getInstance().otherIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        List<CategoryEntry> categoryList = categoryDataBase.categoryDao().loadAllCategories();
                         for(int i = 0; i<categoryList.size(); i++){

                             categoryDataBase.categoryDao().deleteCategory(categoryList.get(i));
                         }

                    }
                });
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null ){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        mDb = AppDataBase.getsInstance(getApplicationContext());

        categoryDataBase = CategoryDataBase.getsInstance(getApplicationContext());


        if ( savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_BOOK_ID) ){
            bookId = savedInstanceState.getInt(INSTANCE_BOOK_ID,DEFAULT_BOOK_ID);
        }


        Intent intent = getIntent();

        if(intent != null && intent.hasExtra(BOOK_ID)){
            if(bookId == DEFAULT_BOOK_ID){

                bookId = intent.getIntExtra(BOOK_ID,DEFAULT_BOOK_ID);
                AddBookFactoryModel factory = new AddBookFactoryModel(mDb, bookId);

                final AddBookViewModel viewModel = ViewModelProviders.of(this,factory).get(AddBookViewModel.class);

                viewModel.getBooks().observe(this, new Observer<BookEntry>() {
                    @Override
                    public void onChanged(@Nullable BookEntry bookEntry) {
                        viewModel.getBooks().removeObserver(this);
                        populateUI(bookEntry);
                    }
                });

            }

        }


        setUpCategories();




    }


    private void setUpCategories(){

        categoryList = new ArrayList<>();

        AppExecutors.getInstance().otherIO().execute(new Runnable() {
            @Override
            public void run() {
                List<CategoryEntry> categoryEntries = categoryDataBase.categoryDao().loadAllCategories();

                for (int i=0; i< categoryEntries.size(); i++) {

                    CategoryEntry categoryEntry = categoryEntries.get(i);

                    categoryList.add(categoryEntry.getCategory());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        AddBookActivity.this, android.R.layout.simple_spinner_item, categoryList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                category.setAdapter(adapter);
                category.setOnItemSelectedListener(AddBookActivity.this);

            }




        });






    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_BOOK_ID, bookId);
        super.onSaveInstanceState(outState);


    }


    private void populateUI(BookEntry bookEntry){
        if(bookEntry == null ){
            return;
        }

        mTitle.setText(bookEntry.getTitle());
        mLastName.setText(bookEntry.getLastName());
        mFirstName.setText(bookEntry.getFirstName());
        mPublisher.setText(bookEntry.getPublisher());
        mPublishedDate.setText(bookEntry.getPublishedDate());
        mNumberPages.setText(bookEntry.getNumberPages());
        mSeries.setText(bookEntry.getSeries());
        mVolume.setText(bookEntry.getVolume());
        mSummary.setText(bookEntry.getSummary());
    }

    public void saveData() {
        String title = String.valueOf(mTitle.getText());
        String lastName = String.valueOf(mLastName.getText());
        String firstName = String.valueOf(mFirstName.getText());
        String lastName2 = String.valueOf(mLastName2.getText());
        String firstName2 = String.valueOf(mFirstName2.getText());
        String lastName3 = String.valueOf(mLastName3.getText());
        String firstName3 = String.valueOf(mFirstName3.getText());
        String publisher = String.valueOf(mPublisher.getText());
        String publishedDate = String.valueOf(mPublishedDate.getText());
        int numberPages;
        if(!(String.valueOf(mNumberPages.getText()).equals("")) ) {
            numberPages = Integer.parseInt(String.valueOf(mNumberPages.getText()));
        }else{
            numberPages = 0;
        }
        String series = String.valueOf(mSeries.getText());
        String volume = String.valueOf(mVolume.getText());
        String summary = String.valueOf(mSummary.getText());


        final BookEntry bookEntry = new BookEntry(title,lastName, firstName, lastName2, firstName2,
                lastName3, firstName3, publisher, publishedDate, numberPages, series, volume,
                mCategory, summary);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(bookId == DEFAULT_BOOK_ID) {
                    mDb.bookDao().insertBook(bookEntry);
                } else {
                    bookEntry.setId(bookId);
                    mDb.bookDao().updateBook(bookEntry);
                }

            }
        });

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.add_book_activity_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_done:
                saveData();
                return true;

                default:  return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mCategory = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void clickAddAuthor(View view) {

        if(author2.getVisibility() == View.GONE && author3.getVisibility() == View.GONE){
            author2.setVisibility(View.VISIBLE);
        } else if ( author2.getVisibility() == View.VISIBLE && author3.getVisibility() == View.GONE){
            author3.setVisibility(View.VISIBLE);
        }

    }

    public void clickRemoveAuthor2(View view) {
        mLastName2.setText("");
        mFirstName2.setText("");
        author2.setVisibility(View.GONE);
    }

    public void clickRemoveAuthor3(View view) {
        mLastName3.setText("");
        mFirstName3.setText("");
        author3.setVisibility(View.GONE);
    }
}
