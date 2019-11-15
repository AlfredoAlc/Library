package aar92_22.library.BaseDeDatos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

public class LibroDao extends AbstractDao<Libro, Long> {

    public static final String TABLENAME = "LIBRO";

    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Nombre = new Property(1, String.class, "nombre", false, "NOMBRE");
        public final static Property Autor = new Property(2, String.class, "autor", false, "AUTOR");
        public final static Property Sinopsis = new Property(3, String.class, "sinopsis", false, "SINOPSIS");
        public final static Property Editorial = new Property(4, String.class, "editorial", false, "EDITORIAL");
        public final static Property Foto = new Property(5, String.class, "foto", false, "FOTO");

    };

    private Session daoSession;


    public LibroDao(DaoConfig config) {
        super(config);
    }

    public LibroDao(DaoConfig config, Session daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'LIBRO' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'NOMBRE' TEXT," + // 1: nombre
                "'AUTOR' TEXT," + // 2: autor
                "'SINOPSIS' TEXT," + // 3: sinopsis
                "'EDITORIAL' TEXT," + // 4: editorial
                "'FOTO' TEXT);"); // 5: foto


    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'LIBRO'";
        db.execSQL(sql);
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, Libro entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        String nombre = entity.getNombre();
        if (nombre != null) {
            stmt.bindString(2, nombre);
        }

        String autor = entity.getAutor();
        if (autor != null) {
            stmt.bindString(3, autor);
        }

        String sinopsis = entity.getSinopsis();
        if (sinopsis != null) {
            stmt.bindString(4, sinopsis);
        }

        String editorial = entity.getEditorial();
        if (editorial != null) {
            stmt.bindString(5, editorial);
        }

        String foto = entity.getFoto();
        if (foto != null) {
            stmt.bindString(6, foto);
        }
    }

    @Override
    protected void attachEntity(Libro entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    @Override
    public Libro readEntity(Cursor cursor, int offset) {
        Libro entity = new Libro( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // nombre
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // autor
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // sinopsis
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // editorial
                cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // foto
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, Libro entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setNombre(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAutor(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSinopsis(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setEditorial(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setFoto(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));

    }

    @Override
    protected Long updateKeyAfterInsert(Libro entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    @Override
    public Long getKey(Libro entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }



}
