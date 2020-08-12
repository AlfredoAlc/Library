package aar92_22.library.Database;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import aar92_22.library.R;
import aar92_22.library.Utilities.AppExecutors;

@Database(entities = {CategoryEntry.class}, version = 1, exportSchema = false)

public abstract class CategoryDataBase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "categoryDatabase";
    private static CategoryDataBase sInstance;

    private static List<CategoryEntry> prepopulateData;


    public static CategoryDataBase getsInstance(final Context context) {
        if(sInstance == null){
            synchronized (LOCK){
                createPrepopulateData(context);
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        CategoryDataBase.class, CategoryDataBase.DATABASE_NAME)
                        .addCallback(new Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);
                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        getsInstance(context).categoryDao().defaultCategories(prepopulateData);
                                    }
                                });
                            }
                        })
                        .build();

            }
        }
        return sInstance;
    }

    public abstract CategoryDao categoryDao();


    private static void createPrepopulateData(final Context context){
        prepopulateData = new ArrayList<CategoryEntry>(){{
            add(new CategoryEntry(context.getString(R.string.others_category)));
            add(new CategoryEntry(context.getString(R.string.art_design_category)));
            add(new CategoryEntry(context.getString(R.string.business_financial_category)));
            add(new CategoryEntry(context.getString(R.string.children_book_category)));
            add(new CategoryEntry(context.getString(R.string.comic_graphic_novels_category)));
            add(new CategoryEntry(context.getString(R.string.education_engineering)));
            add(new CategoryEntry(context.getString(R.string.history_category)));
            add(new CategoryEntry(context.getString(R.string.literature_fiction_category)));
            add(new CategoryEntry(context.getString(R.string.politics_social_sciences_category)));
            add(new CategoryEntry(context.getString(R.string.travel_category)));
        }};

    }

}
