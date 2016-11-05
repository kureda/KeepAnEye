package com.kureda.android.keepaneye.carer.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.kureda.android.keepaneye.R;
import com.kureda.android.keepaneye.both.util.PhoneValidator;
import com.kureda.android.keepaneye.carer.db.CaredDbContract;
import com.kureda.android.keepaneye.carer.db.CaredDbProvider;
import com.kureda.android.keepaneye.carer.util.Cared;
import com.kureda.android.keepaneye.carer.util.ImageArrayAdapter;
import com.kureda.android.keepaneye.carer.util.Util;

import static android.R.layout.simple_spinner_dropdown_item;

/**
 * Created by Sergei Kureda
 */
public class CarerConfigActivity extends AppCompatActivity implements AdapterView
        .OnItemSelectedListener {
    private static final String TAG = "CarerConfigActivity";
    private static final String CARED = "CARED";
    private static final String CARED_INDEX = "CARED_INDEX";

    private Spinner mCaredSpinner, mAvatarSpinner,
            mSleepSpinner, mWakeSpinner,
            mReportYellowSpinner, mReportRedSpinner,
            mLoginYellowSpinner, mLoginRedSpinner,
            mWalkYellowSpinner, mWalkRedSpinner,
            mRideYellowSpinner, mRideRedSpinner;
    private android.support.v7.widget.SwitchCompat mHideSwitch;
    private EditText mPhoneText;
    private Cared mCared = null;
    private int mCaredIndex = 0;
    private int mSkipOnItemSelected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restoreFromBundle(savedInstanceState);
        setContentView(R.layout.activity_carer_config);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setControls();
        mPhoneText.addTextChangedListener(new PhoneValidator(mPhoneText));
    }

    private void restoreFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Object cared = savedInstanceState.getSerializable(CARED);
            if(cared!=null) {
                mCared = (Cared) savedInstanceState.getSerializable(CARED);
            }
            mCaredIndex = savedInstanceState.getInt(CARED_INDEX);
            mSkipOnItemSelected = 2;//after screen rotation onItemSelected() is called twice.
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt(CARED_INDEX, mCaredIndex);
        if(mCared!=null) {
            readControlsToCared();
            bundle.putSerializable(CARED, mCared); //Performance, I know. But no boilerplate code :)
        }
        super.onSaveInstanceState(bundle);
    }

    private void readControlsToCared() {
        mCared.deleted = !mHideSwitch.isChecked();
        mCared.phone = mPhoneText.getText().toString();
        mCared.avatar = mAvatarSpinner.getSelectedItemPosition();
        mCared.sleep = Util.indexToHours(mSleepSpinner.getSelectedItemPosition());
        mCared.wake = Util.indexToHours(mWakeSpinner.getSelectedItemPosition());
        mCared.reportYellow = Util.indexToInterval(mReportYellowSpinner.getSelectedItemPosition());
        mCared.reportRed = Util.indexToInterval(mReportRedSpinner.getSelectedItemPosition());
        mCared.logYellow = Util.indexToInterval(mLoginYellowSpinner.getSelectedItemPosition());
        mCared.logRed = Util.indexToInterval(mLoginRedSpinner.getSelectedItemPosition());
        mCared.walkYellow = Util.indexToInterval(mWalkYellowSpinner.getSelectedItemPosition());
        mCared.walkRed = Util.indexToInterval(mWalkRedSpinner.getSelectedItemPosition());
        mCared.rideYellow = Util.indexToInterval(mRideYellowSpinner.getSelectedItemPosition());
        mCared.rideRed = Util.indexToInterval(mRideRedSpinner.getSelectedItemPosition());
    }

    private void setControls() {
        showKeyboard(false);
        createControls();
        findAllControls();
        Cared[] careds = readCaredsFromDatabase();
        enableControls(careds.length>0);
        setCaredDropdown(careds);
        if(mCared==null && careds.length>0){
            int selected = mCaredSpinner.getSelectedItemPosition();
            mCared=careds[selected];
            fillAllControls(mCared);
        }
    }

    private void enableControls(boolean enable) {
        mCaredSpinner.setEnabled(enable);
        mAvatarSpinner.setEnabled(enable);
        mSleepSpinner.setEnabled(enable);
        mWakeSpinner.setEnabled(enable);
        mReportYellowSpinner.setEnabled(enable);
        mReportRedSpinner.setEnabled(enable);
        mLoginYellowSpinner.setEnabled(enable);
        mLoginRedSpinner.setEnabled(enable);
        mWalkYellowSpinner.setEnabled(enable);
        mWalkRedSpinner.setEnabled(enable);
        mRideYellowSpinner.setEnabled(enable);
        mRideRedSpinner.setEnabled(enable);
        mHideSwitch.setEnabled(enable);
        mPhoneText.setEnabled(enable);
        if(! enable){
            Toast.makeText(this, getString(R.string.nobody_to_edit), Toast.LENGTH_LONG).show();
        }
    }

    public void showKeyboard(boolean visible) {
        int mode = visible ?
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE :
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
        this.getWindow().setSoftInputMode(mode);
    }

    private void createControls() {
        Spinner avatarSpinner = (Spinner) findViewById(R.id.conf_avatar);
        ImageArrayAdapter adapter = new ImageArrayAdapter(this, Cared.AVATARS);
        avatarSpinner.setAdapter(adapter);
    }

    //Called when item from careds spinner is selected. Fill and show all controls.
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mSkipOnItemSelected > 0) { //to tell item click from first after creation
            mSkipOnItemSelected--;
        } else {
            mCared = (Cared) parent.getItemAtPosition(position);
        }
        fillAllControls(mCared);
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }


    private void findAllControls() {
        mCaredSpinner = (Spinner) findViewById(R.id.conf_cared);
        mAvatarSpinner = (Spinner) findViewById(R.id.conf_avatar);
        mSleepSpinner = (Spinner) findViewById(R.id.conf_sleep);
        mWakeSpinner = (Spinner) findViewById(R.id.conf_wake);
        mReportYellowSpinner = (Spinner) findViewById(R.id.conf_report_yellow);
        mReportRedSpinner = (Spinner) findViewById(R.id.conf_report_red);
        mLoginYellowSpinner = (Spinner) findViewById(R.id.conf_login_yellow);
        mLoginRedSpinner = (Spinner) findViewById(R.id.conf_login_red);
        mWalkYellowSpinner = (Spinner) findViewById(R.id.conf_walk_yellow);
        mWalkRedSpinner = (Spinner) findViewById(R.id.conf_walk_red);
        mRideYellowSpinner = (Spinner) findViewById(R.id.conf_ride_yellow);
        mRideRedSpinner = (Spinner) findViewById(R.id.conf_ride_red);

        mHideSwitch = (SwitchCompat) findViewById(R.id.conf_hidden);
        mPhoneText = (EditText) findViewById(R.id.conf_phone);
    }

    private void fillAllControls(Cared cared) {
        mHideSwitch.setChecked(!cared.deleted);
        mPhoneText.setText(cared.phone);
        mAvatarSpinner.setSelection(cared.avatar, true);
        mSleepSpinner.setSelection(Util.hoursToIndex(cared.sleep), true);
        mWakeSpinner.setSelection(Util.hoursToIndex(cared.wake), true);
        mReportYellowSpinner.setSelection(Util.intervalToIndex(cared.reportYellow));
        mReportYellowSpinner.setSelection(Util.intervalToIndex(cared.reportYellow));
        mReportRedSpinner.setSelection(Util.intervalToIndex(cared.reportRed));
        mLoginYellowSpinner.setSelection(Util.intervalToIndex(cared.logYellow));
        mLoginRedSpinner.setSelection(Util.intervalToIndex(cared.logRed));
        mWalkYellowSpinner.setSelection(Util.intervalToIndex(cared.walkYellow));
        mWalkRedSpinner.setSelection(Util.intervalToIndex(cared.walkRed));
        mRideYellowSpinner.setSelection(Util.intervalToIndex(cared.rideYellow));
        mRideRedSpinner.setSelection(Util.intervalToIndex(cared.rideRed));
    }

    public void cancel(View view) {
        mCared = (Cared) mCaredSpinner.getSelectedItem();
        if(mCared!=null) {
            fillAllControls(mCared);
        }
    }

    private void setCaredDropdown(Cared[] careds) {
        int layout = simple_spinner_dropdown_item;
        ArrayAdapter<Cared> dataAdapter = new ArrayAdapter<>(this, layout, careds);
        mCaredSpinner.setAdapter(dataAdapter);
        mCaredSpinner.setOnItemSelectedListener(this);
        mCaredSpinner.setSelection(mCaredIndex, true);
    }

    private Cared[] readCaredsFromDatabase() {
        Cursor cursor = null;
        try {
            final ContentResolver contentResolver = this.getContentResolver();
            Uri uri = CaredDbContract.Entry.CONTENT_URI; // Get all entries
            cursor = contentResolver.query(uri, CaredDbProvider.FULL_PROJECTION, null, null, null);
            int arraySize = cursor.getCount();
            Cared[] result = new Cared[arraySize];
            for (int i = 0; i < arraySize; i++) {
                cursor.moveToNext();
                result[i] = new Cared(null).readFromCursor(cursor);
            }
            return result;
        } catch (Exception ex) {
            Log.e(TAG, "execption: ", ex);
            return new Cared[]{};
        }
    }

    public void save(View view) {
        if (mCared != null) {
            readControlsToCared();
            saveToDatabase();
        }
    }

    private int saveToDatabase() {
        if(mCared==null) return 0;//nothing to save
        final ContentResolver cr = this.getContentResolver();
        String id = Integer.toString(mCared.dbId);
        Uri uri = CaredDbContract.Entry.CONTENT_URI.buildUpon().appendPath(id).build();
        ContentValues values = new ContentValues();
        values.put(CaredDbContract.Entry.COLUMN_NAME_DELETED, mCared.deleted);
        values.put(CaredDbContract.Entry.COLUMN_NAME_PHONE, mCared.phone);
        values.put(CaredDbContract.Entry.COLUMN_NAME_AVATAR, mCared.avatar);
        values.put(CaredDbContract.Entry.COLUMN_NAME_WAKE, mCared.wake);
        values.put(CaredDbContract.Entry.COLUMN_NAME_SLEEP, mCared.sleep);
        values.put(CaredDbContract.Entry.COLUMN_NAME_REPORT_YELLOW, mCared.reportYellow);
        values.put(CaredDbContract.Entry.COLUMN_NAME_REPORT_RED, mCared.reportRed);
        values.put(CaredDbContract.Entry.COLUMN_NAME_LOG_YELLOW, mCared.logYellow);
        values.put(CaredDbContract.Entry.COLUMN_NAME_LOG_RED, mCared.logRed);
        values.put(CaredDbContract.Entry.COLUMN_NAME_WALK_YELLOW, mCared.walkYellow);
        values.put(CaredDbContract.Entry.COLUMN_NAME_WALK_RED, mCared.walkRed);
        values.put(CaredDbContract.Entry.COLUMN_NAME_RIDE_YELLOW, mCared.rideYellow);
        values.put(CaredDbContract.Entry.COLUMN_NAME_RIDE_RED, mCared.rideRed);
        return cr.update(uri, values, null, null);
    }

}

