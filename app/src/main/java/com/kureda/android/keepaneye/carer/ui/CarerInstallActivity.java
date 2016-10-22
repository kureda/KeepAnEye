package com.kureda.android.keepaneye.carer.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.kureda.android.keepaneye.R;
import com.kureda.android.keepaneye.both.util.Internet;
import com.kureda.android.keepaneye.carer.sync.RegisterInMyJsonService;

/**
 * Created by Sergei Kureda
 */
public class CarerInstallActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer_install);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Internet.isConnected()) {
            setText(R.string.registering_in_cloud);
            startService(new Intent(this, RegisterInMyJsonService.class));
        } else {
             openDialog(R.string.turn_wifi);
        }
    }

    private void openDialog(int textId) {
        new AlertDialog.Builder(this)
                .setMessage(getString(textId))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CarerInstallActivity.this.finish();
                    }
                }).create().show();
    }

    private void setText(int stringId) {
        String text = getString(stringId);
        TextView view = (TextView) findViewById(R.id.carer_install_text);
        view.setText(text);
    }

}
