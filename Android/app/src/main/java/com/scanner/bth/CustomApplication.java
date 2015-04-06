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
    public static Context getContext(){
        return instance;
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
    }
}
