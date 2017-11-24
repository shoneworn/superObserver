package com.ailin.shoneworn.mylibrary;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * Created by admin on 2017/11/15.
 */

public class SuperApplication extends Application {

    private static  Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
    }

    public static int getMainThreadId(){
        return android.os.Process.myPid();
    }

    public static Handler getHandler(){
        return mHandler;
    }

}
