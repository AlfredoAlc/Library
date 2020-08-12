package aar92_22.library.Activities;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.app.ShareCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.text.LineBreaker;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import aar92_22.library.Interfaces.LibraryListener;
import aar92_22.library.Utilities.AppExecutors;
import aar92_22.library.Database.BookDataBase;
import aar92_22.library.Database.BookEntry;
import aar92_22.library.R;
import aar92_22.library.Utilities.Dialogs;
import aar92_22.library.Utilities.NetworkUtilities;
import aar92_22.library.ViewModel.AddBookViewModel;

import static aar92_22.library.Activities.SearchOnlineActivity.BOOK_COVER_SEARCH;
import static aar92_22.library.Activities.SearchOnlineActivity.CATEGORY_SEARCH;
import static aar92_22.library.Activities.SearchOnlineActivity.FIRST_NAME_SEARCH;
import static aar92_22.library.Activities.SearchOnlineActivity.LAST_NAME_SEARCH;
import static aar92_22.library.Activities.SearchOnlineActivity.NUMBER_PAGES_SEARCH;
import static aar92_22.library.Activities.SearchOnlineActivity.PUBLISHED_DATE_SEARCH;
import static aar92_22.library.Activities.SearchOnlineActivity.PUBLISHER_SEARCH;
import static aar92_22.library.Activities.SearchOnlineActivity.SUMMARY_SEARCH;
import static aar92_22.library.Activities.SearchOnlineActivity.TITLE_SEARCH;
import static aar92_22.library.Activities.SearchOnlineActivity.VOLUME_SEARCH;


public class BookDetailActivity extends AppCompatActivity implements LibraryListener {

    public static final String EXTRA_ID = "book_id";
    public static final String EXTRA_FROM_SEARCH = "extra_from_search";
    public static final String EXTRA_BOOK_ENTRY = "extra_book_entry";

    ImageView bookCoverImageView;
    TextView titleTv;
    TextView lastNameTv;
    TextView firstNameTv;
    TextView lastNameTv2;
    TextView firstNameTv2;
    TextView lastNameTv3;
    TextView firstNameTv3;
    TextView publisherTv;
    TextView publishedDateTv;
    TextView numPagesTv;
    TextView seriesTv;
    TextView volTv;
    TextView categoryTv;
    TextView summaryTv;
    TextView summaryText;
    LinearLayout authorLayout;
    LinearLayout authorLayout2;
    LinearLayout authorLayout3;
    LinearLayout publisherLayout;
    LinearLayout publishedDateLayout;
    LinearLayout numPagesLayout;
    LinearLayout seriesLayout;
    LinearLayout categoryLayout;
    NestedScrollView summaryScrollView;

    BookDataBase mDb;

    private int bookId;

    private String titleShare;
    private String lastNameShare;
    private String firstNameShare;

    //Values from search online

    private boolean fromSearchOnline;
    private String title;
    private String firstName;
    private String lastName;
    private String publisher;
    private String publishedDate;
    private String category;
    private String summary;
    private String volume;
    private int numberPages;
    private byte [] bookCover;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        //Load ad
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null ) actionBar.setDisplayHomeAsUpEnabled(true);

        mDb = BookDataBase.getsInstance(getApplicationContext());

        bookCoverImageView = findViewById(R.id.book_image_view);
        titleTv = findViewById(R.id.title_tv);
        lastNameTv = findViewById(R.id.last_name_tv);
        firstNameTv = findViewById(R.id.first_name_tv);
        lastNameTv2 = findViewById(R.id.last_name_tv2);
        firstNameTv2 = findViewById(R.id.first_name_tv2);
        lastNameTv3 = findViewById(R.id.last_name_tv3);
        firstNameTv3 = findViewById(R.id.first_name_tv3);
        publisherTv = findViewById(R.id.publisher_display);
        publishedDateTv = findViewById(R.id.published_date_display);
        numPagesTv = findViewById(R.id.number_pages_display);
        seriesTv = findViewById(R.id.series_display);
        volTv = findViewById(R.id.volume_display);
        categoryTv = findViewById(R.id.category_display);
        summaryTv = findViewById(R.id.summary_display);
        summaryText = findViewById(R.id.summary_tv);

        authorLayout = findViewById(R.id.author_layout);
        authorLayout2 = findViewById(R.id.author_layout2);
        authorLayout3 = findViewById(R.id.author_layout3);
        publisherLayout = findViewById(R.id.publisher_layout);
        publishedDateLayout = findViewById(R.id.published_date_layout);
        numPagesLayout = findViewById(R.id.number_pages_layout);
        seriesLayout = findViewById(R.id.series_layout);
        categoryLayout = findViewById(R.id.category_layout);
        summaryScrollView = findViewById(R.id.summary_scroll_view);


        Intent intent = getIntent();
        if(intent != null){
            if(intent.hasExtra(EXTRA_ID)){
                bookId = intent.getIntExtra(EXTRA_ID, -1);
                final AddBookViewModel viewModel = new AddBookViewModel(getApplication(), mDb, bookId);
                viewModel.getBooks().observe(this, new Observer<BookEntry>() {
                    @Override
                    public void onChanged(@Nullable BookEntry bookEntry) {
                        viewModel.getBooks().removeObserver(this);
                        populateUI(bookEntry);
                    }
                });
            }

            if(intent.hasExtra(EXTRA_BOOK_ENTRY) && intent.getBooleanExtra(EXTRA_FROM_SEARCH, false)){
                fromSearchOnline = true;

                Bundle receivedBundle = intent.getBundleExtra(EXTRA_BOOK_ENTRY);
                if(receivedBundle != null) {
                    title = receivedBundle.getString(TITLE_SEARCH);
                    firstName = receivedBundle.getString(FIRST_NAME_SEARCH);
                    lastName = receivedBundle.getString(LAST_NAME_SEARCH);
                    publisher = receivedBundle.getString(PUBLISHER_SEARCH);
                    publishedDate = receivedBundle.getString(PUBLISHED_DATE_SEARCH);
                    category = receivedBundle.getString(CATEGORY_SEARCH);
                    summary = receivedBundle.getString(SUMMARY_SEARCH);
                    numberPages = receivedBundle.getInt(NUMBER_PAGES_SEARCH);
                    bookCover = receivedBundle.getByteArray(BOOK_COVER_SEARCH);
                    volume = receivedBundle.getString(VOLUME_SEARCH);
                }
                BookEntry bookEntry = new BookEntry(title, lastName, firstName, "",
                        "", "", "", publisher,
                        publishedDate, numberPages, "", "",
                        category, summary, bookCover, null);
                populateUI(bookEntry);
            }
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.book_detail_menu, menu);

        if(fromSearchOnline) {
            MenuItem editMenu = menu.findItem(R.id.edit_action);
            MenuItem deleteMenu = menu.findItem(R.id.delete_action);
            MenuItem shopMenu = menu.findItem(R.id.shop_action);
            MenuItem saveToLibrary = menu.findItem(R.id.save_to_library);
            editMenu.setVisible(false);
            deleteMenu.setVisible(false);
            shopMenu.setVisible(true);
            saveToLibrary.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.edit_action:
                Intent editIntent = new Intent(BookDetailActivity.this, AddBookActivity.class);
                editIntent.putExtra(AddBookActivity.BOOK_ID, bookId );
                startActivity(editIntent);
                finish();
                return true;

            case R.id.delete_action:
                Dialogs.deleteBookConfirmationDialog(this, this);
                return true;

            case R.id.share_action:
                shareAction();
                return true;

            case R.id.shop_action:
                String adUrl = NetworkUtilities.getAdAmazonUrl(volume);
                Uri adPage = Uri.parse(adUrl);
                Intent shopIntent = new Intent(Intent.ACTION_VIEW, adPage);
                if(shopIntent.resolveActivity(getPackageManager()) != null){
                    startActivity(shopIntent);
                }
                return true;

            case R.id.save_to_library:
                Intent saveFromSearchIntent = new Intent(BookDetailActivity.this, AddBookActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(TITLE_SEARCH, title);
                bundle.putString(FIRST_NAME_SEARCH, firstName);
                bundle.putString(LAST_NAME_SEARCH, lastName);
                bundle.putString(PUBLISHER_SEARCH, publisher);
                bundle.putString(PUBLISHED_DATE_SEARCH, publishedDate);
                bundle.putString(CATEGORY_SEARCH, category);
                bundle.putInt(NUMBER_PAGES_SEARCH, numberPages);
                bundle.putString(SUMMARY_SEARCH, summary);
                bundle.putByteArray(BOOK_COVER_SEARCH, bookCover);
                saveFromSearchIntent.putExtra(BookDetailActivity.EXTRA_BOOK_ENTRY, bundle);
                saveFromSearchIntent.putExtra(BookDetailActivity.EXTRA_FROM_SEARCH, true);
                startActivity(saveFromSearchIntent);
                finish();
                return true;

            default: return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onPause();
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
        finish();
    }

    @Override
    public void deleteLibraryListener() {
    }


    private void populateUI(BookEntry bookEntry){
        if(bookEntry == null ){
            return;
        }

        titleShare = bookEntry.getTitle();
        lastNameShare = bookEntry.getLastName();
        firstNameShare = bookEntry.getFirstName();

        titleTv.setText(titleShare);
        lastNameTv.setText(lastNameShare);
        firstNameTv.setText(firstNameShare);

        if(!(bookEntry.getLastName2().equals("") || bookEntry.getFirstName2().equals(""))){
            authorLayout2.setVisibility(View.VISIBLE);
            lastNameTv2.setText(bookEntry.getLastName2());
            firstNameTv2.setText(bookEntry.getFirstName2());

        }else if(!(bookEntry.getLastName3().equals("") || bookEntry.getFirstName2().equals("") )){
            authorLayout3.setVisibility(View.VISIBLE);
            lastNameTv3.setText(bookEntry.getLastName2());
            firstNameTv3.setText(bookEntry.getFirstName2());
        }

        if(bookEntry.getPublisher() != null && !(bookEntry.getPublisher().equals(""))){
            publisherLayout.setVisibility(View.VISIBLE);
            publisherTv.setText(bookEntry.getPublisher());
        }else{
            publisherLayout.setVisibility(View.GONE);
        }

        if(bookEntry.getPublishedDate() != null && !(bookEntry.getPublishedDate().equals(""))){
            publishedDateLayout.setVisibility(View.VISIBLE);
            publishedDateTv.setText(bookEntry.getPublishedDate());
        }else{
            publishedDateLayout.setVisibility(View.GONE);
        }

        if(bookEntry.getNumberPages() > 0){
            numPagesLayout.setVisibility(View.VISIBLE);
            numPagesTv.setText(String.valueOf(bookEntry.getNumberPages()));
        }else{
            numPagesLayout.setVisibility(View.GONE);
        }

        if(bookEntry.getSeries() != null && !(bookEntry.getSeries().equals(""))){
            seriesLayout.setVisibility(View.VISIBLE);
            seriesTv.setText(bookEntry.getSeries());
            volTv.setText(bookEntry.getVolume());
        }else{
            seriesLayout.setVisibility(View.GONE);
        }

        categoryTv.setText(bookEntry.getCategory());

        if(bookEntry.getSummary() != null && !(bookEntry.getSummary().equals(""))){
            summaryText.setVisibility(View.VISIBLE);
            summaryScrollView.setVisibility(View.VISIBLE);
            summaryTv.setText(bookEntry.getSummary());
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                summaryTv.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            }

        }else{
            summaryText.setVisibility(View.GONE);
            summaryScrollView.setVisibility(View.GONE);
        }


        if(bookEntry.getBookCover() != null) {
            try {
                byte[] imageInBytes = bookEntry.getBookCover();
                Bitmap cover = BitmapFactory.decodeByteArray(imageInBytes, 0, imageInBytes.length);
                bookCoverImageView.setImageBitmap(cover);
            }catch (Exception e){
                Toast.makeText(this,R.string.error_image_message, Toast.LENGTH_SHORT).show();
            }

        }

    }


    public void shareAction(){
        String mimeType = "text/plain";
        String textToShare = titleShare + " " +  getString(R.string.by_string) + " " + firstNameShare + getString(R.string.coma) +
                " " + lastNameShare;
        ShareCompat.IntentBuilder.from(BookDetailActivity.this)
                .setChooserTitle(getString(R.string.share_title))
                .setType(mimeType)
                .setText(textToShare)
                .startChooser();
    }





}
