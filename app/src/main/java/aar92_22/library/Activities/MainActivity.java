package aar92_22.library.Activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        BookListAdapter.BookLongClickListener {


    private static final String EXTRA_CHANGE_VIEW_BOOLEAN = "change_view";
    private int bookId;
    private boolean listView;
    private boolean filterActivated;
    private String authorFilter;

    private AppDataBase mDb;
    private CategoryDataBase categoryDataBase;

    private AppBarConfiguration mAppBarConfiguration;

    RecyclerView bookList;
    BookListAdapter mAdapter;

    FloatingActionButton addBookFab;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    NavController navController;

    boolean first;



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

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_main, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();



        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        //Load ad
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setAdSize(SMART_BANNER);
        mAdView.setAdUnitId(String.valueOf(R.string.banner_ad_unit_id));
        mAdView.loadAd(adRequest);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        sharedPreferences.getString(getString(R.string.sort_by_key), "" );
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);


        if(savedInstanceState != null){

            listView = savedInstanceState.getBoolean(EXTRA_CHANGE_VIEW_BOOLEAN);

            if(listView){
                setUpLinearLayout();
            }else{
                setUpGridLayout();
            }


            setUpViewModel();

        }else {
            listView = true;
            LinearLayoutManager linearLayout = new LinearLayoutManager(this);
            bookList.setLayoutManager(linearLayout);
            bookList.setHasFixedSize(true);
            mAdapter = new BookListAdapter(this, this,
                    this, listView);

            bookList.setAdapter(mAdapter);
            setUpViewModel();

        }



        first = sharedPreferences.getBoolean(getString(R.string.check_first_category_list_key), true);

        if(first){
            firstCategories();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.check_first_category_list_key), false);
            editor.apply();

        }


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

    private void setUpViewModel(){
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getBooks().observe(this, new Observer<List<BookEntry>>() {
            @Override
            public void onChanged(@Nullable List<BookEntry> bookEntries) {
                mAdapter.setBookEntry(bookEntries);
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
                    setUpViewModel();
                } else {
                    listView = true;
                    item.setIcon(R.drawable.ic_view_module);
                    setUpLinearLayout();
                    setUpViewModel();
                }

                return true;



            case R.id.filter_menu:

                if(filterActivated){
                    Intent intent = new Intent(MainActivity.this, FilterActivity.class);
                    intent.putExtra(FilterActivity.AUTHOR_FILTER,authorFilter);
                    startActivityForResult(intent,100);

                }else{
                    Intent intent = new Intent(MainActivity.this, FilterActivity.class);
                    startActivityForResult(intent,100);
                }

                return true;



            default: return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public void onListBookClick(final int id) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                BookEntry bookEntry = mDb.bookDao().loadBookByIdIndividual(id);
                Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
                intent.putExtra(BookDetailActivity.EXTRA_BOOK_TITLE,bookEntry.getTitle());
                intent.putExtra(BookDetailActivity.EXTRA_BOOK_LAST_NAME,bookEntry.getLastName());
                intent.putExtra(BookDetailActivity.EXTRA_CATEGORY, bookEntry.getCategory());
                startActivity(intent);
            }
        });
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

                if(data != null) {
                    filterActivated = true;
                    authorFilter = data.getStringExtra(FilterActivity.AUTHOR_FILTER);
                    mAdapter.setFiltered(filterActivated);
                    mAdapter.getFilter().filter(authorFilter);
                }
            }
            if(resultCode == RESULT_CANCELED){
                filterActivated = false;
                mAdapter.setFiltered(false);
                mAdapter.getFilter().filter("");
            }
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).
                unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroy();
    }



}

