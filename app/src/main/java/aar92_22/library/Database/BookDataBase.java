package aar92_22.library.Database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {BookEntry.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class BookDataBase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "library";
    private static BookDataBase sInstance;


    public static BookDataBase getsInstance(Context context) {
        if(sInstance == null){
            synchronized (LOCK){
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        BookDataBase.class, BookDataBase.DATABASE_NAME).build();
            }
        }
        return sInstance;
    }

    public abstract BookDao bookDao();

}


