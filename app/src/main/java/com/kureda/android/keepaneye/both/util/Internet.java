package com.kureda.android.keepaneye.both.util;

import android.content.Context;
import android.net.ConnectivityManager;

import com.kureda.android.keepaneye.both.ui.App;
import com.squareup.okhttp.OkHttpClient;

/**
 * Created by Sergei Kureda
 * Internet-related methods
 */

public class Internet {
    private static OkHttpClient sOkHttpClient;

    public static boolean isConnected() {
        Context context = App.getContext();
        String service = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(service);
        return cm.getActiveNetworkInfo() != null;
    }

    public static OkHttpClient getHttpClient() {
        if (sOkHttpClient == null) {
            sOkHttpClient = new OkHttpClient();
        }
        return sOkHttpClient;
    }

}
