package aar92_22.library.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.HashMap;

import aar92_22.library.Utilities.AppExecutors;
import aar92_22.library.Utilities.NetworkUtilities;
import aar92_22.library.Utilities.PreferenceUtilities;

public class RecommendedViewModel extends AndroidViewModel {

    MutableLiveData<HashMap<String, Object>> bookMap;

    public RecommendedViewModel(@NonNull final Application application) {
        super(application);

        PreferenceUtilities preferenceUtilities = new PreferenceUtilities(application);

        final String recommendedTitle = preferenceUtilities.getRecommendedTitle();

        bookMap = new MutableLiveData<>();

        AppExecutors.getInstance().otherIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    bookMap.postValue(NetworkUtilities.createRecommendedView(application, recommendedTitle));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public MutableLiveData<HashMap<String, Object>> getBookMap() {
        return bookMap;
    }
}
