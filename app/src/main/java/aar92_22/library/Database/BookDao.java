package aar92_22.library.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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
