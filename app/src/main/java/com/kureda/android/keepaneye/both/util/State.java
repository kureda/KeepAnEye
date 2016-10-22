package com.kureda.android.keepaneye.both.util;

/**
 * Created by Sergei Kureda
 * Results of last attempt to upload data.
 * Kept in shared preference and displayed on main screen.
 */
public class State {
    private static final String UPLOAD_RESULTS_FILE = "upload_results_file"; //filename
    private static final String UPLOAD_RESULTS_KEY = "upload_results_key"; //filename


    private static final String NO_INTERNET = "no_internet";
    private static final String NO_ID = "no_id"; //no keep an eye id
    private static final String NO_JSON = "no_json"; // no global json file found in myjson.com
    private static final String NO_UPLOAD = "no_json"; // exception during upload attempt
    private static final String OK = "OK"; //uploaded_successfully

    public static void noInternet() {
        writeResult(NO_INTERNET);
    }

    public static void noId() {
        writeResult(NO_ID);
    }

    public static void noJson() {
        writeResult(NO_JSON);
    }

    public static void noUpload() {
        writeResult(NO_UPLOAD);
    }

    public static void ok() {
        writeResult(OK);
    }

    /**
     * Get result of last attempt to upload user activity
     *
     * @return string with result description
     */
    public static String get() {
        return Prefs.readString(UPLOAD_RESULTS_FILE, UPLOAD_RESULTS_KEY);
    }

    private static void writeResult(String result) {
        Prefs.writeString(UPLOAD_RESULTS_FILE, UPLOAD_RESULTS_KEY, result);
    }
}
