package aar92_22.library;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class BookDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK_TITLE = "book_title";
    public static final String EXTRA_BOOK_AUTHOR = "book_author";

    TextView bookTitleTextView;
    TextView bookAuthorTextView;
    ImageView bookCoverImageView;

    String bookTitle;
    String bookAuthor;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        bookTitleTextView = findViewById(R.id.title_text_view);
        bookAuthorTextView = findViewById(R.id.author_text_view);
        bookCoverImageView = findViewById(R.id.book_image_view);


        Intent intent = getIntent();

        if(intent != null){
            bookTitle = intent.getStringExtra(EXTRA_BOOK_TITLE);
            bookAuthor = intent.getStringExtra(EXTRA_BOOK_AUTHOR);

            bookTitleTextView.setText(bookTitle);
            bookAuthorTextView.setText(bookAuthor);

        }

    }


}
