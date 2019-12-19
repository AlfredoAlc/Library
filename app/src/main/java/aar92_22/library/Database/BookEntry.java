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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName2() {
        return lastName2;
    }

    public void setLastName2(String lastName2) {
        this.lastName2 = lastName2;
    }

    public String getFirstName2() {
        return firstName2;
    }

    public void setFirstName2(String firstName2) {
        this.firstName2 = firstName2;
    }

    public String getLastName3() {
        return lastName3;
    }

    public void setLastName3(String lastName3) {
        this.lastName3 = lastName3;
    }

    public String getFirstName3() {
        return firstName3;
    }

    public void setFirstName3(String firstName3) {
        this.firstName3 = firstName3;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public int getNumberPages() {
        return numberPages;
    }

    public void setNumberPages(int numberPages) {
        this.numberPages = numberPages;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public byte [] getBookCover() {
        return bookCover;
    }

    public void setBookCover(byte [] bookCover) {
        this.bookCover = bookCover;
    }
}
