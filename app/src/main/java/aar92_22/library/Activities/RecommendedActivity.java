package aar92_22.library.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.text.LineBreaker;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;

import java.util.HashMap;

import aar92_22.library.R;
import aar92_22.library.Utilities.NetworkUtilities;
import aar92_22.library.ViewModel.RecommendedViewModel;

public class RecommendedActivity extends AppCompatActivity{

    public static final String RECOMMENDED_TITLE = "title";
    public static final String RECOMMENDED_AUTHOR = "author";
    public static final String RECOMMENDED_BOOK_COVER = "bookCover";
    public static final String RECOMMENDED_ISBN = "isbn";
    public static final String RECOMMENDED_SUMMARY = "summary";

    HashMap<String, Object> bookMap;

    ImageView bookImageView;
    TextView bookTitle;
    TextView bookAuthor;
    TextView summaryDisplay;
    TextView summaryTitle;
    NestedScrollView summaryScrollView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended);

        bookImageView = findViewById(R.id.book_image_view);
        bookTitle = findViewById(R.id.title_tv);
        bookAuthor = findViewById(R.id.author_name);
        summaryDisplay = findViewById(R.id.summary_display);
        summaryTitle = findViewById(R.id.summary_tv);
        summaryScrollView = findViewById(R.id.summary_scroll_view);

        RecommendedViewModel recommendedViewModel = new RecommendedViewModel(getApplication());
        recommendedViewModel.getBookMap().observe(this, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> stringObjectHashMap) {
                bookMap = stringObjectHashMap;
                setUpRecommendedView();
            }
        });

    }

    private void setUpRecommendedView(){
        String title = (String) bookMap.get(RECOMMENDED_TITLE);
        String author = (String) bookMap.get(RECOMMENDED_AUTHOR);
        String summary = (String) bookMap.get(RECOMMENDED_SUMMARY);
        byte [] bookCoverByte = (byte[]) bookMap.get(RECOMMENDED_BOOK_COVER);

        bookTitle.setText(title);
        bookAuthor.setText(author);

        if(bookCoverByte != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bookCoverByte, 0, bookCoverByte.length);
            bookImageView.setImageBitmap(bitmap);
        }

        if(summary != null && !summary.equals("")){
            summaryTitle.setVisibility(View.VISIBLE);
            summaryScrollView.setVisibility(View.VISIBLE);
            summaryDisplay.setText(summary);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                summaryDisplay.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            }
        }

    }

    public void closeOnClick(View view) {
        finish();
    }

    public void shopOnClick(View view) {
        String adUrl = NetworkUtilities.getAdAmazonUrl((String) bookMap.get(RECOMMENDED_ISBN));
        Uri adPage = Uri.parse(adUrl);
        Intent shopIntent = new Intent(Intent.ACTION_VIEW, adPage);
        if (shopIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(shopIntent);
        }


    }

    @Override
    protected void onPause() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onPause();
    }


}
