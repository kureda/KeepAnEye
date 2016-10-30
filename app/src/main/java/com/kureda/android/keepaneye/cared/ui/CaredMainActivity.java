package com.kureda.android.keepaneye.cared.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.kureda.android.keepaneye.R;
import com.kureda.android.keepaneye.both.util.Internet;
import com.kureda.android.keepaneye.both.util.Phone;
import com.kureda.android.keepaneye.both.util.UserActivity;
import com.kureda.android.keepaneye.both.util.UserDetails;
import com.kureda.android.keepaneye.both.util.Util;
import com.kureda.android.keepaneye.cared.serv.AlarmReceiver;
import com.kureda.android.keepaneye.cared.serv.CaredBootReceiver;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Sergei Kureda
 */
public class CaredMainActivity extends AppCompatActivity {
    public static final int MINUTE = 60 * 1000;
    public static final long SEC = 1000L;
    Handler handler = new Handler();
    Runnable refresh;
    private FirebaseAnalytics mFirebaseAnalytics;
    private TextView mMainText;
    private FloatingActionButton mFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_cared_main);
        findControls();
        showControls(false);
        mMainText = (TextView) findViewById(R.id.cared_main_text);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        displayAppState();
        //startAlarm();
        startScreenRefreshing();
    }

    private void showControls(boolean show) {
        mMainText.setVisibility(show ? VISIBLE : GONE);
        mFab.setVisibility(show ? VISIBLE : GONE);
    }

    private void findControls() {
        mFab = (FloatingActionButton) findViewById(R.id.cared_fab_call);
        mMainText = (TextView) findViewById(R.id.cared_main_text);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (configIsSet()) {
            startAlarm();
            showControls(true);
        } else {
            startActivity(new Intent(this, CaredConfigActivity.class));
        }
    }

    private boolean configIsSet() {
        return !UserDetails.read().isEmpty();
    }

    /**
     * Create the ActionBar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cared_main, menu);
        return true;
    }

    /**
     * Respond to user gestures on the ActionBar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cared_main_menu_edit:
                startActivity(new Intent(this, CaredConfigActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startAlarm() {
        //enable boot receiver to start alarm
        ComponentName receiver = new ComponentName(this, CaredBootReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        long interval = 1000L * getResources().getInteger(R.integer.activity_log_interval);
        Intent alarm = new Intent(this, AlarmReceiver.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(this, 0, alarm, PendingIntent
                .FLAG_NO_CREATE) != null);
        if (!alarmRunning) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarm, 0);
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SEC, interval, pendingIntent);
        }
    }

    public void makePhoneCallToCarer(View view) {
        String phoneNumber = UserDetails.read().getPhone();
        Phone.call(phoneNumber, this);
    }

    private void displayAppState() {
        String text = "";
        int color = ContextCompat.getColor(this, R.color.green700);
        if (!Internet.isConnected()) {
            text = getString(R.string.cared_no_internet);
            color = ContextCompat.getColor(this, R.color.red);
        } else {
            String lastTime = UserActivity.getLastUploadTime();
            if (lastTime == null || lastTime.length() == 0 || lastTime.equals("0")) {
                text = getString(R.string.carer_not_yet_reported);
            } else {
                int ago = Util.now() - Integer.parseInt(lastTime);
                if(ago==0) {
                    text = getString(R.string.cared_last_report_just_now);
                }else{
                    text = getResources().getQuantityString(R.plurals.cared_last_report_was, ago, ago);
                }
            }
        }
        mMainText.setAlpha(0.0f);
        mMainText.setText(text);
        mMainText.setTextColor(color);
        mMainText.animate().setStartDelay(1000L).setDuration(1000L).alpha(1.0f).start();
    }


    private void startScreenRefreshing() {
        refresh = new Runnable() {
            public void run() {
                displayAppState();
                mMainText.invalidate();
                handler.postDelayed(refresh, MINUTE);
            }
        };
        handler.post(refresh);
    }

}
