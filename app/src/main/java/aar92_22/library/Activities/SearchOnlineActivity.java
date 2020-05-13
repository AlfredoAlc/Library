package aar92_22.library.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import aar92_22.library.Adapters.BookListAdapter;
import aar92_22.library.Database.BookEntry;
import aar92_22.library.R;
import aar92_22.library.Utilities.NetworkUtilities;

public class SearchOnlineActivity extends AppCompatActivity implements BookListAdapter.ListBookClickListener,
    BookListAdapter.BookLongClickListener{

    private static String SEARCH_FOR_BUNDLE = "search_for_bundle";
    SearchView searchView;
    RecyclerView searchResultsRecyclerView;
    ProgressBar waitingPb;

    String searchQuery;

    BookListAdapter mAdapter;
    BookEntry bookEntry;
    List<BookEntry> bookEntryList;
    String title;
    String lastName;
    String firstName;
    String publisher;
    String publishedDate;
    String category;
    int numberPages;
    byte [] bookCover;


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

        if(actionBar != null ){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        searchView = findViewById(R.id.search_input);
        setUpSearchView();

        searchResultsRecyclerView = findViewById(R.id.search_results_rv);
        setUpRecyclerView();

        bookEntryList = new ArrayList<>();

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
        super.onBackPressed();
    }

    @Override
    public void onListBookClick(int id) {

    }

    @Override
    public void onListBookClickFromSearch(BookEntry entry) {
        Intent intent = new Intent(SearchOnlineActivity.this, BookDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", entry.getTitle());
        bundle.putString("firstName", entry.getFirstName());
        bundle.putString("lastName", entry.getLastName());
        bundle.putString("publisher", entry.getPublisher());
        bundle.putString("publishedDate", entry.getPublishedDate());
        bundle.putString("category", entry.getCategory());
        bundle.putInt("numberPages", entry.getNumberPages());
        bundle.putByteArray("bookCover", entry.getBookCover());

        intent.putExtra(BookDetailActivity.EXTRA_BOOK_ENTRY, bundle);
        intent.putExtra(BookDetailActivity.EXTRA_FROM_SEARCH, true);
        startActivity(intent);
    }

    @Override
    public void onLongBookClick(int id) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SEARCH_FOR_BUNDLE, searchQuery);
        super.onSaveInstanceState(outState);
    }

    @SuppressLint("StaticFieldLeak")
    class QueryTask extends AsyncTask<URL, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            waitingPb.setVisibility(View.VISIBLE);
            bookEntryList.clear();
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String searchResults = null;

            try{
                searchResults = NetworkUtilities.getResponseFromHttpUrl(searchUrl);
            }catch (Exception e){
                e.printStackTrace();
            }

            return searchResults;
        }


        @Override
        protected void onPostExecute(String result) {
              new GetImagesFromJSON().execute(result);
        }

    }
    @SuppressLint("StaticFieldLeak")
    class GetImagesFromJSON extends AsyncTask<String, Void, List<BookEntry>>{

        @Override
        protected List<BookEntry> doInBackground(String... strings) {
            String values = strings[0];

            try {
                JSONObject results = new JSONObject(values);
                JSONArray items = results.getJSONArray("items");
                JSONObject item;
                JSONObject volumeInfo;
                JSONArray authors;
                JSONObject images;
                JSONArray categories;

                //iterate over all items
                for (int i = 0; i < items.length(); i++) {
                    item = items.getJSONObject(i);
                    volumeInfo = item.getJSONObject("volumeInfo");


                    title = volumeInfo.getString("title");

                    if(volumeInfo.has("authors")){
                        authors = volumeInfo.getJSONArray("authors");
                        separateFirstFromLastName(authors);
                    }


                    if (volumeInfo.has("pageCount")) {
                        numberPages = Integer.parseInt(volumeInfo.getString("pageCount"));
                    } else {
                        numberPages = 0;
                    }

                    if(volumeInfo.has("imageLinks")){
                        images = volumeInfo.getJSONObject("imageLinks");
                        processImageLink(images);
                    }

                    if(volumeInfo.has("publisher")){
                        publisher = volumeInfo.getString("publisher");
                    }

                    if(volumeInfo.has("publishedDate")){
                        publishedDate = volumeInfo.getString("publishedDate");
                    }

                    if(volumeInfo.has("categories")){
                        categories = volumeInfo.getJSONArray("categories");
                        category = String.valueOf(categories.get(0));
                    }

                    bookEntry = new BookEntry(title, lastName, firstName, null,
                            null, null, null, publisher,
                            publishedDate, numberPages, null, null,
                            category, null, bookCover, null);
                    bookEntryList.add(bookEntry);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return bookEntryList;
        }

        @Override
        protected void onPostExecute(List<BookEntry> bookEntries) {
            mAdapter.setBookEntry(bookEntries);
            waitingPb.setVisibility(View.INVISIBLE);
        }
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
        final URL completeSearchUrl = NetworkUtilities.buildUrl(searchQuery);
        new QueryTask().execute(completeSearchUrl);
    }

    private void setUpRecyclerView(){
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        mAdapter = new BookListAdapter(this, this,this, true);
        searchResultsRecyclerView.setLayoutManager(linearLayout);
        searchResultsRecyclerView.setAdapter(mAdapter);
        searchResultsRecyclerView.setHasFixedSize(true);
    }


    private void separateFirstFromLastName(JSONArray authors){
        String author;
        String [] authorArr;
        try{
            author = String.valueOf(authors.get(0));
            authorArr = author.split(" ");
            firstName = authorArr[0];
            if (authorArr.length > 1) {
                lastName = authorArr[1];
            } else {
                lastName = "";
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void processImageLink(JSONObject images){
        try{
            String tempImageUrl = images.getString("smallThumbnail");
            String imageURL = tempImageUrl.replace("http", "https");

            URL url = null;
            Bitmap bitmap = null;

            try {
                url = new URL(imageURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                if(url != null){
                    bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bitmap != null) {
                ByteArrayOutputStream objectByteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, objectByteArrayOutputStream);
                bookCover = objectByteArrayOutputStream.toByteArray();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

    }




}
