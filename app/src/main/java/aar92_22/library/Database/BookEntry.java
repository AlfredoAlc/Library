package aar92_22.library.Database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "books")
public class BookEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String lastName;
    private String firstName;
    private String lastName2;
    private String firstName2;
    private String lastName3;
    private String firstName3;
    private String publisher;
    private String publishedDate;
    private int numberPages;
    private String series;
    private String volume;
    private String category;
    private String summary;
    private byte [] bookCover;

    @Ignore
    public BookEntry(String title, String lastName, String firstName, String lastName2, String firstName2,
                     String lastName3, String firstName3, String publisher, String publishedDate,
                     int numberPages, String series, String volume, String category, String summary,
                     byte [] bookCover) {

        this.title = title;
        this.lastName = lastName;
        this.firstName = firstName;
        this.lastName2 = lastName2;
        this.firstName2 = firstName2;
        this.lastName3 = lastName3;
        this.firstName3 = firstName3;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.numberPages = numberPages;
        this.series = series;
        this.volume = volume;
        this.category = category;
        this.summary = summary;
        this.bookCover = bookCover;
    }


    public BookEntry(int id, String title, String lastName, String firstName, String lastName2,
                     String firstName2, String lastName3, String firstName3, String publisher,
                     String publishedDate, int numberPages, String series, String volume,
                     String category, String summary, byte [] bookCover) {
        this.id = id;
        this.title = title;
        this.lastName = lastName;
        this.firstName = firstName;
        this.lastName2 = lastName2;
        this.firstName2 = firstName2;
        this.lastName3 = lastName3;
        this.firstName3 = firstName3;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.numberPages = numberPages;
        this.series = series;
        this.volume = volume;
        this.category = category;
        this.summary = summary;
        this.bookCover = bookCover;
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

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName2() {
        return lastName2;
    }

    public String getFirstName2() {
        return firstName2;
    }

    public String getLastName3() {
        return lastName3;
    }

    public String getFirstName3() {
        return firstName3;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public int getNumberPages() {
        return numberPages;
    }

    public String getSeries() {
        return series;
    }

    public String getVolume() {
        return volume;
    }

    public String getCategory() {
        return category;
    }

    public String getSummary() {
        return summary;
    }

    public byte [] getBookCover() {
        return bookCover;
    }

}
