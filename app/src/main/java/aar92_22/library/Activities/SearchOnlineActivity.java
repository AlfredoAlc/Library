package aar92_22.library.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


import java.net.URL;
import java.util.List;

import aar92_22.library.Adapters.BookListAdapter;
import aar92_22.library.Database.BookEntry;
import aar92_22.library.Interfaces.SelectingCoverListener;
import aar92_22.library.R;
import aar92_22.library.Utilities.NetworkUtilities;
import aar92_22.library.ViewModel.SearchOnlineViewModel;

public class SearchOnlineActivity extends AppCompatActivity implements BookListAdapter.ListBookClickListener,
    BookListAdapter.BookLongClickListener, SelectingCoverListener {

    public static final String TITLE_SEARCH = "title_search";
    public static final String FIRST_NAME_SEARCH = "first_name_search";
    public static final String LAST_NAME_SEARCH = "last_name_search";
    public static final String PUBLISHER_SEARCH = "publisher_search";
    public static final String PUBLISHED_DATE_SEARCH = "published_date_search";
    public static final String CATEGORY_SEARCH = "category_search";
    public static final String NUMBER_PAGES_SEARCH = "number_pages_search";
    public static final String SUMMARY_SEARCH = "summary_search";
    public static final String BOOK_COVER_SEARCH = "book_cover_search";
    public static final String VOLUME_SEARCH = "volume_search";

    private static String SEARCH_FOR_BUNDLE = "search_for_bundle";

    SearchView searchView;
    RecyclerView searchResultsRecyclerView;
    ProgressBar waitingPb;

    String searchQuery;

    BookListAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_online);

        //Load ad
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = findViewById(R.id.search_online_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null ) actionBar.setDisplayHomeAsUpEnabled(true);


        searchView = findViewById(R.id.search_input);
        setUpSearchView();

        searchResultsRecyclerView = findViewById(R.id.search_results_rv);
        setUpRecyclerView();


        waitingPb = findViewById(R.id.waiting_pb);

        if(savedInstanceState != null){
            searchQuery = savedInstanceState.getString(SEARCH_FOR_BUNDLE);
            searchOnline(searchQuery);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SEARCH_FOR_BUNDLE, searchQuery);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onPause();
    }


    @Override
    public void onListBookClickFromSearch(BookEntry entry) {
        Intent intent = new Intent(SearchOnlineActivity.this, BookDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(TITLE_SEARCH, entry.getTitle());
        bundle.putString(FIRST_NAME_SEARCH, entry.getFirstName());
        bundle.putString(LAST_NAME_SEARCH, entry.getLastName());
        bundle.putString(PUBLISHER_SEARCH, entry.getPublisher());
        bundle.putString(PUBLISHED_DATE_SEARCH, entry.getPublishedDate());
        bundle.putString(CATEGORY_SEARCH, entry.getCategory());
        bundle.putInt(NUMBER_PAGES_SEARCH, entry.getNumberPages());
        bundle.putString(SUMMARY_SEARCH, entry.getSummary());
        bundle.putByteArray(BOOK_COVER_SEARCH, entry.getBookCover());
        bundle.putString(VOLUME_SEARCH, entry.getVolume());

        intent.putExtra(BookDetailActivity.EXTRA_BOOK_ENTRY, bundle);
        intent.putExtra(BookDetailActivity.EXTRA_FROM_SEARCH, true);
        startActivity(intent);
    }


    private void setUpSearchView(){

        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getString(R.string.search_edit_text_hint));

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }

        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchOnline(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void searchOnline(String query) {
        searchView.clearFocus();
        searchQuery = query;

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;
        if(connectivityManager != null){
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        if(networkInfo != null && networkInfo.isConnected()){
            final URL completeSearchUrl = NetworkUtilities.buildUrl(searchQuery);

            waitingPb.setVisibility(View.VISIBLE);
            SearchOnlineViewModel searchOnlineViewModel = new SearchOnlineViewModel(getApplication(), completeSearchUrl);
            searchOnlineViewModel.getResults().observe(SearchOnlineActivity.this, new Observer<List<BookEntry>>() {
                @Override
                public void onChanged(List<BookEntry> bookEntries) {
                    mAdapter.setBookEntry(bookEntries);
                    waitingPb.setVisibility(View.GONE);
                }
            });

        } else {
            Toast.makeText(this, R.string.No_Connection, Toast.LENGTH_LONG).show();
        }

    }

    private void setUpRecyclerView(){
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        mAdapter = new BookListAdapter(this, this,this, true);
        searchResultsRecyclerView.setLayoutManager(linearLayout);
        searchResultsRecyclerView.setAdapter(mAdapter);
        searchResultsRecyclerView.setHasFixedSize(true);
    }



    @Override
    public void onListBookClick(int id) {}
    @Override
    public void onLongBookClick(int id) {}
    @Override
    public void selectImageListener() {}
    @Override
    public void takePhotoListener() {}


}
