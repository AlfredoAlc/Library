package aar92_22.library.Database;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "books")
public class BookEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String author;
    private String editorial;
    private String synopsis;



    @Ignore
    public BookEntry(String title, String author, String editorial, String synopsis) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.editorial = editorial;
        this.synopsis = synopsis;
    }



    public BookEntry(int id, String title, String author, String editorial, String synopsis) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.editorial = editorial;
        this.synopsis = synopsis;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
}
