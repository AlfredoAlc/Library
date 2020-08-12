package aar92_22.library.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

import aar92_22.library.Interfaces.CategoryListener;
import aar92_22.library.Utilities.AppExecutors;
import aar92_22.library.Adapters.CategoryListAdapter;
import aar92_22.library.Database.CategoryDataBase;
import aar92_22.library.Database.CategoryEntry;
import aar92_22.library.R;
import aar92_22.library.Utilities.Dialogs;
import aar92_22.library.Utilities.PreferenceUtilities;
import aar92_22.library.ViewModel.CategoryViewModel;

public class EditCategoryActivity extends AppCompatActivity implements
        CategoryListAdapter.categoryOptionsListener, CategoryListener {

    RecyclerView categoriesList;

    CategoryDataBase categoryDataBase;

    CategoryListAdapter mAdapter;

    PreferenceUtilities preferenceUtilities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        //Load ad
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null ) actionBar.setDisplayHomeAsUpEnabled(true);

        preferenceUtilities = new PreferenceUtilities(this);

        categoryDataBase = CategoryDataBase.getsInstance(this);
        categoriesList = findViewById(R.id.category_recycler_view);
        setCategoriesList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_category_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.add_category:
                Dialogs.addNewCategoryDialog(this, this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onPause();
    }

    /**
     *      CATEGORY OPTIONS LISTENER
     */


    @Override
    public void onEditClick(CategoryEntry categoryEntry) {
        int id = categoryEntry.getId();
        String name = categoryEntry.getCategory();
        Dialogs.renameCategoryDialog(this, this, id, name);
    }

    @Override
    public void onDeleteClick(int id) {
        Dialogs.deleteCategoryConfirmationDialog(this, this, id);
    }


    /**
     *          UTILITIES LISTENER
     */

    @Override
    public void renameCategoryListener(int id, String name) {
        final CategoryEntry categoryEntry = new CategoryEntry(name);
        categoryEntry.setId(id);

        AppExecutors.getInstance().otherIO().execute(new Runnable() {
            @Override
            public void run() {
                categoryDataBase.categoryDao().updateCategory(categoryEntry);
            }
        });
    }

    @Override
    public void addNewCategoryListener(String name) {
        final CategoryEntry categoryEntry = new CategoryEntry(name);
        AppExecutors.getInstance().otherIO().execute(new Runnable() {
            @Override
            public void run() {
                categoryDataBase.categoryDao().newCategory(categoryEntry);
            }
        });
    }

    @Override
    public void deleteCategoryListener(final int id) {
        AppExecutors.getInstance().otherIO().execute(new Runnable() {
            @Override
            public void run() {
                CategoryEntry categoryEntry = categoryDataBase.categoryDao().loadCategoryById(id);
                categoryDataBase.categoryDao().deleteCategory(categoryEntry);
            }
        });
    }

    private void setCategoriesList (){
        mAdapter = new CategoryListAdapter(this,this);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        categoriesList.setLayoutManager(linearLayout);
        categoriesList.setHasFixedSize(true);
        categoriesList.setAdapter(mAdapter);

        CategoryViewModel viewModel = new CategoryViewModel(getApplication());
        viewModel.getCategoryEntries().observe(EditCategoryActivity.this, new Observer<List<CategoryEntry>>() {
            @Override
            public void onChanged(List<CategoryEntry> categoryEntries) {
                mAdapter.setCategoryEntries(categoryEntries);
            }
        });
    }


}
