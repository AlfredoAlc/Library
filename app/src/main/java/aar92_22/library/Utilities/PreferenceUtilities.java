package aar92_22.library.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import aar92_22.library.R;

public class PreferenceUtilities {

    private static final String LIST_VIEW = "list_view";
    private static final String SORT_BY = "sort_by";
    private static final String LIBRARY_NAME = "library_name";
    public static final String AUTHOR_FILTER_ACTIVATED = "author_filter_activated";
    public static final String CATEGORY_FILTER_ACTIVATED = "category_filter_activated";
    public static final String SERIES_FILTER_ACTIVATED = "series_filter_activated";
    private static final String AUTHOR_FILTER = "author_filter";
    private static final String CATEGORY_FILTER = "category_filter";
    private static final String SERIES_FILTER = "series_filter";
    private static final String REGISTER_DAY = "register_day";
    private static final String RECOMMENDED_TITLE = "recommended_title";

    private Context context;
    private SharedPreferences sharedPreferences;

    public PreferenceUtilities(Context context){
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public SharedPreferences sharedPreferencesChange(){
        return sharedPreferences;
    }

    private SharedPreferences.Editor getEditor(){
        return sharedPreferences.edit();
    }

    public void setListView(boolean listView){
        getEditor().putBoolean(LIST_VIEW, listView).apply();
    }

    public boolean isListView(){
        return sharedPreferences.getBoolean(LIST_VIEW, true);
    }

    public void setSortBy(String sortBy){
        getEditor().putString(SORT_BY, sortBy).apply();
    }

    public String getSortBy(){
        return sharedPreferences.getString(SORT_BY, context.getString(R.string.date_added));
    }

    public void setLibraryName(String libraryName){
        getEditor().putString(LIBRARY_NAME, libraryName).apply();
    }

    public String getLibraryName(){
        return sharedPreferences.getString(LIBRARY_NAME, context.getString(R.string.library_string));
    }

    public void setAuthorFilterActivated(boolean activated){
        getEditor().putBoolean(AUTHOR_FILTER_ACTIVATED, activated).apply();
    }

    public boolean isAuthorFilter(){
        return sharedPreferences.getBoolean(AUTHOR_FILTER_ACTIVATED, false);
    }

    public void setCategoryFilterActivated(boolean activated){
        getEditor().putBoolean(CATEGORY_FILTER_ACTIVATED, activated).apply();
    }

    public boolean isCategoryFilter(){
        return sharedPreferences.getBoolean(CATEGORY_FILTER_ACTIVATED, false);
    }

    public void setSeriesFilterActivated(boolean activated){
        getEditor().putBoolean(SERIES_FILTER_ACTIVATED, activated).apply();
    }

    public boolean isSeriesFilter(){
        return sharedPreferences.getBoolean(SERIES_FILTER_ACTIVATED, false);
    }

    public void setAuthorFilter(String authorFilter){
        getEditor().putString(AUTHOR_FILTER, authorFilter).apply();
    }

    public String getAuthorFilter(){
        return sharedPreferences.getString(AUTHOR_FILTER, "");
    }

    public void setCategoryFilter(String categoryFilter){
        getEditor().putString(CATEGORY_FILTER, categoryFilter).apply();
    }

    public String getCategoryFilter(){
        return sharedPreferences.getString(CATEGORY_FILTER, "");
    }

    public void setSeriesFilter(String seriesFilter){
        getEditor().putString(SERIES_FILTER, seriesFilter).apply();
    }

    public String getSeriesFilter(){
        return sharedPreferences.getString(SERIES_FILTER, "");
    }

    public void setRegisterDay(int day){
        getEditor().putInt(REGISTER_DAY, day).apply();
    }

    public int getRegisterDay(){
        return sharedPreferences.getInt(REGISTER_DAY, 0);
    }

    public void setRecommendedTitle(String title){
        getEditor().putString(RECOMMENDED_TITLE, title).apply();
    }

    public String getRecommendedTitle(){
        return sharedPreferences.getString(RECOMMENDED_TITLE, "");
    }

}
