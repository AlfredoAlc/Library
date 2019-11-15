package aar92_22.library.BaseDeDatos;

import java.io.Serializable;

import de.greenrobot.dao.DaoException;

/**
 * Created by aar92_22 on 1/18/17.
 */

public class Libro implements Serializable {


    private Long id;
    private String nombre;
    private String autor;
    private String sinopsis;
    private String editorial;
    private String foto;

    private transient Session daoSession;

    private transient LibroDao myDao;


    public Libro() {
    }

    public Libro(Long id) {
        this.id = id;
    }



    public Libro(Long id, String nombre, String autor, String sinopsis, String editorial, String foto) {
        this.id = id;
        this.nombre = nombre;
        this.autor = autor;
        this.sinopsis = sinopsis;
        this.editorial = editorial;
        this.foto = foto;
    }

    public void __setDaoSession(Session daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getLibroDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

}
