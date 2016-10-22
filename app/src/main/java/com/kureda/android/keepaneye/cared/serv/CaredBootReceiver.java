package com.kureda.android.keepaneye.cared.serv;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kureda.android.keepaneye.R;
import com.kureda.android.keepaneye.both.util.Res;

import static com.kureda.android.keepaneye.cared.ui.CaredMainActivity.SEC;

/**
 * Created by Sergei Kureda
 */
public class CaredBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            long interval = 1000L * Res.getInt(R.integer.activity_log_interval);
            Intent alarm = new Intent(context, AlarmReceiver.class);
            boolean alarmIsRunnig = (PendingIntent.getBroadcast(context, 0, alarm, PendingIntent
                    .FLAG_NO_CREATE) != null);
            if (!alarmIsRunnig) {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarm, 0);
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SEC, interval,
                        pendingIntent);
            } else {
            }
        }
    }
}