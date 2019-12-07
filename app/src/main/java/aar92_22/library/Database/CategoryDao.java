package aar92_22.library.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM category")
    List<CategoryEntry> loadAllCategories ();

    @Insert
    void newCategory ( CategoryEntry category);

    @Delete
    void deleteCategory (CategoryEntry category);





}
