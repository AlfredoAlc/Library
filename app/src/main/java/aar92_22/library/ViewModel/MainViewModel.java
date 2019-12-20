package aar92_22.library.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import aar92_22.library.Database.AppDataBase;
import aar92_22.library.Database.BookEntry;

public class MainViewModel extends AndroidViewModel {


    private LiveData<List<BookEntry>> books;


    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDataBase dataBase = AppDataBase.getsInstance(this.getApplication());
            books = dataBase.bookDao().loadAllBooks();
    }

    public LiveData<List<BookEntry>> getBooks(){
            return books;
    }



}
