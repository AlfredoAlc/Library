package aar92_22.library.Utilities;

import java.io.Serializable;
import java.util.List;

import aar92_22.library.BaseDeDatos.Libro;

/**
 * Created by aar92_22 on 1/18/17.
 */

public class Holder implements Serializable {

    private List<Libro> bookList;

    public List<Libro> getBookList() {
        return bookList;
    }

    public void setBookList(List<Libro> bookList) {
        this.bookList = bookList;
    }


}
