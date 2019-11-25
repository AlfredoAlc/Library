package aar92_22.library.Activities;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import aar92_22.library.R;

import static com.google.android.gms.ads.AdSize.SMART_BANNER;

public class BookDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK_TITLE = "book_title";
    public static final String EXTRA_BOOK_AUTHOR = "book_author";

    TextView bookTitleTextView;
    TextView bookAuthorTextView;
    ImageView bookCoverImageView;

    String bookTitle;
    String bookAuthor;

    private AdView mAdView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);


        //Load ad
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setAdSize(SMART_BANNER);
        mAdView.setAdUnitId(String.valueOf(R.string.banner_ad_unit_id));
        mAdView.loadAd(adRequest);

        bookTitleTextView = findViewById(R.id.title_text_view);
        bookAuthorTextView = findViewById(R.id.author_text_view);
        bookCoverImageView = findViewById(R.id.book_image_view);


        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null ){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();

        if(intent != null){
            bookTitle = intent.getStringExtra(EXTRA_BOOK_TITLE);
            bookAuthor = intent.getStringExtra(EXTRA_BOOK_AUTHOR);

            bookTitleTextView.setText(bookTitle);
            bookAuthorTextView.setText(bookAuthor);

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
