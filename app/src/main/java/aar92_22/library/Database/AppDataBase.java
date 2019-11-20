package aar92_22.library.Database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {BookEntry.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {

    public static final Object LOCK = new Object();
    public static final String DATABASE_NAME = "library";
    private static AppDataBase sInstance;


    public static AppDataBase getsInstance(Context context) {
        if(sInstance == null){
            synchronized (LOCK){
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDataBase.class,AppDataBase.DATABASE_NAME).build();
            }
        }
        return sInstance;
    }

    public abstract BookDao bookDao();
}
