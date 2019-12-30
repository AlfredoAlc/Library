package aar92_22.library.Activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

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

import java.util.ArrayList;
import java.util.List;

import aar92_22.library.AppExecutors;
import aar92_22.library.BookListAdapter;
import aar92_22.library.Database.AppDataBase;
import aar92_22.library.Database.BookEntry;
import aar92_22.library.Database.CategoryDataBase;
import aar92_22.library.Database.CategoryEntry;
import aar92_22.library.ModuleViewDecoration;
import aar92_22.library.R;
import aar92_22.library.ViewModel.MainViewModel;

import static com.google.android.gms.ads.AdSize.SMART_BANNER;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener, BookListAdapter.ListBookClickListener,
        BookListAdapter.BookLongClickListener, NavigationView.OnNavigationItemSelectedListener {


    private static final String EXTRA_CHANGE_VIEW_BOOLEAN = "change_view";
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


        //Load ad
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setAdSize(SMART_BANNER);
        mAdView.setAdUnitId(String.valueOf(R.string.banner_ad_unit_id));
        mAdView.loadAd(adRequest);


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



        first = sharedPreferences.getBoolean(getString(R.string.check_first_category_list_key), true);

        if(first){
            firstCategories();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.check_first_category_list_key), false);
            editor.apply();

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
        sortBy = sharedPreferences.getString(getString(R.string.sort_by_key), getString(R.string.title) );
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
        toolbar.setTitle(sharedPreferences.getString(
                getString(R.string.library_name_key),""));

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

                startActivityForResult(intent,100);

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

        if(itemId == R.id.setting_drawer_activity){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
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

        if (requestCode == 100) {

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

