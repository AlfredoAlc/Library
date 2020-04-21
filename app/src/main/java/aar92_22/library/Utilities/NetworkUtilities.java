package aar92_22.library.Utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;



public class NetworkUtilities {

    private final static String GOOGLE_BOOKS_BASE_URL =
            "https://www.google.com/search?tbm=bks";

    private final static String PARAM_QUERY = "q";


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
}