package aar92_22.library.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface BookDao  {

    @Query("SELECT * FROM books")
    LiveData<List<BookEntry>> loadAllBooks ();

    @Insert
    void insertBook ( BookEntry bookEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateBook (BookEntry bookEntry);

    @Delete
    void deleteBook (BookEntry bookEntry);

    @Query("SELECT * FROM books WHERE id = :id")
    LiveData<BookEntry> loadBookById (int id);

    @Query("SELECT * FROM books WHERE id = :id")
    BookEntry loadBookByIdIndividual(int id);




}
