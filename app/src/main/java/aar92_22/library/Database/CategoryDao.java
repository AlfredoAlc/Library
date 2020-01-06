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
public interface CategoryDao {

    @Query("SELECT * FROM category")
    LiveData<List<CategoryEntry>> loadAllCategories();

    @Insert
    void newCategory ( CategoryEntry category);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCategory (CategoryEntry category);

    @Delete
    void deleteCategory (CategoryEntry category);

    @Query("SELECT * FROM category")
    List<CategoryEntry> loadAllCategoriesList();

    @Query("SELECT * FROM category WHERE id = :id")
    CategoryEntry loadCategoryById(int id);


}
