package aar92_22.library.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import aar92_22.library.Database.CategoryDataBase;
import aar92_22.library.Database.CategoryEntry;

public class CategoryViewModel extends AndroidViewModel {

    private LiveData<List<CategoryEntry>> categoryEntries;


    public CategoryViewModel(@NonNull Application application) {
        super(application);
        CategoryDataBase dataBase = CategoryDataBase.getsInstance(this.getApplication());
        categoryEntries = dataBase.categoryDao().loadAllCategories();
    }


    public LiveData<List<CategoryEntry>> getCategoryEntries(){

        return categoryEntries;
    }





}
