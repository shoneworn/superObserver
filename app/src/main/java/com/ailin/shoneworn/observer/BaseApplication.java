package com.ailin.shoneworn.observer;

import android.content.Context;

import com.ailin.shoneworn.mylibrary.SuperApplication;

/**
 * Created by admin on 2017/11/16.
 */

public class BaseApplication extends SuperApplication {
    public static int screenWidth;
    public static float px;
    public static int screenHeight;
    private static Context context;
    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

}
