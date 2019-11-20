package aar92_22.library;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import aar92_22.library.Database.AppDataBase;
import aar92_22.library.Database.BookEntry;
import aar92_22.library.ViewModel.AddBookFactoryModel;
import aar92_22.library.ViewModel.AddBookViewModel;

import static com.google.android.gms.ads.AdSize.SMART_BANNER;

public class AddBookActivity extends AppCompatActivity {


    public static final String BOOK_ID = "book_id";
    public static final String INSTANCE_BOOK_ID = "instance_book_id";

    private static final int DEFAULT_BOOK_ID = -1 ;


    private int bookId = DEFAULT_BOOK_ID;

    EditText mBookTitle;
    EditText mBookAuthor;
    Button saveButton;

    AppDataBase mDb;

    private AdView mAdView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        //Load ad
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setAdSize(SMART_BANNER);
        mAdView.setAdUnitId(String.valueOf(R.string.banner_ad_unit_id));
        mAdView.loadAd(adRequest);


        mBookTitle = findViewById(R.id.titleEditText);
        mBookAuthor = findViewById(R.id.authorEditText);
        saveButton = findViewById(R.id.save_button);


        mDb = AppDataBase.getsInstance(getApplicationContext());


        if ( savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_BOOK_ID) ){
            bookId = savedInstanceState.getInt(INSTANCE_BOOK_ID,DEFAULT_BOOK_ID);
        }


        Intent intent = getIntent();

        if(intent != null && intent.hasExtra(BOOK_ID)){
            saveButton.setText(R.string.update_string);
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

        mBookTitle.setText(bookEntry.getTitle());
        mBookAuthor.setText(bookEntry.getAuthor());

    }

    public void saveButtonClick(View view) {
        String title = String.valueOf(mBookTitle.getText());
        String author = String.valueOf(mBookAuthor.getText());

        final BookEntry bookEntry = new BookEntry(title,author, null, null);
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
}
