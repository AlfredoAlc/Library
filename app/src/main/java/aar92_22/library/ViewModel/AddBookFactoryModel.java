package aar92_22.library.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import aar92_22.library.Database.AppDataBase;

public class AddBookFactoryModel extends ViewModelProvider.NewInstanceFactory {


    private final AppDataBase mDb;
    private final int bookId;


    public AddBookFactoryModel(AppDataBase mDb, int bookId) {
        this.mDb = mDb;
        this.bookId = bookId;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddBookViewModel(mDb,bookId);
    }
}
