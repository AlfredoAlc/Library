package aar92_22.library.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

import static aar92_22.library.Activities.RecommendedActivity.RECOMMENDED_AUTHOR;
import static aar92_22.library.Activities.RecommendedActivity.RECOMMENDED_BOOK_COVER;
import static aar92_22.library.Activities.RecommendedActivity.RECOMMENDED_ISBN;
import static aar92_22.library.Activities.RecommendedActivity.RECOMMENDED_SUMMARY;
import static aar92_22.library.Activities.RecommendedActivity.RECOMMENDED_TITLE;


public class NetworkUtilities {

    private final static String GOOGLE_BOOKS_BASE_URL =
            "https://www.googleapis.com/books/v1/volumes";

    private final static String PARAM_QUERY = "q";

    private final static String AMAZON_URL_FIRST = "https://www.amazon.com/s/ref=as_li_ss_tl?k=";
    private final static String AMAZON_URL_SECOND = "&i=stripbooks&linkCode=ll2&tag=adroit010-20&linkId=12e4359addc787367795a968dd51d6b5&language=en_US";

    public static URL buildUrl(String bookSearchQuery) {
        Uri builtUri = Uri.parse(GOOGLE_BOOKS_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_QUERY, bookSearchQuery)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }



    public static HashMap<String, Object> createRecommendedView(Context context, String query)
            throws IOException {

        String values = "";
        HashMap<String, Object> recommendedBook = new HashMap<>();

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            values = getResponseFromHttpUrl(buildUrl(query));
        }

        if(values != null){
            try {
                recommendedBook = getRecommendedMap(values);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return recommendedBook;
    }

    private static HashMap<String, Object> getRecommendedMap(String values) throws JSONException{
        HashMap<String, Object> recommendedBook = new HashMap<>();

        JSONObject results = new JSONObject(values);
        JSONArray items = results.getJSONArray("items");
        JSONObject item;
        JSONObject volumeInfo;
        JSONArray industryIdentifiers;

        int randomBook;

        do {
            double randomBookDouble = Math.random() * 10;
            randomBook = (int) randomBookDouble;

        } while (randomBook < 0 && randomBook > items.length());


        item = items.getJSONObject(randomBook);
        volumeInfo = item.getJSONObject("volumeInfo");


        recommendedBook.put(RECOMMENDED_TITLE, volumeInfo.getString("title"));

        if (volumeInfo.has("authors")) {
            recommendedBook.put(RECOMMENDED_AUTHOR, volumeInfo.getJSONArray("authors").get(0).toString());
        }

        if (volumeInfo.has("imageLinks")) {
            String imageUrl = volumeInfo.getJSONObject("imageLinks").getString("thumbnail");
            byte[] bytes = processImageLink(imageUrl);
            recommendedBook.put(RECOMMENDED_BOOK_COVER, bytes);
        }

        if (volumeInfo.has("description")) {
            recommendedBook.put(RECOMMENDED_SUMMARY, volumeInfo.getString("description"));
        }

        if (volumeInfo.has("industryIdentifiers")) {
            industryIdentifiers = volumeInfo.getJSONArray("industryIdentifiers");
            for (int i = 0; i < industryIdentifiers.length(); i++) {
                JSONObject object = industryIdentifiers.getJSONObject(i);
                if (object.get("type").equals("ISBN_13")) {
                    recommendedBook.put(RECOMMENDED_ISBN, object.get("identifier").toString());
                }
            }
        }

        return recommendedBook;
    }

    private static byte[] processImageLink(String tempImageUrl){

        String imageURL = tempImageUrl.replace("http", "https");

        byte [] bytes = null;

        URL url = null;

        try {
            url = new URL(imageURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            if (url != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                bytes = byteArrayOutputStream.toByteArray();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return bytes;
    }


    public static String getAdAmazonUrl(String isbn){
        return AMAZON_URL_FIRST + isbn + AMAZON_URL_SECOND;
    }

}