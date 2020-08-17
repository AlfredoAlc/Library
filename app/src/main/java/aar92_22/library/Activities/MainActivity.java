package aar92_22.library.Activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import aar92_22.library.Database.BookDataBase;
import aar92_22.library.Interfaces.LibraryListener;
import aar92_22.library.Utilities.AppExecutors;
import aar92_22.library.Adapters.BookListAdapter;
import aar92_22.library.Database.BookEntry;
import aar92_22.library.Utilities.Dialogs;
import aar92_22.library.Utilities.ModuleViewDecoration;
import aar92_22.library.R;
import aar92_22.library.Utilities.PreferenceUtilities;
import aar92_22.library.ViewModel.MainViewModel;


public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener, BookListAdapter.ListBookClickListener,
        BookListAdapter.BookLongClickListener, LibraryListener{


    private static final int REQUEST_CODE_FILTER = 3;

    public static boolean fromRecommendedActivity = false;


    private int bookId;
    private boolean firstChangeOfViews = true;

    private BookDataBase mDb;

    Toolbar toolbar;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView bookList;
    BookListAdapter mAdapter;
    LinearLayoutManager linearLayout;
    GridLayoutManager gridLayout;
    ModuleViewDecoration moduleViewDecoration;

    List<BookEntry> listToFilter;

    PreferenceUtilities preferenceUtilities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load ad
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mDb = BookDataBase.getsInstance(this);

        toolbar = findViewById(R.id.mainToolbar);
        toolbar.setTitle(getString(R.string.library_string));
        setSupportActionBar(toolbar);

        bookList = findViewById(R.id.list_books_recycler_view);

        linearLayout = new LinearLayoutManager(this);
        gridLayout = new GridLayoutManager(this, 3);
        moduleViewDecoration = new ModuleViewDecoration(this, R.dimen.module_padding);

        preferenceUtilities = new PreferenceUtilities(this);
        preferenceUtilities.sharedPreferencesChange().registerOnSharedPreferenceChangeListener(this);

        if(preferenceUtilities.isListView()){
            setUpLinearLayout();
        } else {
            setUpGridLayout();
        }

        setUpViewModel();

        pullToRefresh = findViewById(R.id.pull_to_refresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!preferenceUtilities.isAuthorFilter() &&
                        !preferenceUtilities.isCategoryFilter() &&
                        !preferenceUtilities.isSeriesFilter()) {
                    setUpViewModel();
                }
                pullToRefresh.setRefreshing(false);

            }
        });

        toolbar.setTitle(preferenceUtilities.getLibraryName());



    }

    @Override
    protected void onStart() {
        super.onStart();

        if(fromRecommendedActivity) {
            fromRecommendedActivity = false;
        } else{
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }

            final String recommendedTitle = preferenceUtilities.getRecommendedTitle();
            if (networkInfo != null && networkInfo.isConnected() && !recommendedTitle.equals("")) {
                Intent recommendedIntent = new Intent(MainActivity.this, RecommendedActivity.class);
                startActivity(recommendedIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main_activity, menu);

        for(int i = 0; i < menu.size() ; i++){
            MenuItem viewItem = menu.getItem(i);
            if(viewItem.getItemId() == R.id.change_view_menu){
                if(preferenceUtilities.isListView()){
                    viewItem.setIcon(R.drawable.ic_view_module);
                }else{
                    viewItem.setIcon(R.drawable.ic_view_list);
                }
            }
        }


        MenuItem searchItem = menu.findItem(R.id.search_menu);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.change_view_menu:
                if(firstChangeOfViews){
                    if(preferenceUtilities.isListView()){
                        item.setIcon(R.drawable.ic_view_list);
                        setUpGridLayout();
                    }else{
                        item.setIcon(R.drawable.ic_view_module);
                        setUpLinearLayout();
                    }

                    firstChangeOfViews = false;
                } else{
                    if(preferenceUtilities.isListView()){
                        item.setIcon(R.drawable.ic_view_module);
                        setUpLinearLayout();
                        preferenceUtilities.setListView(false);
                    }else{
                        item.setIcon(R.drawable.ic_view_list);
                        setUpGridLayout();
                        preferenceUtilities.setListView(true);
                    }

                }

                setUpViewModel();
                return true;


            case R.id.filter_menu:
                Intent filterIntent = new Intent(MainActivity.this, FilterActivity.class);
                startActivityForResult(filterIntent, REQUEST_CODE_FILTER);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.setting_drawer_activity:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;


            case R.id.info:
                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.library_name_key))) {
            toolbar.setTitle(preferenceUtilities.getLibraryName());
        } else if (key.equals(getString(R.string.sort_by_key))) {
            setUpViewModel();
        }
    }

    @Override
    public void onListBookClick(final int id) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                BookEntry bookEntry = mDb.bookDao().loadBookByIdIndividual(id);
                Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
                intent.putExtra(BookDetailActivity.EXTRA_ID, bookEntry.getId());
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    @Override
    public void onListBookClickFromSearch(BookEntry bookEntry) {

    }


    @Override
    public void onLongBookClick(int id) {
        this.bookId = id;

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Intent intent = new Intent(this, AddBookActivity.class);
                intent.putExtra(AddBookActivity.BOOK_ID, bookId);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            case 2:
                Dialogs.deleteBookConfirmationDialog(this, this);
                break;

        }

        return true;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FILTER) {
            if (resultCode == RESULT_OK) {
                setFilter();
            } else if (resultCode == RESULT_CANCELED) {
                setUpViewModel();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0){
            for(int result : grantResults) {
                if(result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.permission_not_granted),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }else {
            Toast.makeText(this, getString(R.string.permission_not_granted),
                    Toast.LENGTH_LONG).show();
        }

    }


    @Override
    protected void onDestroy() {
        preferenceUtilities.sharedPreferencesChange().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }


    @Override
    public void deleteBookListener() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                BookEntry book = mDb.bookDao().loadBookByIdIndividual(bookId);
                mDb.bookDao().deleteBook(book);
            }
        });
    }


    @Override
    public void deleteLibraryListener() {}


    public void scanIsbnClick(View v) {

    }


    public void searchOnlineClick(View v) {
        Intent searchIntent = new Intent(MainActivity.this, SearchOnlineActivity.class);
        startActivity(searchIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    public void addBookClick(View v) {
        Intent intent = new Intent(MainActivity.this, AddBookActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }



    private void setUpLinearLayout() {
        bookList.removeAllViews();
        bookList.setLayoutManager(linearLayout);
        mAdapter = new BookListAdapter(this, this,this,
                true);
        bookList.removeItemDecoration(moduleViewDecoration);
        bookList.setAdapter(mAdapter);
    }

    private void setUpGridLayout() {
        bookList.removeAllViews();
        bookList.setLayoutManager(gridLayout);
        bookList.addItemDecoration(moduleViewDecoration);
        mAdapter = new BookListAdapter(this, this,this,
                false);
        bookList.setAdapter(mAdapter);
    }


    private void setUpViewModel() {
        String sortBy = preferenceUtilities.getSortBy();
        MainViewModel viewModel = new MainViewModel(getApplication(), sortBy);
        viewModel.getBooks().observe(MainActivity.this, new Observer<List<BookEntry>>() {
            @Override
            public void onChanged(@Nullable List<BookEntry> bookEntries) {
                mAdapter.setBookEntry(bookEntries);
                listToFilter = bookEntries;

                int recommendedIndex;

                if(bookEntries != null && bookEntries.size() >= 1) {
                    do {
                        recommendedIndex = (int) (Math.random() * 10);
                    } while (recommendedIndex >= bookEntries.size());


                    String bookReferenceRecommend = bookEntries.get(recommendedIndex).getTitle();
                    preferenceUtilities.setRecommendedTitle(bookReferenceRecommend);

                }
            }
        });
    }


    private void setFilter() {
        List<BookEntry> result = new ArrayList<>();

        if (listToFilter != null) {
            for (BookEntry entry : listToFilter) {
                if(preferenceUtilities.isAuthorFilter() &&
                        entry.getLastName().equals(preferenceUtilities.getAuthorFilter()) &&
                        !result.contains(entry)){
                    result.add(entry);
                }
                if (preferenceUtilities.isCategoryFilter() &&
                        entry.getCategory().equals(preferenceUtilities.getCategoryFilter()) &&
                        !result.contains(entry)) {
                    result.add(entry);
                }
                if(preferenceUtilities.isSeriesFilter() &&
                        entry.getSeries().equals(preferenceUtilities.getSeriesFilter()) &&
                        !result.contains(entry)){
                    result.add(entry);
                }
            }
        }
        mAdapter.setBookEntry(result);
    }



}

