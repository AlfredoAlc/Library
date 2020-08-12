package aar92_22.library.Utilities;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;
    private final Executor diskIO;
    private final Executor networkIO;


     private AppExecutors(Executor diskIO, Executor networkIO) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
    }

    public static AppExecutors getInstance(){
        if(sInstance == null ){
            synchronized (LOCK){
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newSingleThreadExecutor());
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


}
