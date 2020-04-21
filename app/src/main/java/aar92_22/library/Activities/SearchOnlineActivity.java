package aar92_22.library.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.net.URL;

import aar92_22.library.R;
import aar92_22.library.SearchResultsAdapter;
import aar92_22.library.Utilities.NetworkUtilities;

public class SearchOnlineActivity extends AppCompatActivity {

    SearchView searchView;
    RecyclerView searchResultsRecyclerView;

    SearchResultsAdapter searchResultsAdapter;
    ProgressBar waitingPb;


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
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getString(R.string.search_edit_text_hint));


        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchOnline();

            }

        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchOnline();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchResultsAdapter = new SearchResultsAdapter(this);

        searchResultsRecyclerView = findViewById(R.id.search_results_recycler_view);


        setUpView();

        waitingPb = findViewById(R.id.waiting_pb);



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


      class QueryTask extends AsyncTask<URL, Void, String>{


          @Override
          protected void onPreExecute() {
              super.onPreExecute();
              waitingPb.setVisibility(View.VISIBLE);
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
        protected void onPostExecute(String s) {
              waitingPb.setVisibility(View.INVISIBLE);

            if(s != null && !s.equals("")){
                Log.d("Checking", s);
            }else{
                Toast.makeText(getApplicationContext(),getString(R.string.error_occurred),Toast.LENGTH_LONG).show();
            }



        }
    }




    private void searchOnline() {
        searchView.clearFocus();

        String searchQuery = searchView.getQuery().toString();
        URL completeSearchUrl = NetworkUtilities.buildUrl(searchQuery);

        new QueryTask().execute(completeSearchUrl);
    }

    private void setUpView() {

        LinearLayoutManager recyclerViewLayout = new LinearLayoutManager(this);
        searchResultsRecyclerView.setLayoutManager(recyclerViewLayout);
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);


    }




}
