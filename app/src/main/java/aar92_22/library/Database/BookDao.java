package aar92_22.library.Database;

import android.renderscript.Sampler;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.lang.reflect.Array;
import java.util.List;


@Dao
public interface BookDao  {




    @RawQuery(observedEntities = BookEntry.class)
    LiveData<List<BookEntry>> loadAllBooks (SupportSQLiteQuery query);

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNewLibrary(BookEntry bookEntry);


}
