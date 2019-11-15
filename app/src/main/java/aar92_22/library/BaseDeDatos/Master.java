package aar92_22.library.BaseDeDatos;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

public class Master extends AbstractDaoMaster{

    public static final int SCHEMA_VERSION = 1000;

    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        LibroDao.createTable(db, ifNotExists);
        //OrdenDao.createTable(db, ifNotExists);
        //CarritoDao.createTable(db, ifNotExists);
    }

    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        LibroDao.dropTable(db, ifExists);
        //OrdenDao.dropTable(db, ifExists);
       // CarritoDao.dropTable(db, ifExists);
    }

    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }

    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

    public Master(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(LibroDao.class);
        //registerDaoClass(OrdenDao.class);
       // registerDaoClass(CarritoDao.class);
    }

    public Session newSession() {
        return new Session(db, IdentityScopeType.Session, daoConfigMap);
    }

    public Session newSession(IdentityScopeType type) {
        return new Session(db, type, daoConfigMap);
    }


}
