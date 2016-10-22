package com.kureda.android.keepaneye.both.ui;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;

/**
 * Created by Sergei Kureda
 * Extended the Application, to have a static reference to ApplicationContext
 */

public class App extends Application {

    private static Context sContext; //not recommended, but other options are worse
    private static String sId;

    public static Context getContext() {
        return sContext;
    }

    public static String getId() {
        return sId;
    }

    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        sId = Settings.Secure.getString(App.getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID); //not recommended, but other options are worse
    }
}
