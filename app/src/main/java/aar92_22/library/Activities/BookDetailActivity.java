package aar92_22.library.Activities;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.app.ShareCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import aar92_22.library.AppExecutors;
import aar92_22.library.Database.AppDataBase;
import aar92_22.library.Database.BookEntry;
import aar92_22.library.R;
import aar92_22.library.ViewModel.AddBookFactoryModel;
import aar92_22.library.ViewModel.AddBookViewModel;


public class BookDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "book_id";
    public static final String EXTRA_FROM_SEARCH = "extra_from_search";
    public static final String EXTRA_BOOK_ENTRY = "extra_book_entry";

    Toolbar toolbar;

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

    Bitmap mResultsBitmap;

    AppDataBase mDb;

    int bookId;

    String titleShare;
    String lastNameShare;
    String firstNameShare;

    //Values from search online

    boolean fromSearchOnline;
    String title;
    String firstName;
    String lastName;
    String publisher;
    String publishedDate;
    String category;
    int numberPages;
    byte [] bookCover;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);


        //Load ad
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mDb = AppDataBase.getsInstance(getApplicationContext());

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

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null ){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();

        if(intent != null){

            if(intent.hasExtra(EXTRA_ID)){
                bookId = intent.getIntExtra(EXTRA_ID, -1);

                AddBookFactoryModel factory = new AddBookFactoryModel(mDb, bookId);

                final AddBookViewModel viewModel = ViewModelProviders.of(this,factory).get(AddBookViewModel.class);

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
                title = receivedBundle.getString("title");
                firstName = receivedBundle.getString("firstName");
                lastName = receivedBundle.getString("lastName");
                publisher = receivedBundle.getString("publisher");
                publishedDate = receivedBundle.getString("publishedDate");
                category = receivedBundle.getString("category");
                numberPages = receivedBundle.getInt("numberPages");
                bookCover = receivedBundle.getByteArray("bookCover");

                BookEntry bookEntry = new BookEntry(title, lastName, firstName, "",
                        "", "", "", publisher,
                        publishedDate, numberPages, "", "",
                        category, "", bookCover, null);
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
            //MenuItem shopMenu = menu.findItem(R.id.shop_action);
            MenuItem saveToLibrary = menu.findItem(R.id.save_to_library);
            editMenu.setVisible(false);
            deleteMenu.setVisible(false);
            saveToLibrary.setVisible(true);
            //shopMenu.setVisible(true);
        }

        return super.onCreateOptionsMenu(menu);


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
                break;

            case R.id.delete_action:

                deleteCheck();
                break;

            case R.id.share_action:
                shareAction();
                break;

            /*case R.id.shop_action:
                Uri adPage = Uri.parse(AD_PAGE);
                Intent shopIntent = new Intent(Intent.ACTION_VIEW, adPage);
                if(shopIntent.resolveActivity(getPackageManager()) != null){
                    startActivity(shopIntent);
                }
                break;*/

            case R.id.save_to_library:
                Intent saveFromSearchIntent = new Intent(BookDetailActivity.this, AddBookActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", title);
                bundle.putString("firstName", firstName);
                bundle.putString("lastName", lastName);
                bundle.putString("publisher", publisher);
                bundle.putString("publishedDate", publishedDate);
                bundle.putString("category", category);
                bundle.putInt("numberPages", numberPages);
                bundle.putByteArray("bookCover", bookCover);
                saveFromSearchIntent.putExtra(BookDetailActivity.EXTRA_BOOK_ENTRY, bundle);
                saveFromSearchIntent.putExtra(BookDetailActivity.EXTRA_FROM_SEARCH, true);
                startActivity(saveFromSearchIntent);
                finish();
                break;


        }

        return super.onOptionsItemSelected(item);
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

        if(!(bookEntry.getPublisher().equals(""))){
            publisherLayout.setVisibility(View.VISIBLE);
            publisherTv.setText(bookEntry.getPublisher());
        }else{
            publisherLayout.setVisibility(View.GONE);
        }

        if(!(bookEntry.getPublishedDate().equals(""))){
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

        if(!(bookEntry.getSeries().equals(""))){
            seriesLayout.setVisibility(View.VISIBLE);
            seriesTv.setText(bookEntry.getSeries());
            volTv.setText(bookEntry.getVolume());
        }else{
            seriesLayout.setVisibility(View.GONE);
        }

        categoryTv.setText(bookEntry.getCategory());

        if(!(bookEntry.getSummary().equals(""))){
            summaryText.setVisibility(View.VISIBLE);
            summaryScrollView.setVisibility(View.VISIBLE);
            summaryTv.setText(bookEntry.getSummary());
        }else{
            summaryText.setVisibility(View.GONE);
            summaryScrollView.setVisibility(View.GONE);
        }


        if(bookEntry.getBookCover() != null) {
            try {
                byte[] imageInBytes = bookEntry.getBookCover();
                mResultsBitmap = BitmapFactory.decodeByteArray(imageInBytes, 0, imageInBytes.length);
                bookCoverImageView.setImageBitmap(mResultsBitmap);
            }catch (Exception e){
                Toast.makeText(this,R.string.error_image_message, Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void deleteCheck() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                        finish();
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
