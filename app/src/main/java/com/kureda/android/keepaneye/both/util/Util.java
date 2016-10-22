package com.kureda.android.keepaneye.both.util;

import android.widget.Toast;

import com.kureda.android.keepaneye.both.ui.App;

import java.util.Calendar;
import java.util.TimeZone;

import static com.kureda.android.keepaneye.both.util.Res.getString;

/**
 * Created by Sergei Kureda
 * Set of various utility methods
 */

public class Util {

    private static final long MINUTE = 60000L; //milliseconds in one minute
    public static final String UTC = "UTC";

    /**
     * Timestamp
     *
     * @return the current time as UTC minutes from the epoch
     */
    public static int now() {
        //returns something in range of 24580800
        return (int) (Calendar.getInstance(TimeZone.getTimeZone(UTC)).getTimeInMillis() / MINUTE);
    }

    public static void toast(int stringCode) {
        Toast.makeText(App.getContext(), getString(stringCode), Toast.LENGTH_LONG).show();
    }
}
