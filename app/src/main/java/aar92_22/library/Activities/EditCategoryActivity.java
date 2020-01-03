package aar92_22.library.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

import aar92_22.library.AppExecutors;
import aar92_22.library.CategoryListAdapter;
import aar92_22.library.Database.CategoryDataBase;
import aar92_22.library.Database.CategoryEntry;
import aar92_22.library.R;
import aar92_22.library.ViewModel.CategoryViewModel;

public class EditCategoryActivity extends AppCompatActivity implements CategoryListAdapter.categoryOptionsListener{


    public static final int DEFAULT_CATEGORY_ID = -1;

    Toolbar toolbar;
    RecyclerView categoriesList;

    CategoryDataBase categoryDataBase;

    CategoryListAdapter mAdapter;

    String categoryName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        //Load ad
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();


        if(actionBar != null ){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        categoryDataBase = CategoryDataBase.getsInstance(this);

        categoriesList = findViewById(R.id.category_recycler_view);

        setCategoriesList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_category_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.add_category:
                showTheDialog(DEFAULT_CATEGORY_ID);
                return true;

        }


        return super.onOptionsItemSelected(item);
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

    private void showTheDialog(final int categoryId){
        LayoutInflater inflater = LayoutInflater.from(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = inflater.inflate(R.layout.add_category_dialog,null);

        final EditText categoryNameET = view.findViewById(R.id.category_name_edit_text );
        TextView titleTv = view.findViewById(R.id.category_dialog_title);

        if(categoryId != DEFAULT_CATEGORY_ID){
            titleTv.setText(getString(R.string.rename_title));
            AppExecutors.getInstance().otherIO().execute(new Runnable() {
                @Override
                public void run() {
                    CategoryEntry categoryEntry = categoryDataBase.categoryDao().loadCategoryById(categoryId);
                    String name = categoryEntry.getCategory();
                    categoryNameET.setText(name);
                }
            });

        } else {
            titleTv.setText(getString(R.string.add_new_category));
        }

        builder.setView(view);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                categoryName = String.valueOf(categoryNameET.getText());
                addNewCategory(categoryName, categoryId);
                dialog.dismiss();
            }

        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void addNewCategory(String categoryName, final int categoryId){
        final CategoryEntry categoryEntry = new CategoryEntry(categoryName);
        AppExecutors.getInstance().otherIO().execute(new Runnable() {
            @Override
            public void run() {
                if(categoryId == DEFAULT_CATEGORY_ID) {
                    categoryDataBase.categoryDao().newCategory(categoryEntry);
                } else{
                    categoryEntry.setId(categoryId);
                    categoryDataBase.categoryDao().updateCategory(categoryEntry);
                }
            }

        });

        setCategoriesList();
    }


    @Override
    public void onEditClick(View view, final int id) {
        showTheDialog(id);
    }

    @Override
    public void onDeleteClick(View view, final int id) {
        AppExecutors.getInstance().otherIO().execute(new Runnable() {
            @Override
            public void run() {
                CategoryEntry categoryEntry = categoryDataBase.categoryDao().loadCategoryById(id);
                categoryDataBase.categoryDao().deleteCategory(categoryEntry);
            }
        });

        setCategoriesList();

    }
}
