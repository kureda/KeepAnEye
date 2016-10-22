package com.kureda.android.keepaneye.cared.serv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Sergei Kureda
 */
public class AlarmReceiver extends BroadcastReceiver {

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, CaredLogService.class);
        context.startService(serviceIntent);
    }
}
