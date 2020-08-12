package aar92_22.library.ViewModel;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import aar92_22.library.Database.BookDataBase;
import aar92_22.library.Database.BookEntry;

public class AddBookViewModel extends AndroidViewModel {

    private LiveData<BookEntry> book;

    public AddBookViewModel (@NonNull Application application, BookDataBase dataBase, int bookId){
        super(application);
        book = dataBase.bookDao().loadBookById(bookId);
    }

    public LiveData<BookEntry> getBooks(){
        return book;
    }


}
