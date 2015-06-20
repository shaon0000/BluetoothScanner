package com.scanner.bth;

import android.app.Application;
import android.content.Context;

/**
 * Created by shaon on 4/3/2015.
 */

public class CustomApplication extends Application {
    private static CustomApplication instance;
    public CustomApplication(){
        instance = this;

    }

    //private static MyApp instance;
    private static Context mContext;

    public static CustomApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        //  return instance.getApplicationContext();
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //  instance = this;
        mContext = getApplicationContext();
    }
}
