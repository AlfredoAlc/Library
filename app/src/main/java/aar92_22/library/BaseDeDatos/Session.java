package aar92_22.library.BaseDeDatos;


import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

public class Session extends AbstractDaoSession{

    private final DaoConfig libroDaoConfig;
    //private final DaoConfig ordenDaoConfig;
    //private final DaoConfig carritoDaoConfig;

    private final LibroDao libroDao;
    //private final OrdenDao ordenDao;
//    private final CarritoDao carritoDao;



    public Session(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        libroDaoConfig = daoConfigMap.get(LibroDao.class).clone();
        libroDaoConfig.initIdentityScope(type);

        /*ordenDaoConfig = daoConfigMap.get(OrdenDao.class).clone();
        ordenDaoConfig.initIdentityScope(type);*/

        /*carritoDaoConfig = daoConfigMap.get(CarritoDao.class).clone();
        carritoDaoConfig.initIdentityScope(type);*/

        libroDao = new LibroDao(libroDaoConfig, this);
        //ordenDao = new OrdenDao(ordenDaoConfig, this);
        //carritoDao = new CarritoDao(carritoDaoConfig, this);

        registerDao(Libro.class, libroDao);
        //registerDao(Orden.class, ordenDao);
       // registerDao(Carrito.class, carritoDao);
    }


    public void clear() {
        libroDaoConfig.getIdentityScope().clear();
        //ordenDaoConfig.getIdentityScope().clear();
        //carritoDaoConfig.getIdentityScope().clear();
    }

    public LibroDao getLibroDao() {
        return libroDao;
    }

    /*public OrdenDao getOrdenDao() {
        return ordenDao;
    }*/

    /*public CarritoDao getCarritoDao() {
        return carritoDao;
    }*/

}
