package com.kureda.android.keepaneye.both.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kureda.android.keepaneye.both.util.Mode;
import com.kureda.android.keepaneye.cared.ui.CaredMainActivity;
import com.kureda.android.keepaneye.carer.ui.CarerInstallActivity;
import com.kureda.android.keepaneye.carer.ui.CarerMainActivity;

import static com.kureda.android.keepaneye.both.util.Mode.CARED;
import static com.kureda.android.keepaneye.both.util.Mode.CARER;
import static com.kureda.android.keepaneye.both.util.Mode.CARER_INSTALL;

/**
 * Created by Sergei Kureda
 * Launches an activity depending of mode, stored in Shared Preferences.
 * So application can start at cared or carer mode. If mode not set yet, is starts at inctallation
 * mode.
 */
public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mode mode = Mode.getMode();
        if (mode.equals(CARER)) {
            startActivity(new Intent(this, (Class) CarerMainActivity.class));
        } else if (mode.equals(CARED)) {
            startActivity(new Intent(this, (Class) CaredMainActivity.class));
        } else if (mode.equals(CARER_INSTALL)) {
            startActivity(new Intent(this, (Class) CarerInstallActivity.class));
        } else {
            startActivity(new Intent(this, (Class) InstallActivity.class));
        }
        finish();
    }
}
