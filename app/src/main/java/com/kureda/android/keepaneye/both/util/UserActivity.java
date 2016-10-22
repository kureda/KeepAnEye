package com.kureda.android.keepaneye.both.util;

/**
 * Created by Sergei Kureda
 */

public class UserActivity {
    private static final String DEFAULT = "0";
    private static final String USER_ACTIVITY = "user_activity";
    private static final String LAST_REPORT = "last_report";
    private static final String LAST_LOGIN = "last_login";
    private static final String LAST_WALK = "last_walk";
    private static final String LAST_RIDE = "last_ride";

    public static void logReporting() {
        Prefs.writeString(USER_ACTIVITY, LAST_REPORT, "" + Util.now());
    }

    public static void logLogging() {
        Prefs.writeString(USER_ACTIVITY, LAST_LOGIN, "" + Util.now());
    }

    public static void logWalking() {
        Prefs.writeString(USER_ACTIVITY, LAST_WALK, "" + Util.now());
    }

    public static void logRiding() {
        Prefs.writeString(USER_ACTIVITY, LAST_RIDE, "" + Util.now());
    }

    /**
     * Get time, in minutes, since last report upload.
     *
     * @return minutes since last upload. Or very big integer, if it never happened
     */
    public static int getTimeSinceLastUpload() {
        String lastUploadTime = Prefs.readString(USER_ACTIVITY, LAST_REPORT);
        if (lastUploadTime == null || lastUploadTime.isEmpty()) {
            return Integer.MAX_VALUE;
        }
        return Util.now() - Integer.parseInt(lastUploadTime);
    }

    //    public static String getName() {
//        return Prefs.readString(USER_ACTIVITY, USER_NAME);
//    }

    public static String getLastUploadTime() {
        return readString(LAST_REPORT);
    }

    public static String getLastLoginTime() {
        return readString(LAST_LOGIN);
    }

    public static String getLastWalkTime() {
        return readString(LAST_WALK);
    }

    public static String getLastRideTime() {
        return readString(LAST_RIDE);
    }


    private static String readString(String key) {
        String value = Prefs.readString(USER_ACTIVITY, key);
        if (value == null || value.isEmpty())
            value = DEFAULT;
        return value;
    }

}
