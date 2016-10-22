package com.kureda.android.keepaneye.both.util;

import android.content.Context;

import com.kureda.android.keepaneye.both.ui.App;

/**
 * Created by Sergei Kureda
 * Access to resources
 */

public class Res {

    /**
     * Get string from strings.xml using static context
     *
     * @param resourceId id of resource to read
     * @return the string
     */
    public static String getString(int resourceId) {
        Context context = App.getContext();
        return context.getString(resourceId);
    }

    public static int getInt(int resourceId) {
        Context context = App.getContext();
        return context.getResources().getInteger(resourceId);
    }

    public static String getString(int resourceId, Object... formatArgs) {
        Context context = App.getContext();
        return context.getString(resourceId, formatArgs);
    }

    static String getQuantityString(int resourceId, int howMuch) {
        return App.getContext().getResources().getQuantityString(resourceId, howMuch, howMuch);
    }

}
