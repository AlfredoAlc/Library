package aar92_22.library.ViewModel;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import aar92_22.library.Database.BookEntry;
import aar92_22.library.Utilities.AppExecutors;
import aar92_22.library.Utilities.NetworkUtilities;


public class SearchOnlineViewModel extends AndroidViewModel {

    MutableLiveData<List<BookEntry>> bookEntryList;


    String title;
    String lastName;
    String firstName;
    String publisher;
    String publishedDate;
    String category;
    String summary;
    String volume;
    int numberPages;
    byte [] bookCover;

    BookEntry bookEntry;

    public SearchOnlineViewModel(@NonNull Application application, final URL completeSearchUrl) {
        super(application);
        bookEntryList = new MutableLiveData<>();

        AppExecutors.getInstance().otherIO().execute(new Runnable() {
            @Override
            public void run() {
                try{
                    String searchResults = NetworkUtilities.getResponseFromHttpUrl(completeSearchUrl);
                    bookEntryList.postValue(createEntry(searchResults));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }

    public MutableLiveData<List<BookEntry>> getResults(){
        return bookEntryList;
    }


    private List<BookEntry> createEntry(String values){

        List<BookEntry> bookEntriesList = new ArrayList<>();

        try {
            JSONObject results = new JSONObject(values);
            JSONArray items = results.getJSONArray("items");
            JSONObject item;
            JSONObject volumeInfo;
            JSONArray authors;
            JSONObject images;
            JSONArray categories;
            JSONArray industryIdentifiers;


            for (int i = 0; i < items.length(); i++) {
                item = items.getJSONObject(i);
                volumeInfo = item.getJSONObject("volumeInfo");


                title = volumeInfo.getString("title");

                if(volumeInfo.has("authors")){
                    authors = volumeInfo.getJSONArray("authors");
                    separateFirstFromLastName(authors);
                }


                if (volumeInfo.has("pageCount")) {
                    numberPages = Integer.parseInt(volumeInfo.getString("pageCount"));
                } else {
                    numberPages = 0;
                }

                if(volumeInfo.has("imageLinks")){
                    images = volumeInfo.getJSONObject("imageLinks");
                    processImageLink(images);
                }

                if(volumeInfo.has("publisher")){
                    publisher = volumeInfo.getString("publisher");
                }

                if(volumeInfo.has("publishedDate")){
                    publishedDate = volumeInfo.getString("publishedDate");
                }

                if(volumeInfo.has("categories")){
                    categories = volumeInfo.getJSONArray("categories");
                    category = String.valueOf(categories.get(0));
                }

                if(volumeInfo.has("description")){
                    summary = volumeInfo.getString("description");
                }

                if (volumeInfo.has("industryIdentifiers")) {
                    industryIdentifiers = volumeInfo.getJSONArray("industryIdentifiers");
                    for (int j = 0; j < industryIdentifiers.length(); j++) {
                        JSONObject object = industryIdentifiers.getJSONObject(j);
                        if (object.get("type").equals("ISBN_13")) {
                            volume = object.get("identifier").toString();
                        }
                    }
                }

                bookEntry = new BookEntry(title, lastName, firstName, null,
                        null, null, null, publisher,
                        publishedDate, numberPages, null, volume,
                        category, summary, bookCover, null);


                bookEntriesList.add(bookEntry);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bookEntriesList;

    }


    private void separateFirstFromLastName(JSONArray authors){
        String author;
        String [] authorArr;
        try{
            author = String.valueOf(authors.get(0));
            authorArr = author.split(" ");
            firstName = authorArr[0];
            if (authorArr.length > 1) {
                lastName = authorArr[1];
            } else {
                lastName = "";
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void processImageLink(JSONObject images){
        try{
            String tempImageUrl = images.getString("smallThumbnail");
            String imageURL = tempImageUrl.replace("http", "https");

            URL url = null;
            Bitmap bitmap = null;

            try {
                url = new URL(imageURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                if(url != null){
                    bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bitmap != null) {
                ByteArrayOutputStream objectByteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, objectByteArrayOutputStream);
                bookCover = objectByteArrayOutputStream.toByteArray();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

    }



}
