package com.kureda.android.keepaneye.both.util;

import android.content.SharedPreferences;

import com.kureda.android.keepaneye.both.ui.App;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergei Kureda
 * To (read from / write to) shared preferences
 */
public class Prefs {

    /**
     * Reads string from shared preferences
     *
     * @param fileName Name of file to read from
     * @param key      key for value to read
     * @return string, if found. If not found, returns empty string.
     */
    static String readString(String fileName, String key) {
        SharedPreferences preferences = App.getContext().getSharedPreferences(fileName, 0);
        return preferences.getString(key, "");
    }

    public static void writeString(String fileName, String key, String value) {
        SharedPreferences preferences = App.getContext().getSharedPreferences(fileName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    static void writeStrings(String fileName, Map<String, String> map) {
        SharedPreferences preferences = App.getContext().getSharedPreferences(fileName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            editor.putString(key, value);
        }
        editor.apply();
    }

    /**
     * Read several strings at once, for performance
     *
     * @param filename name of file to read from
     * @param keys     keys to read
     * @return values, corresponding to keys. Of "" if not found.
     */
    static Map<String, String> readStrings(String filename, String[] keys) {
        SharedPreferences preferences = App.getContext().getSharedPreferences(filename, 0);
        Map<String, String> map = new HashMap<>();
        for (String key : keys) {
            map.put(key, preferences.getString(key, ""));
        }
        return map;
    }
}
