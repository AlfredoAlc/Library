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
    private String publisher;
    private String publishedDate;
    private String numberPages;
    private String series;
    private String volume;
    private String category;
    private String summary;

    @Ignore
    public BookEntry(String title, String lastName, String firstName,
                     String publisher, String publishedDate, String numberPages,
                     String series, String volume, String category, String summary) {

        this.title = title;
        this.lastName = lastName;
        this.firstName = firstName;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.numberPages = numberPages;
        this.series = series;
        this.volume = volume;
        this.category = category;
        this.summary = summary;
    }


    public BookEntry(int id, String title, String lastName, String firstName,
                     String publisher, String publishedDate, String numberPages,
                     String series, String volume, String category, String summary) {
        this.id = id;
        this.title = title;
        this.lastName = lastName;
        this.firstName = firstName;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.numberPages = numberPages;
        this.series = series;
        this.volume = volume;
        this.category = category;
        this.summary = summary;
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

    public String getNumberPages() {
        return numberPages;
    }

    public void setNumberPages(String numberPages) {
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
}
