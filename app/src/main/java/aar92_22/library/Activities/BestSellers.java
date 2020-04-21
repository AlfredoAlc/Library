package aar92_22.library.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import aar92_22.library.R;

public class BestSellers extends AppCompatActivity {


    RecyclerView bestSellersRV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_sellers);

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

        bestSellersRV = findViewById(R.id.best_sellers_recycler_view);




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




    /*private void setUpView() {

        LinearLayoutManager recyclerViewLayout = new LinearLayoutManager(this);
        bestSellersRV.setLayoutManager(recyclerViewLayout);
        bestSellersRV.setAdapter(bestSellersAdapter);


    }*/



}
