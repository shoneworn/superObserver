package com.ailin.shoneworn.mylibrary;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by admin on 2017/11/15.
 */

public class NotifyUtils {

    public static int getMainThreadId() {
        return SuperApplication.getMainThreadId();
    }

    public static Handler getHandler() {
        return SuperApplication.getHandler();
    }

    // 判断是否是主线的方法
    public static boolean isRunInMainThread() {
        return getMainThreadId() == android.os.Process.myTid();
    }

    // 保证当前的UI操作在主线程里面运行
    public static void runInMainThread(Runnable runnable) {
        if (isRunInMainThread()) {
            // 如果现在就是在珠现场中，就直接运行run方法
            runnable.run();
        } else {
            // 否则将其传到主线程中运行
            try{
                getHandler().post(runnable);
            }catch (Exception e){
                Log.d("SuperObserver","请将您自己的BaseApplication类继承自SuperApplication");
            }

        }
    }

}
