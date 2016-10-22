package com.kureda.android.keepaneye.both.util;


/**
 * Created by Sergei Kureda
 * Possible modes of application.
 */

public enum Mode {
    CARED("CARED"), // cared mode chosen, settings complete
    CARER("CARER"), // carer mode chosen, settings complete
    INSTALL("INSTALL"), // no mode chosen yet
    CARER_INSTALL("CARER_INSTALL"); // carer mode chosen, settings not yet complete

    public static final String PREFS_NAME = "ModePrefsFile";

    public static final String KEY = "MODE";
    private String value;

    Mode(String value) {
        this.value = value;
    }

    private static Mode parse(String mode) {
        if (CARER.value.equals(mode))
            return CARER;
        if (CARED.value.equals(mode))
            return CARED;
        if (CARER_INSTALL.value.equals(mode))
            return CARER_INSTALL;
        return INSTALL; //no mode registered yet
    }

    /**
     * Reads mode from shared preference
     */
    public static Mode getMode() {
        String prefsName = Prefs.readString(PREFS_NAME, KEY);
        return Mode.parse(prefsName);
    }

    /**
     * Saves mode to shared preference
     */
    public void save() {
        Prefs.writeString(PREFS_NAME, KEY, value);
    }

    @Override
    public String toString() {
        return value;
    }
}
