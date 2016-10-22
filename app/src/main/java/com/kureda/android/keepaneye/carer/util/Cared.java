package com.kureda.android.keepaneye.carer.util;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kureda.android.keepaneye.R;
import com.kureda.android.keepaneye.both.ui.App;
import com.kureda.android.keepaneye.both.util.Util;

import java.io.Serializable;

import static com.kureda.android.keepaneye.carer.db.CaredDbContract.Entry.COLUMN_NAME_AVATAR;
import static com.kureda.android.keepaneye.carer.db.CaredDbContract.Entry.COLUMN_NAME_DELETED;
import static com.kureda.android.keepaneye.carer.db.CaredDbContract.Entry.COLUMN_NAME_ENTRY_ID;
import static com.kureda.android.keepaneye.carer.db.CaredDbContract.Entry.COLUMN_NAME_LOG;
import static com.kureda.android.keepaneye.carer.db.CaredDbContract.Entry.COLUMN_NAME_LOG_RED;
import static com.kureda.android.keepaneye.carer.db.CaredDbContract.Entry.COLUMN_NAME_LOG_YELLOW;
import static com.kureda.android.keepaneye.carer.db.CaredDbContract.Entry.COLUMN_NAME_NAME;
import static com.kureda.android.keepaneye.carer.db.CaredDbContract.Entry.COLUMN_NAME_PHONE;
import static com.kureda.android.keepaneye.carer.db.CaredDbContract.Entry.COLUMN_NAME_REPORT;
import static com.kureda.android.keepaneye.carer.db.CaredDbContract.Entry.COLUMN_NAME_REPORT_RED;
import static com.kureda.android.keepaneye.carer.db.CaredDbContract.Entry.COLUMN_NAME_REPORT_YELLOW;
import static com.kureda.android.keepaneye.carer.db.CaredDbContract.Entry.COLUMN_NAME_RIDE;
import static com.kureda.android.keepaneye.carer.db.CaredDbContract.Entry.COLUMN_NAME_RIDE_RED;
import static com.kureda.android.keepaneye.carer.db.CaredDbContract.Entry.COLUMN_NAME_RIDE_YELLOW;
import static com.kureda.android.keepaneye.carer.db.CaredDbContract.Entry.COLUMN_NAME_SLEEP;
import static com.kureda.android.keepaneye.carer.db.CaredDbContract.Entry.COLUMN_NAME_WAKE;
import static com.kureda.android.keepaneye.carer.db.CaredDbContract.Entry.COLUMN_NAME_WALK;
import static com.kureda.android.keepaneye.carer.db.CaredDbContract.Entry.COLUMN_NAME_WALK_RED;
import static com.kureda.android.keepaneye.carer.db.CaredDbContract.Entry.COLUMN_NAME_WALK_YELLOW;
import static com.kureda.android.keepaneye.carer.db.CaredDbContract.Entry._ID;

/**
 * Created by Sergei Kureda
 * Reads information about cared and displays it to the view.
 * Has some business logic (when display green or red etc)
 */

public class Cared implements Serializable {
    public static final Integer[] AVATARS = new Integer[]{
            R.drawable.ic_avatar_0,
            R.drawable.ic_avatar_1,
            R.drawable.ic_avatar_2,
            R.drawable.ic_avatar_3,
            R.drawable.ic_avatar_4,
            R.drawable.ic_avatar_5,
            R.drawable.ic_avatar_6,
            R.drawable.ic_avatar_7
    };
    //color codes
    public static final int UNKNOWN = 0, GREEN = 1, YELLOW = 2, RED = 3;
    private static String mMinute = "m", mHour = "h", mDay = "d", mWeek = "w", mMonth = "M",
            mYear = "Y";
    public int dbId, avatar, report, log, walk, ride, sleep, wake, reportYellow,
            reportRed, logYellow, logRed, walkYellow, walkRed, rideYellow, rideRed;
    public String phoneId, name, phone;
    public boolean deleted;
    public TextView mNameView, mReportView, mLogView, mWalkView, mRideView;
    private int mNow; //current time (GMT, in minutes)
    private Context mContext;
    private View mView, mPhoneButton;
    private int mReportColor, mLogColor, mWalkColor, mRideColor, mSummaryColor;
    private ImageView mAvatarView;
    //default values, for testing. In other locales they would be different

    public Cared(Context context) {
        //we initalize them here instead of making static final to be able to switch languages
        if (context != null) {
            mContext = context;
            mMinute = mContext.getString(R.string.minute);
            mHour = mContext.getString(R.string.hour);
            mDay = mContext.getString(R.string.day);
            mWeek = mContext.getString(R.string.week);
            mWeek = mContext.getString(R.string.month);
            mWeek = mContext.getString(R.string.year);
        }
    }

    public static int getColor(int i, boolean greenBecomesTransparent) {
        Context context = App.getContext();
         if (i == RED)
            return ContextCompat.getColor(context, R.color.red);
        if (i == YELLOW)
            return ContextCompat.getColor(context, R.color.orange);
        if (i == GREEN) {
            if (greenBecomesTransparent) {
                return ContextCompat.getColor(context, R.color.transparent);
            } else {
                return ContextCompat.getColor(context, R.color.greenA400);
            }
        }
        return ContextCompat.getColor(context, R.color.white); //unknown
    }

    public int getIcon() {
        return AVATARS[avatar];
    }

    // So Cared can be used in spinner
    @Override
    public String toString() {
        return name;
    }

    public Cared readFromCursor(Cursor cursor) {
        dbId = cursor.getInt(cursor.getColumnIndexOrThrow(_ID));
        phoneId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_ENTRY_ID));
        avatar = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_AVATAR));
        name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NAME));
        phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_PHONE));
        report = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_REPORT));
        log = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_LOG));
        walk = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_WALK));
        ride = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_RIDE));
        deleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_DELETED)) == 1;
        sleep = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_SLEEP));
        wake = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_WAKE));
        reportYellow = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_REPORT_YELLOW));
        reportRed = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_REPORT_RED));
        logYellow = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_LOG_YELLOW));
        logRed = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_LOG_RED));
        walkYellow = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_WALK_YELLOW));
        walkRed = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_WALK_RED));
        rideYellow = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_RIDE_YELLOW));
        rideRed = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_RIDE_RED));
        //cursor.close();
        return this;
    }

    public void displayToView(View view) {
        findAllViews(view);
        populateAllViews();
        calculateColorCodes();
        setColors();
    }

    private void findAllViews(View view) {
        mView = view;
        mAvatarView = (ImageView) mView.findViewById(R.id.row_avatar);
        mPhoneButton = mView.findViewById(R.id.phone_call_button);
        mNameView = get(R.id.row_name);
        mReportView = get(R.id.row_report);
        mLogView = get(R.id.row_log);
        mWalkView = get(R.id.row_walk);
        mRideView = get(R.id.row_ride);
    }

    private TextView get(int id) {
        return (TextView) mView.findViewById(id);
    }

    private void populateAllViews() {
        mNow = Util.now();
        //mView.setVisibility(deleted ? View.GONE : View.VISIBLE);//hide the row?
        mAvatarView.setImageResource(AVATARS[avatar]);
        mNameView.setText(name);
        mReportView.setText(ago(report));
        mLogView.setText(ago(log));
        mWalkView.setText(ago(walk));
        mRideView.setText(ago(ride));
        mPhoneButton.setTag(R.id.TAG_PHONE_NUMBER, phone);
    }

    /**
     * Set colors of views depending on how long ago cared was doing this activity
     */
    private void setColors() {
        mReportView.setBackgroundColor(getColor(mReportColor, false));
        mLogView.setBackgroundColor(getColor(mLogColor, false));
        mWalkView.setBackgroundColor(getColor(mWalkColor, false));
        mRideView.setBackgroundColor(getColor(mRideColor, false));
    }

    /**
     * Calculates code colors for all cared activities
     *
     * @return summary color (the worst color code of all activities)
     */
    public int calculateColorCodes() {
        mReportColor = calculateColorCode(report, reportYellow, reportRed);
        mLogColor = calculateColorCode(log, logYellow, logRed);
        mWalkColor = calculateColorCode(walk, walkYellow, walkRed);
        mRideColor = calculateColorCode(ride, rideYellow, rideRed);
        mSummaryColor = max(mReportColor, mLogColor, mWalkColor, mRideColor);
        return mSummaryColor;
    }

    //package access - for running unit tests
    public int calculateColorCode(int value, int yellowThreshold, int redThreshold) {
        if (mNow == 0)
            mNow = Util.now();
        if (value == 0)
            return 0; //no info yet
        int ago = mNow - value;
        if (ago >= redThreshold)
            return RED;
        if (ago >= yellowThreshold)
            return YELLOW;
        return GREEN;
    }

    private int max(int a, int b, int c, int d) {
        return Math.max(Math.max(Math.max(a, b), c), d);
    }


    /**
     * Convert time to string presentation, in how many hours/days ago it was
     *
     * @param when time in GMT
     * @return somethingl like "1h", "5d" or "?" if input was 0
     */
    private String ago(int when) {
        if (when == 0 || when > mNow) {
             return "??";
        }
        int minutesAgo = mNow - when;
        if (minutesAgo < 100)
            return minutesAgo + mMinute;
        int hoursAgo = minutesAgo / 60;
        if (hoursAgo < 48)
            return hoursAgo + mHour;
        int daysAgo = hoursAgo / 24;
        if (daysAgo < 21)
            return daysAgo + mDay;
        int weeksAgo = daysAgo / 7;
        if (weeksAgo < 9)
            return weeksAgo + mWeek;
        int monthsAgo = daysAgo / 30;
        if (monthsAgo < 24)
            return monthsAgo + mMonth;
        int yearsAgo = daysAgo / 365;
        return yearsAgo + mYear;
    }

}
