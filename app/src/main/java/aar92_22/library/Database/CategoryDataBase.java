package aar92_22.library.Database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CategoryEntry.class}, version = 1, exportSchema = false)

public abstract class CategoryDataBase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "categoryDatabase";
    private static CategoryDataBase sInstance;


    public static CategoryDataBase getsInstance(Context context) {
        if(sInstance == null){
            synchronized (LOCK){
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        CategoryDataBase.class, CategoryDataBase.DATABASE_NAME).build();

            }
        }
        return sInstance;
    }

    public abstract CategoryDao categoryDao();


}
