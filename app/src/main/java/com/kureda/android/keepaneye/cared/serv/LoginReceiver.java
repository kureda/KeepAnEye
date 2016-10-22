package com.kureda.android.keepaneye.cared.serv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kureda.android.keepaneye.both.util.UserActivity;

/**
 * Created by Sergei Kureda
 * Regester cases of user logging in
 */

public class LoginReceiver extends BroadcastReceiver {

    /**
     * When user logs in (i.e. uses his smartphone), makes a record of it as shared preference
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            UserActivity.logLogging();
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
        }
    }
}


