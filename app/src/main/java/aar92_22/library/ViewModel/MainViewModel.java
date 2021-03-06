package aar92_22.library.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.List;

import aar92_22.library.Database.BookDataBase;
import aar92_22.library.Database.BookEntry;

public class MainViewModel extends AndroidViewModel {


    private LiveData<List<BookEntry>> books;



    public MainViewModel(@NonNull Application application, String sort) {
        super(application);
        SimpleSQLiteQuery query = new SimpleSQLiteQuery("SELECT * FROM books ORDER BY LOWER(" + sort + ")");
        BookDataBase dataBase = BookDataBase.getsInstance(application);
            books = dataBase.bookDao().loadAllBooks(query);
    }



    public LiveData<List<BookEntry>> getBooks(){
            return books;
    }



}
