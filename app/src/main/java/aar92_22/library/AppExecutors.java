package aar92_22.library;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;
    private final Executor diskIO;
    private final Executor networkIO;
    private final Executor mainThreadIO;


     private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThreadIO) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThreadIO = mainThreadIO;

    }

    public static AppExecutors getInstance(){
        if(sInstance == null ){
            synchronized (LOCK){
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(2),
                        new MainThreadExecutor());
            }
        }

        return sInstance;

    }

    public Executor diskIO () {
        return diskIO;
    }

    public Executor otherIO (){
        return networkIO;
    }

    public Executor mainThreadIO (){
        return mainThreadIO;
    }



    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }



}
