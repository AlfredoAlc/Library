package aar92_22.library;


import android.app.IntentService;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

import aar92_22.library.Database.AppDataBase;
import aar92_22.library.Database.BookEntry;
import aar92_22.library.ViewModel.MainViewModel;

import static com.google.android.gms.ads.AdSize.SMART_BANNER;

public class MainActivity extends AppCompatActivity
        implements BookListAdapter.ListBookClickListener, BookListAdapter.BookLongClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {



    private int bookId;

    private AppDataBase mDb;
    private AdView mAdView;

    RecyclerView bookList;
    BookListAdapter mAdapter;
    FloatingActionButton addBookFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Load ad
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setAdSize(SMART_BANNER);
        mAdView.setAdUnitId(String.valueOf(R.string.banner_ad_unit_id));
        mAdView.loadAd(adRequest);


        addBookFab = findViewById(R.id.add_book_fab);


        mDb = AppDataBase.getsInstance(getApplicationContext());

        bookList = findViewById(R.id.list_books_recycler_view);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        bookList.setLayoutManager(linearLayout);
        bookList.setHasFixedSize(true);

        mAdapter = new BookListAdapter(this, this, this);

        bookList.setAdapter(mAdapter);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.getString(getString(R.string.sort_by_key), null );
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);


        setupViewModel();

    }


    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getBooks().observe(this, new Observer<List<BookEntry>>() {
            @Override
            public void onChanged(@Nullable List<BookEntry> bookEntries) {
                Log.d("Main view model:", "Updating list of tasks from LiveData in ViewModel");
                mAdapter.setBookEntry(bookEntries);
            }
        });
    }

    public void addBookOnClick(View view) {

        Intent intent = new Intent (MainActivity.this, AddBookActivity.class);
        startActivity(intent);


    }

    @Override
    public void onListBookClick(final int id) {


        AppExecutors.getInstance().diskIO().execute(new Runnable() {

            @Override
            public void run() {
                BookEntry bookEntry = mDb.bookDao().loadBookByIdIndividual(id);
                Intent detailIntent = new Intent (MainActivity.this, BookDetailActivity.class);
                detailIntent.putExtra(BookDetailActivity.EXTRA_BOOK_TITLE, bookEntry.getTitle());
                detailIntent.putExtra(BookDetailActivity.EXTRA_BOOK_AUTHOR, bookEntry.getAuthor());
                startActivity(detailIntent);

            }



        });



    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case 1:
                Intent intent = new Intent(MainActivity.this, AddBookActivity.class);
                intent.putExtra(AddBookActivity.BOOK_ID, bookId );
                startActivity(intent);
                break;

            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.delete_message);
                builder.setCancelable(true);

                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        BookEntry book = mDb.bookDao().loadBookByIdIndividual(bookId);
                                        mDb.bookDao().deleteBook(book);
                                    }
                                });

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
                break;

        }

        return true;



    }

    @Override
    public void onLongBookClick(int id) {
        this.bookId = id;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.settings,menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.settings_menu){
            Intent menuIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(menuIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {



    }

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).
                unregisterOnSharedPreferenceChangeListener(this);


        super.onDestroy();
    }
}

