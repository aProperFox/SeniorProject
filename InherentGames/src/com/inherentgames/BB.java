package com.inherentgames;

import android.app.Application;
import android.content.Context;

public class BB extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        BB.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return BB.context;
    }
}
