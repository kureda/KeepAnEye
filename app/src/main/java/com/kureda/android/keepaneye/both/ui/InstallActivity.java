package com.kureda.android.keepaneye.both.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.kureda.android.keepaneye.R;
import com.kureda.android.keepaneye.cared.ui.CaredConfigActivity;
import com.kureda.android.keepaneye.carer.ui.CarerInstallActivity;

import static com.kureda.android.keepaneye.both.util.Mode.CARED;
import static com.kureda.android.keepaneye.both.util.Mode.CARER_INSTALL;

/**
 * Created by Sergei Kureda
 */
public class InstallActivity extends AppCompatActivity {
    public static final long ANIM_DURATION = 500;
    public static final long ANIMA_INTERVAL = 1500;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_install);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            startAnimation();
        }
    }

    public void setMode(View v) {
        AppCompatImageButton button = (AppCompatImageButton) v;
        int buttonId = button.getId();
        if (buttonId == R.id.btn_cared) {
            CARED.save();
            startActivity(new Intent(this, CaredConfigActivity.class));
        }
        if (buttonId == R.id.btn_carer) {
            CARER_INSTALL.save();
            startActivity(new Intent(this, CarerInstallActivity.class));
        }
        finish();//we don't need this activity anymore
    }

    private void startAnimation() {
        View image1 = findViewById(R.id.carer1);
        View image2 = findViewById(R.id.cared1);
        View text1 = findViewById(R.id.text1);
        View text2 = findViewById(R.id.text2);
        View newView = findViewById(R.id.content_2_layout);
        View oldView = findViewById(R.id.content_1_layout);

        image1.animate().setStartDelay(100).setDuration(ANIM_DURATION).alpha(1);
        text1.animate().setStartDelay(ANIMA_INTERVAL).setDuration(ANIM_DURATION).alpha(1);
        image2.animate().setStartDelay(ANIMA_INTERVAL * 2).setDuration(ANIM_DURATION).alpha(1);
        text2.animate().setStartDelay(ANIMA_INTERVAL * 3).setDuration(ANIM_DURATION).alpha(1);
        oldView.animate().setStartDelay(ANIMA_INTERVAL * 4).setDuration(ANIM_DURATION).alpha(0);
        newView.animate().setStartDelay(ANIMA_INTERVAL * 4).setDuration(ANIM_DURATION * 2).alpha(1);
    }
}
