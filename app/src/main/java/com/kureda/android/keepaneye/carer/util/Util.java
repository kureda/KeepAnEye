package com.kureda.android.keepaneye.carer.util;

/**
 * Created by Sergei Kureda
 */

public class Util {

    static final int THOUSAND_YEARS = 60 * 24 * 365 * 1000;
    private static int[] intervals = {
            60,
            60 * 2,
            60 * 3,
            60 * 4,
            60 * 6,
            60 * 8,
            60 * 12,
            60 * 18,
            60 * 24,
            60 * 36,
            60 * 24 * 2,
            60 * 24 * 3,
            60 * 24 * 4,
            60 * 24 * 7,
            60 * 24 * 10,
            60 * 24 * 14,
            60 * 24 * 21,
            60 * 24 * 30,
            THOUSAND_YEARS};

    //convert hours to dropdown index. See the list in res/values/arrays.xml: hour_array
    public static int hoursToIndex(int hours) {
        if (hours <= 0) {
            return 0;
        } else if (hours < 24)
            return hours - 1;
        else
            return 23;
    }

    //convert dropdown index to hours. See the list in res/values/arrays.xml: hour_array
    public static int indexToHours(int index) {
        if (index < 0)
            return 1;
        if (index > 23)
            return 24;
        return index + 1;
    }

    public static int indexToInterval(int index) {
        if (index < 0 || index > intervals.length)
            return 60;
        return intervals[index];
    }

    public static int intervalToIndex(int interval) {
        for (int i = 0; i < intervals.length; i++) {
            if (intervals[i] == interval)
                return i;
        }
        if (interval < 60)
            return 0;
        return 18;//forever
    }
}
/*    <string-array name="alert_array">
        <item>1 hr</item>
        <item>2 hrs</item>
        <item>3 hrs</item>
        <item>4 hrs</item>
        <item>6 hrs</item>
        <item>8 hrs</item>
        <item>12 hrs</item>
        <item>18 hrs</item>
        <item>1 day</item>
        <item>1.5 ds</item>
        <item>2 days</item>
        <item>3 days</item>
        <item>4 days</item>
        <item>1 week</item>
        <item>1.5 w</item>
        <item>2 wks</item>
        <item>3 wks</item>
        <item>1 mnth</item>
        <item>never</item>
    </string-array>

     */
