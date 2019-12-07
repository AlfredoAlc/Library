package aar92_22.library.ViewModel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import aar92_22.library.Database.AppDataBase;
import aar92_22.library.Database.BookEntry;

public class AddBookViewModel extends ViewModel {

    private LiveData<BookEntry> book;

    public AddBookViewModel (AppDataBase dataBase, int bookId){

        book = dataBase.bookDao().loadBookById(bookId);

    }

    public LiveData<BookEntry> getBooks(){
        return book;
    }


}
