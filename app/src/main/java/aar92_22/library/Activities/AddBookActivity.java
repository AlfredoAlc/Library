package aar92_22.library.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import aar92_22.library.Database.BookDataBase;
import aar92_22.library.Interfaces.SelectingCoverListener;
import aar92_22.library.Utilities.AppExecutors;
import aar92_22.library.Utilities.BitmapUtils;
import aar92_22.library.Database.BookEntry;
import aar92_22.library.Database.CategoryDataBase;
import aar92_22.library.Database.CategoryEntry;
import aar92_22.library.R;
import aar92_22.library.Utilities.Dialogs;
import aar92_22.library.ViewModel.AddBookViewModel;
import aar92_22.library.ViewModel.CategoryViewModel;

import static aar92_22.library.Activities.SearchOnlineActivity.BOOK_COVER_SEARCH;
import static aar92_22.library.Activities.SearchOnlineActivity.CATEGORY_SEARCH;
import static aar92_22.library.Activities.SearchOnlineActivity.FIRST_NAME_SEARCH;
import static aar92_22.library.Activities.SearchOnlineActivity.LAST_NAME_SEARCH;
import static aar92_22.library.Activities.SearchOnlineActivity.NUMBER_PAGES_SEARCH;
import static aar92_22.library.Activities.SearchOnlineActivity.PUBLISHED_DATE_SEARCH;
import static aar92_22.library.Activities.SearchOnlineActivity.PUBLISHER_SEARCH;
import static aar92_22.library.Activities.SearchOnlineActivity.SUMMARY_SEARCH;
import static aar92_22.library.Activities.SearchOnlineActivity.TITLE_SEARCH;


public class AddBookActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        SelectingCoverListener {


    public static final String BOOK_ID = "book_id";
    public static final String INSTANCE_BOOK_ID = "instance_book_id";
    private static final String FILE_PROVIDER_AUTHORITY = "aar92_22.library.fileprovider";

    private static final String WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String READ_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;

    private static final String [] PERMISSIONS = new String[]{
            WRITE_PERMISSION, READ_PERMISSION, CAMERA_PERMISSION };

    private static final int REQUEST_CODE_PERMISSION_CAMERA = 2;
    private static final int REQUEST_CODE_PERMISSION_SELECT_IMAGE = 4;


    private static final int DEFAULT_BOOK_ID = -1 ;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_IMAGE = 3;

    private int bookId = DEFAULT_BOOK_ID;
    private String mCategory;
    private String mTempPhotoPath;
    private Bitmap mResultsBitmap;

    EditText mTitle;
    EditText mLastName;
    EditText mFirstName;
    EditText mLastName2;
    EditText mFirstName2;
    EditText mLastName3;
    EditText mFirstName3;
    EditText mPublisher;
    EditText mPublishedDate;
    EditText mNumberPages;
    EditText mSeries;
    EditText mVolume;
    EditText mSummary;
    Spinner category;
    Button camera;
    Button rotateImage;
    ImageView mBookCover;
    LinearLayout author2;
    LinearLayout author3;

    BookDataBase mDb;
    CategoryDataBase categoryDataBase;

    ArrayList <String> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        //Load ad
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null ) actionBar.setDisplayHomeAsUpEnabled(true);


        mTitle = findViewById(R.id.title_input);
        mLastName = findViewById(R.id.last_name_input);
        mFirstName = findViewById(R.id.first_name_input);
        mLastName2 = findViewById(R.id.last_name_input2);
        mFirstName2 = findViewById(R.id.first_name_input2);
        mLastName3 = findViewById(R.id.last_name_input3);
        mFirstName3 = findViewById(R.id.first_name_input3);
        mPublisher = findViewById(R.id.publisher_input);
        mPublishedDate = findViewById(R.id.published_date_input);
        mNumberPages = findViewById(R.id.number_pages_input);
        mSeries = findViewById(R.id.series_input);
        mVolume = findViewById(R.id.volume_input);
        mSummary = findViewById(R.id.summary_input);
        category = findViewById(R.id.category_spinner);
        author2 = findViewById(R.id.author_layout2);
        author3 = findViewById(R.id.author_layout3);
        camera = findViewById(R.id.take_photo);
        rotateImage = findViewById(R.id.rotate_image);
        mBookCover = findViewById(R.id.book_cover_input);


        mDb = BookDataBase.getsInstance(getApplicationContext());

        categoryDataBase = CategoryDataBase.getsInstance(getApplicationContext());
        category.setOnItemSelectedListener(this);
        setUpCategories();

        if ( savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_BOOK_ID) ){
            bookId = savedInstanceState.getInt(INSTANCE_BOOK_ID,DEFAULT_BOOK_ID);
        }


        Intent intent = getIntent();

        if(intent != null){
            if(intent.hasExtra(BOOK_ID) && bookId == DEFAULT_BOOK_ID){

                bookId = intent.getIntExtra(BOOK_ID,DEFAULT_BOOK_ID);

                final AddBookViewModel viewModel = new AddBookViewModel(getApplication(), mDb, bookId);
                viewModel.getBooks().observe(this, new Observer<BookEntry>() {
                    @Override
                    public void onChanged(@Nullable BookEntry bookEntry) {
                        viewModel.getBooks().removeObserver(this);
                        populateUI(bookEntry);
                    }
                });

            } else if(intent.hasExtra(BookDetailActivity.EXTRA_BOOK_ENTRY) &&
                    intent.getBooleanExtra(BookDetailActivity.EXTRA_FROM_SEARCH, false)){

                Bundle receivedBundle = intent.getBundleExtra(BookDetailActivity.EXTRA_BOOK_ENTRY);
                if(receivedBundle != null) {
                    String title = receivedBundle.getString(TITLE_SEARCH);
                    String firstName = receivedBundle.getString(FIRST_NAME_SEARCH);
                    String lastName = receivedBundle.getString(LAST_NAME_SEARCH);
                    String publisher = receivedBundle.getString(PUBLISHER_SEARCH);
                    String publishedDate = receivedBundle.getString(PUBLISHED_DATE_SEARCH);
                    String categoryFromBundle = receivedBundle.getString(CATEGORY_SEARCH);
                    String summary = receivedBundle.getString(SUMMARY_SEARCH);
                    int numberPages = receivedBundle.getInt(NUMBER_PAGES_SEARCH);
                    byte[] bookCover = receivedBundle.getByteArray(BOOK_COVER_SEARCH);

                    BookEntry bookEntry = new BookEntry(title, lastName, firstName, "",
                            "", "", "", publisher,
                            publishedDate, numberPages, "", "",
                            categoryFromBundle, summary, bookCover, null);
                    populateUI(bookEntry);
                }
            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_book_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_done:
                if(!(String.valueOf(mTitle.getText()).equals("") || String.valueOf(mLastName.getText()).equals(""))
                        && checkNumberPages()){
                    saveData();
                } else if (String.valueOf(mTitle.getText()).equals("")){
                    Toast.makeText(this,R.string.complete_title_warning, Toast.LENGTH_LONG).show();
                } else if(String.valueOf(mLastName.getText()).equals("")){
                    Toast.makeText(this,R.string.complete_last_name_warning, Toast.LENGTH_LONG).show();
                }
                return true;

            default:  return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mCategory = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if(grantResults.length > 0){
            for(int result : grantResults){
                if(result != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            switch (requestCode){
                case REQUEST_CODE_PERMISSION_SELECT_IMAGE:
                    selectImage();
                    break;

                case REQUEST_CODE_PERMISSION_CAMERA:
                    launchCamera();
                    break;
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            processAndSetImage();
        }
        if(requestCode == SELECT_IMAGE && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            String realPath = getPath(this.getApplicationContext(),selectedImageUri);
            if(realPath.equals("Not found")){
                Toast.makeText(this, getString(R.string.error_image_message),Toast.LENGTH_LONG).show();
            }else{
                processReceivedImageFromDevice(realPath);
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_BOOK_ID, bookId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onPause();
    }

    @Override
    public void selectImageListener() {
        if(ContextCompat.checkSelfPermission(this, WRITE_PERMISSION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, READ_PERMISSION) ==
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) ==
                        PackageManager.PERMISSION_GRANTED){
            selectImage();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSION_SELECT_IMAGE);
        }

    }

    @Override
    public void takePhotoListener() {
        if(ContextCompat.checkSelfPermission(this, WRITE_PERMISSION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, READ_PERMISSION) ==
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) ==
                        PackageManager.PERMISSION_GRANTED){
            launchCamera();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSION_CAMERA);
        }
    }


    public void clickAddAuthor(View view) {
        if(author2.getVisibility() == View.GONE && author3.getVisibility() == View.GONE){
            author2.setVisibility(View.VISIBLE);
        } else if ( author2.getVisibility() == View.VISIBLE && author3.getVisibility() == View.GONE){
            author3.setVisibility(View.VISIBLE);
        }
    }

    public void clickRemoveAuthor2(View view) {
        mLastName2.setText("");
        mFirstName2.setText("");
        author2.setVisibility(View.GONE);
    }

    public void clickRemoveAuthor3(View view) {
        mLastName3.setText("");
        mFirstName3.setText("");
        author3.setVisibility(View.GONE);
    }

    public void clickTakePhoto(View view) {
        Dialogs.selectImageOptions(this, this);
    }

    public void clickRotateImage(View view) {
        Bitmap rotatedImage;
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        rotatedImage = Bitmap.createBitmap(mResultsBitmap, 0, 0, mResultsBitmap.getWidth(), mResultsBitmap.getHeight(), matrix, true);
        mResultsBitmap = rotatedImage;
        mBookCover.setImageBitmap(mResultsBitmap);

    }

    private void setUpCategories(){
        categoryList = new ArrayList<>();
        CategoryViewModel categoryViewModel = new CategoryViewModel(getApplication());
        categoryViewModel.getCategoryEntries().observe(this, new Observer<List<CategoryEntry>>() {
            @Override
            public void onChanged(List<CategoryEntry> categoryEntries) {
                for (CategoryEntry categoryEntry : categoryEntries) {
                    categoryList.add(categoryEntry.getCategory());
                }
                setUpAdapter();
            }
        });
    }

    private void setUpAdapter(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setSelection(0);
    }

    private void launchCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = BitmapUtils.createTempImageFile(this);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                mTempPhotoPath = photoFile.getAbsolutePath();

                Uri photoURI = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY,
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void selectImage(){
        Intent selectImage = new Intent(Intent.ACTION_GET_CONTENT);
        selectImage.setType("image/*");
        startActivityForResult(selectImage,SELECT_IMAGE);
    }


    public static String getPath(Context context, Uri uri ) {
        String result = null;
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, projection, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( projection[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }



    private void processAndSetImage() {
        mResultsBitmap = BitmapUtils.resamplePic(this, mTempPhotoPath);
        mBookCover.setImageBitmap(mResultsBitmap);
        rotateImage.setVisibility(View.VISIBLE);

    }

    private void processReceivedImageFromDevice(String path){
        mResultsBitmap = BitmapUtils.resamplePic(this, path);
        mBookCover.setImageBitmap(mResultsBitmap);
        rotateImage.setVisibility(View.VISIBLE);

    }


    private void populateUI(BookEntry bookEntry){
        if(bookEntry == null ){
            return;
        }

        mTitle.setText(bookEntry.getTitle());
        mLastName.setText(bookEntry.getLastName());
        mFirstName.setText(bookEntry.getFirstName());

        if(!(bookEntry.getLastName2().equals("") && bookEntry.getFirstName2().equals(""))){
            author2.setVisibility(View.VISIBLE);
            mLastName2.setText(bookEntry.getLastName2());
            mFirstName2.setText(bookEntry.getFirstName2());

        }else if(!(bookEntry.getLastName3().equals("") && bookEntry.getFirstName2().equals("") )){
            author3.setVisibility(View.VISIBLE);
            mLastName3.setText(bookEntry.getLastName3());
            mFirstName3.setText(bookEntry.getFirstName3());
        }

        mPublisher.setText(bookEntry.getPublisher());
        mPublishedDate.setText(bookEntry.getPublishedDate());
        mNumberPages.setText(String.valueOf(bookEntry.getNumberPages()));
        mSeries.setText(bookEntry.getSeries());
        mVolume.setText(bookEntry.getVolume());

        for(int i = 0; i < categoryList.size(); i++){
            if(categoryList.get(i).equals(bookEntry.getCategory())){
                category.setSelection(i);
                break;
            } else {
                category.setSelection(0);
            }
        }

        mSummary.setText(bookEntry.getSummary());

        if(bookEntry.getBookCover() != null) {
            byte[] imageInBytes = bookEntry.getBookCover();
            mResultsBitmap = BitmapFactory.decodeByteArray(imageInBytes, 0, imageInBytes.length);
            mBookCover.setImageBitmap(mResultsBitmap);
        }

    }


    public void saveData() {
        String title = String.valueOf(mTitle.getText());
        String lastName = String.valueOf(mLastName.getText());
        String firstName = String.valueOf(mFirstName.getText());
        String lastName2 = String.valueOf(mLastName2.getText());
        String firstName2 = String.valueOf(mFirstName2.getText());
        String lastName3 = String.valueOf(mLastName3.getText());
        String firstName3 = String.valueOf(mFirstName3.getText());
        String publisher = String.valueOf(mPublisher.getText());
        String publishedDate = String.valueOf(mPublishedDate.getText());
        int numberPages;
        if(!(String.valueOf(mNumberPages.getText()).equals("")) ) {
            numberPages = Integer.parseInt(String.valueOf(mNumberPages.getText()));
        }else{
            numberPages = 0;
        }
        String series = String.valueOf(mSeries.getText());
        String volume = String.valueOf(mVolume.getText());
        String summary = String.valueOf(mSummary.getText());

        byte[] imageInBytes;
        if(mResultsBitmap != null) {
            ByteArrayOutputStream objectByteArrayOutputStream = new ByteArrayOutputStream();
            mResultsBitmap.compress(Bitmap.CompressFormat.JPEG, 50, objectByteArrayOutputStream);
            imageInBytes = objectByteArrayOutputStream.toByteArray();

        }else {
            imageInBytes = null;
        }

        Date date = new Date();

        final BookEntry bookEntry = new BookEntry(title,lastName, firstName, lastName2, firstName2,
                lastName3, firstName3, publisher, publishedDate, numberPages, series, volume,
                mCategory, summary, imageInBytes, date);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(bookId == DEFAULT_BOOK_ID) {
                    mDb.bookDao().insertBook(bookEntry);
                } else {
                    bookEntry.setId(bookId);
                    mDb.bookDao().updateBook(bookEntry);
                }

            }
        });

        finish();
    }


    private boolean checkNumberPages(){
        if(!(String.valueOf(mNumberPages.getText()).equals("")) ) {
            try {
                Integer.parseInt(String.valueOf(mNumberPages.getText()));
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.error_only_numbers), Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }


}
