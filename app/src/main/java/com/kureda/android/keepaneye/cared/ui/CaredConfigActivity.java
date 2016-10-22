package com.kureda.android.keepaneye.cared.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.kureda.android.keepaneye.R;
import com.kureda.android.keepaneye.both.util.FormValidator;
import com.kureda.android.keepaneye.both.util.UserDetails;

import static com.kureda.android.keepaneye.both.util.FormValidator.PHONE;
import static com.kureda.android.keepaneye.both.util.FormValidator.RANGED_NUMBER;
import static com.kureda.android.keepaneye.both.util.FormValidator.SHORT_NAME;

/**
 * Created by Sergei Kureda
 * Install and config activity.
 */
public class CaredConfigActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mId, mName, mPhone;
    FloatingActionButton mFab;
    FormValidator mValidator;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_cared_config);
        findControls();
        readPreviousSettings(bundle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFab = (FloatingActionButton) findViewById(R.id.cared_config_fab);
        mFab.setOnClickListener(this);
        createFormValidator();
    }


    @Override
    public void onClick(View v) { //FAB clicked
        saveUserDetails();
        startActivity(new Intent(this, CaredMainActivity.class));
        finish();
    }

    private void findControls() {
        mId = (EditText) findViewById(R.id.id_entry_field);
        mName = (EditText) findViewById(R.id.name_entry_field);
        mPhone = (EditText) findViewById(R.id.phone_entry_field);
    }

    /**
     * If activity just started, read setting from shared prefs and set them to screen's controls
     *
     * @param bundle State that was before rotating
     */
    private void readPreviousSettings(Bundle bundle) {
        UserDetails details = UserDetails.read();
        if (bundle == null && !details.isEmpty()) { //it is not first time, already was set
            fillDataFromDetails(details);
        }
    }

    private void fillDataFromDetails(UserDetails details) {
        mId.setText(details.getKeepAnEyeId());
        mName.setText(details.getName());
        mPhone.setText(details.getPhone());
    }

    private void createFormValidator() {
        mValidator = new FormValidator(mFab);
        mValidator.register(mId, RANGED_NUMBER, 7);
        mValidator.register(mName, SHORT_NAME, 12);
        mValidator.register(mPhone, PHONE, 8);
        mValidator.checkAll(false);
    }

    private void saveUserDetails() {
        String id = getText(mId);
        String name = getText(mName);
        String phone = getText(mPhone);
        UserDetails user = new UserDetails(id, name, phone);
        user.save();
    }

    private String getText(EditText field) {
        return field.getText().toString();
    }

}
