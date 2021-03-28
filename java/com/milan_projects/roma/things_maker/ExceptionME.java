package com.milan_projects.roma.things_maker;

import android.util.Log;

/**
 * Created by Roma on 12.11.2017.
 */

public class ExceptionME implements Thread.UncaughtExceptionHandler{
    public void uncaughtException(Thread thread, Throwable throwable) {
        Log.d("TryMe", "Something wrong happened!");
    }
}
