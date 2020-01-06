package aar92_22.library.Activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import aar92_22.library.AppExecutors;
import aar92_22.library.BookListAdapter;
import aar92_22.library.CSVReader;
import aar92_22.library.CSVWriter;
import aar92_22.library.Database.AppDataBase;
import aar92_22.library.Database.BookEntry;
import aar92_22.library.Database.CategoryDataBase;
import aar92_22.library.Database.CategoryEntry;
import aar92_22.library.Fragments.SettingsFragment;
import aar92_22.library.ModuleViewDecoration;
import aar92_22.library.R;
import aar92_22.library.ViewModel.MainViewModel;


public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener, BookListAdapter.ListBookClickListener,
        BookListAdapter.BookLongClickListener, NavigationView.OnNavigationItemSelectedListener {


    private static final String EXTRA_CHANGE_VIEW_BOOLEAN = "change_view";
    private static final int REQUEST_CODE_FILTER = 11;
    private int bookId;
    private boolean listView;
    private boolean authorFilterActivated;
    private boolean categoryFilterActivated;
    private boolean seriesFilterActivated;
    private String authorFilter;
    private String categoryFilter;
    private String seriesFilter;
    boolean first;
    boolean filtersActivated = false;
    String sortBy;
    String libraryName;

    private AppDataBase mDb;
    private CategoryDataBase categoryDataBase;

    SharedPreferences sharedPreferences;
    RecyclerView bookList;
    BookListAdapter mAdapter;

    FloatingActionButton addBookFab;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    NavigationView navigationView;
    SwipeRefreshLayout pullToRefresh;

    List<BookEntry> listToFilter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load ad
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        bookList = findViewById(R.id.list_books_recycler_view);

        mDb = AppDataBase.getsInstance(this);
        categoryDataBase = CategoryDataBase.getsInstance(this);

        addBookFab = findViewById(R.id.add_book_fab);

        addBookFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (MainActivity.this, AddBookActivity.class);
                startActivity(intent);
            }
        });

        toolbar = findViewById(R.id.mainToolbar);
        toolbar.setTitle(getString(R.string.library_string));
        setSupportActionBar(toolbar);




        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);



        if(savedInstanceState != null){

            listView = savedInstanceState.getBoolean(EXTRA_CHANGE_VIEW_BOOLEAN);

            if(listView){
                setUpLinearLayout();
            }else{
                setUpGridLayout();
            }


            setUpViewModel(sharedPreferences);

        }else {
            listView = true;
            LinearLayoutManager linearLayout = new LinearLayoutManager(this);
            bookList.setLayoutManager(linearLayout);
            bookList.setHasFixedSize(true);
            mAdapter = new BookListAdapter(this, this,
                    this, listView);
            bookList.setAdapter(mAdapter);

            setUpViewModel(sharedPreferences);

        }



        first = SettingsFragment.getCategoryValue(this);

        if(first){
            firstCategories();
            SettingsFragment.firstCategoryList(this);
        }

        pullToRefresh = findViewById(R.id.pull_to_refresh);

            pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if(!filtersActivated) {
                        setUpViewModel(sharedPreferences);
                    }
                    pullToRefresh.setRefreshing(false);

                }
            });


        setToolbarName(sharedPreferences);
    }



    private void setUpLinearLayout (){

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        bookList.setLayoutManager(linearLayout);
        bookList.setHasFixedSize(true);
        mAdapter = new BookListAdapter(this, this,
                this, listView);

        bookList.setAdapter(mAdapter);

    }

    private void setUpGridLayout (){

        GridLayoutManager gridLayout = new GridLayoutManager(this, 3);
        bookList.setLayoutManager(gridLayout);
        ModuleViewDecoration moduleViewDecoration = new ModuleViewDecoration(this, R.dimen.module_padding);
        bookList.addItemDecoration(moduleViewDecoration);
        bookList.setHasFixedSize(true);
        mAdapter = new BookListAdapter(this, this,
                this, listView);

        bookList.setAdapter(mAdapter);

    }

    private void setUpViewModel(SharedPreferences sharedPreferences){
        sortBy = sharedPreferences.getString(getString(R.string.sort_by_key), getString(R.string.date_added) );
        MainViewModel viewModel = new MainViewModel(getApplication(),sortBy);
        viewModel.getBooks().observe(MainActivity.this, new Observer<List<BookEntry>>() {
            @Override
            public void onChanged(@Nullable List<BookEntry> bookEntries) {
                mAdapter.setBookEntry(bookEntries);
                listToFilter = bookEntries;
            }
        });
    }


    private void firstCategories(){

        final List<String> firstList = new ArrayList<>();

        firstList.add("Others");
        firstList.add("Art & Design");
        firstList.add("Business & Financial");
        firstList.add("Children's Books");
        firstList.add("Comics & Graphic Novels");
        firstList.add("Education & Engineering");
        firstList.add("History");
        firstList.add("Literature & Fiction");
        firstList.add("Politics & Social Sciences");
        firstList.add("Travel");


        AppExecutors.getInstance().otherIO().execute(new Runnable() {

            @Override
            public void run() {

                for(int i = 0; i<firstList.size(); i++) {
                    CategoryEntry categoryEntry = new CategoryEntry(firstList.get(i));
                    categoryDataBase.categoryDao().newCategory(categoryEntry);
                }

            }
        });
    }

    private void setToolbarName (SharedPreferences sharedPreferences){
        libraryName = sharedPreferences.getString(
                getString(R.string.library_name_key), getString(R.string.library_string) );

        toolbar.setTitle(libraryName);

    }

    private void setFilter(){


        BookEntry entry;
        List<BookEntry> result = new ArrayList<>();

        if(listToFilter != null){

            for(int i = 0; i<listToFilter.size(); i++){

                entry = listToFilter.get(i);

                if(authorFilterActivated && categoryFilterActivated && seriesFilterActivated){
                    if(entry.getLastName().equals(authorFilter) &&
                            entry.getCategory().equals(categoryFilter) &&
                            entry.getSeries().equals(seriesFilter)){
                        result.add(entry);
                    }
                }else if (authorFilterActivated && categoryFilterActivated && !seriesFilterActivated){
                    if(entry.getLastName().equals(authorFilter) &&
                            entry.getCategory().equals(categoryFilter)){
                        result.add(entry);
                    }

                }else if (authorFilterActivated && !categoryFilterActivated && seriesFilterActivated){
                    if(entry.getLastName().equals(authorFilter) &&
                            entry.getSeries().equals(seriesFilter)){
                        result.add(entry);
                    }

                }else if (authorFilterActivated && !categoryFilterActivated && !seriesFilterActivated){
                    if(entry.getLastName().equals(authorFilter)){
                        result.add(entry);
                    }

                }else if (!authorFilterActivated && categoryFilterActivated && seriesFilterActivated){
                    if(entry.getCategory().equals(categoryFilter) &&
                            entry.getSeries().equals(seriesFilter)){
                        result.add(entry);
                    }

                }else if (!authorFilterActivated && categoryFilterActivated && !seriesFilterActivated){
                    if(entry.getCategory().equals(categoryFilter)){
                        result.add(entry);
                    }

                }else if (!authorFilterActivated && !categoryFilterActivated && seriesFilterActivated){
                    if(entry.getSeries().equals(seriesFilter)){
                        result.add(entry);
                    }

                }

            }

        }


        mAdapter.setBookEntry(result);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main_activity,menu);

        MenuItem searchItem = menu.findItem(R.id.search_menu);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId){
            case R.id.change_view_menu:

                if(listView){
                    listView = false;
                    item.setIcon(R.drawable.ic_view_list);
                    setUpGridLayout();
                    setUpViewModel(sharedPreferences);
                } else {
                    listView = true;
                    item.setIcon(R.drawable.ic_view_module);
                    setUpLinearLayout();
                    setUpViewModel(sharedPreferences);
                }

                return true;



            case R.id.filter_menu:

                Intent intent = new Intent(MainActivity.this, FilterActivity.class);

                if(authorFilterActivated){
                    intent.putExtra(FilterActivity.AUTHOR_FILTER,authorFilter);
                }
                if(categoryFilterActivated){
                    intent.putExtra(FilterActivity.CATEGORY_FILTER,categoryFilter);
                }
                if(seriesFilterActivated){
                    intent.putExtra(FilterActivity.SERIES_FILTER,seriesFilter);
                }

                startActivityForResult(intent,REQUEST_CODE_FILTER);

                return true;

            default: return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key.equals(getString(R.string.library_name_key))){
            setToolbarName(sharedPreferences);
        } else if(key.equals(getString(R.string.sort_by_key))){
            setUpViewModel(sharedPreferences);
        }

    }

    @Override
    public void onListBookClick(final int id) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                BookEntry bookEntry = mDb.bookDao().loadBookByIdIndividual(id);
                Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
                intent.putExtra(BookDetailActivity.EXTRA_ID,bookEntry.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int itemId = menuItem.getItemId();

        switch (itemId){

            case R.id.setting_drawer_activity:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.export_library:

                AlertDialog.Builder exportBuilder = new AlertDialog.Builder(this);
                exportBuilder.setMessage(R.string.warning_exporting_message);
                exportBuilder.setCancelable(true);

                exportBuilder.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String path = String.valueOf(
                                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));

                                File exportDir = new File(path, "");
                                if (!exportDir.exists()) {
                                    exportDir.mkdirs();
                                }

                                File file = new File(exportDir, libraryName + ".csv");
                                try {
                                    file.createNewFile();
                                    CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                                    Cursor curCSV = mDb.query("SELECT * FROM books", null);
                                    csvWrite.writeNext(curCSV.getColumnNames());
                                    while (curCSV.moveToNext()) {
                                        //Which column you want to export
                                        String arrStr[] = new String[curCSV.getColumnCount()];
                                        for (int i = 0; i < curCSV.getColumnCount() - 1; i++)
                                            arrStr[i] = curCSV.getString(i);
                                        csvWrite.writeNext(arrStr);
                                    }
                                    csvWrite.close();
                                    curCSV.close();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }



                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.export_confirmed), Toast.LENGTH_LONG).show();
                                dialog.cancel();
                            }

                        });

                exportBuilder.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = exportBuilder.create();
                alertDialog.show();




                break;

            case R.id.import_library:

                AlertDialog.Builder importBuilder = new AlertDialog.Builder(this);
                importBuilder.setMessage(R.string.warning_importing_message);
                importBuilder.setCancelable(true);

                importBuilder.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int num) {

                                try{


                                    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +"/"+ libraryName +".csv";
                                    File file = new File(path);
                                    FileReader reader = new FileReader(file);

                                    CSVReader csvReader = new CSVReader(reader);

                                    String[] line;


                                    csvReader.readNext();
                                    try{
                                        while ( (line = csvReader.readNext()) != null){

                                            int id;
                                            String title;
                                            String lastName;
                                            String firstName;
                                            String lastName2;
                                            String firstName2;
                                            String lastName3;
                                            String firstName3;
                                            String publisher;
                                            String publishedDate;
                                            int numberPages;
                                            String series;
                                            String volume;
                                            String category;
                                            String summary;
                                            byte [] bookCover;
                                            Date date = new Date ();

                                            id = Integer.parseInt(line[0]);
                                            title = line [1];
                                            if(line[2].length()>0){
                                                lastName = line[2];
                                            }else{
                                                lastName = "";
                                            }

                                            if(line[3].length()>0){
                                                firstName = line[3];
                                            }else{
                                                firstName="";
                                            }

                                            if(line[4].length()>0){
                                                lastName2 = line[4];
                                            }else{
                                                lastName2="";
                                            }

                                            if(line[5].length()>0){
                                                firstName2 = line[5];
                                            }else{
                                                firstName2="";
                                            }

                                            if(line[6].length()>0){
                                                lastName3 = line[6];
                                            }else{
                                                lastName3="";
                                            }

                                            if(line[7].length()>0){
                                                firstName3 = line[7];
                                            }else{
                                                firstName3="";
                                            }

                                            if(line[8].length()>0){
                                                publisher = line[8];
                                            }else{
                                                publisher="";
                                            }

                                            if(line[9].length()>0){
                                                publishedDate = line[9];
                                            }else{
                                                publishedDate="";
                                            }

                                            if(line[10].length()>0){
                                                numberPages = Integer.parseInt(line[10]);
                                            }else{
                                                numberPages=0;
                                            }

                                            if(line[11].length()>0){
                                                series = line[11];
                                            }else{
                                                series="";
                                            }

                                            if(line[12].length()>0){
                                                volume = line[12];
                                            }else{
                                                volume="";
                                            }

                                            if(line[13].length()>0){
                                                category = line[13];
                                            }else{
                                                category="";
                                            }

                                            if(line[14].length()>0){
                                                summary = line[14];
                                            }else{
                                                summary="";
                                            }

                                            if(line[15].length()>0){
                                                bookCover = line[15].getBytes();
                                            }else{
                                                bookCover = null;
                                            }

                                            final BookEntry bookEntry = new BookEntry(id, title,lastName, firstName, lastName2, firstName2,
                                                    lastName3, firstName3, publisher, publishedDate, numberPages, series, volume,
                                                    category, summary, bookCover, date);

                                            AppExecutors.getInstance().otherIO().execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mDb.bookDao().insertNewLibrary(bookEntry);
                                                }
                                            });

                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(),getString(R.string.import_error), Toast.LENGTH_LONG).show();
                                } catch (IOException e){
                                    e.printStackTrace();
                                } catch (NullPointerException ex){
                                    Log.wtf("Exception", "", ex);
                                }



                                dialog.cancel();
                            }
                        });

                importBuilder.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog importDialog = importBuilder.create();
                importDialog.show();




                break;


            case R.id.info:

                Intent aboutIntent =  new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutIntent);

                break;






        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    @Override
    public void onLongBookClick(int id) {
        this.bookId = id;

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case 1:
                Intent intent = new Intent(this, AddBookActivity.class);
                intent.putExtra(AddBookActivity.BOOK_ID, bookId );
                startActivity(intent);
                break;

            case 2:
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
                break;

        }

        return true;



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FILTER) {

            if(resultCode == RESULT_OK) {

                if (data != null) {

                    filtersActivated = true;

                    authorFilterActivated = data.getBooleanExtra(FilterActivity.AUTHOR_FILTER_ACTIVATED, false);
                    categoryFilterActivated = data.getBooleanExtra(FilterActivity.CATEGORY_FILTER_ACTIVATED, false);
                    seriesFilterActivated = data.getBooleanExtra(FilterActivity.SERIES_FILTER_ACTIVATED, false);

                    if (authorFilterActivated) {
                        authorFilter = data.getStringExtra(FilterActivity.AUTHOR_FILTER);
                    }
                    if (categoryFilterActivated) {
                        categoryFilter = data.getStringExtra(FilterActivity.CATEGORY_FILTER);
                    }
                    if (seriesFilterActivated) {
                        seriesFilter = data.getStringExtra(FilterActivity.SERIES_FILTER);
                    }

                    setFilter();

                }

            }

            if(resultCode == RESULT_CANCELED){
                filtersActivated = false;
                authorFilterActivated = false;
                categoryFilterActivated = false;
                seriesFilterActivated = false;
                setUpViewModel(sharedPreferences);
            }
        }



    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).
                unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroy();
    }


}

