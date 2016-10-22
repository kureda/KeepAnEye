package com.kureda.android.keepaneye.both.util;

import android.support.annotation.NonNull;
import android.util.Log;

import com.kureda.android.keepaneye.R;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.kureda.android.keepaneye.both.util.UserDetails.USER_DETAILS_FILE;

/**
 * Created by Sergei Kureda
 */

public class MyJson {
    private static final String TAG = "both.util.MyJson";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final int OK = 200;
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PHONE = "phone";
    private static final String REPORT = "report";
    private static final String LOGIN = "login";
    private static final String WALK = "walk";
    private static final String RIDE = "ride";

    //url of the myjson.com site
    //for testing: https://api.myjson.com/bins/5bwtg  for alphanum=5bwtg, num=33425954
    public static final String MY_JSON_URL = "https://api.myjson.com/bins/";

    //elements of the json file
    public static final String MEMBERS = "members";
    private static final String KEEP_AN_EYE_ID = "keep_an_eye_id";

    //number of digits in alphanumerical alphabet (0-z), first char is reserved
    private static final long BASE = 37L;

    /**
     * Convert alphadigital code to gigital one by converting it to base-10 system
     * @param inputString string to convert
     * @return converted string
     */
    public static String toNumbers(String inputString) {
        if (inputString.length() > 11L) {
            String problem = Res.getString(R.string.too_long_mjson_id_string) + inputString;
            throw new IllegalArgumentException(problem);
        }
        char[] input = inputString.toCharArray();
        long output = 0L;
        long register = 1L;
        for (char d : input) {
            long i = d;
            i = toBase37(i);
            output = output + i * register;
            register *= BASE;
        }
        return "" + output;
    }

    /**
     * Convert digital code back to alphanumerical one by converting it back to base-36 system.
     */
    static String toAlphanumbers(String inputString) {
        long input = Long.parseLong(inputString);
        String output = "";
        while (input > 0L) {
            long remainder = input % BASE;
            input = input / BASE;
            char c = fromBase37((long) remainder);
            output = output + c;
        }
        return output;
    }

    private static char fromBase37(long i) {
        if (i < 11L) {
            i = i + 47L; //1 -> 0
        } else if (i < 38L) {
            i = i + 86L; //11-> a
        } else {
            String problem = "\"" + i + "\"" +Res.getString(R.string.is_not_character);
            throw new IllegalArgumentException(problem);
        }
        return (char) i;
    }

    private static long toBase37(long i) {
        if (i > 96L && i < 123L) {
            i = i - 86L; //a -> 11
        } else if (i > 47L && i < 58L) {
            i = i - 47L; //0-> 1
        } else {
            throw new IllegalArgumentException("\"" + i + "\" allowed only digits or low-case " +
                    "letters");
        }
        return i;
    }

    @NonNull
    private static String makeUrl(String keepAnEyeId) {
        String alphanum = toAlphanumbers(keepAnEyeId); //something like "3ug1s"
        return MY_JSON_URL + alphanum;
    }

    public static void uploadJson(JSONObject json, String keepAnEyeId) throws IOException {
        String url = makeUrl(keepAnEyeId);
        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder().url(url).put(body).build();
        Response response = Internet.getHttpClient().newCall(request).execute();
        response.body().close();//just in case
    }

    /**
     * Replace or append json object into an array of objects
     * (replace - if object with the same id already exists)
     *
     * @param parent object to insert/append into
     * @param id     id of object to be replaced
     * @param child  object to insert/append
     * @throws JSONException
     */
    public static void insert(JSONObject parent, String id, JSONObject child) throws JSONException {
        JSONArray members = parent.getJSONArray(MEMBERS);
        for (int i = 0; i < members.length(); i++) {
            JSONObject member = members.getJSONObject(i);
            String value = member.getString(ID);
            if (value.equals(id)) {
                members.put(i, child); //replace
                return;
            }
        }
        members.put(child); //append
    }

    public static JSONArray downloadCared(String keepAnEyeId) {
        try {
            JSONObject json = downloadJson(keepAnEyeId);
            if (json != null) {
                return json.getJSONArray(MEMBERS);
            } else {
                return new JSONArray();
            }
        } catch (Exception ex) {
            Log.e(TAG, ": cannot load json:" + ex);
        }
        return null;
    }

    public static JSONObject downloadJson(String keepAnEyeId) throws IOException, JSONException {
        String url = makeUrl(keepAnEyeId);
        OkHttpClient client = Internet.getHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        String responseString = response.body().string();
        if (response.code() != OK)
            return null; //no such json?
        JSONObject json = new JSONObject(responseString);
        response.body().close();//just in case
        if (wrongJson(json))
            return null;
        return json;
    }

    //validate downloaded json file
    private static boolean wrongJson(JSONObject json) {
        return json == null || !json.has(MEMBERS) || !json.has(KEEP_AN_EYE_ID);
    }

    public static JSONObject makeJson(String id,
                                      String userName,
                                      String phone,
                                      String upTime,
                                      String logTime,
                                      String walkTime,
                                      String rideTime) {
        JSONObject object = new JSONObject();
        try {
            object.put(ID, id);
            object.put(NAME, userName);
            object.put(PHONE, phone);
            object.put(REPORT, upTime);
            object.put(LOGIN, logTime);
            object.put(WALK, walkTime);
            object.put(RIDE, rideTime);
        } catch (JSONException ex) {
            Log.e(TAG, "Cannot create Json", ex);
        }
        return object;
    }

    public static String[][] toDoubleArray(JSONArray array) {
        String[][] result = new String[0][0];
        if (array != null && array.length() != 0) {
            try {
                int length = array.length();
                result = new String[length][7];
                for (int i = 0; i < array.length(); i++) {
                    String[] memberS = new String[7];
                    JSONObject memberJ = array.getJSONObject(i);
                    memberS[0] = getString(memberJ, ID);
                    memberS[1] = getString(memberJ, NAME);
                    memberS[2] = getString(memberJ, PHONE);
                    memberS[3] = getString(memberJ, REPORT);
                    memberS[4] = getString(memberJ, LOGIN);
                    memberS[5] = getString(memberJ, WALK);
                    memberS[6] = getString(memberJ, RIDE);
                    result[i] = memberS;
                }

            } catch (JSONException ex) {
                Log.e(TAG, ": json exception: ", ex);
            }
        }
        return result;
    }

    private static String getString(JSONObject json, String key) throws JSONException {
        if (json.has(key)) {
            return json.getString(key);
        } else {
            return "";
        }
    }

    public static void saveKeepAnEyeId(String alphanumId) {
        String keepAnEyeId = toNumbers(alphanumId);
        Prefs.writeString(USER_DETAILS_FILE, UserDetails.KEEP_AN_EYE_ID, keepAnEyeId);
    }

    public static String getKeepAnEyeId() {
        return Prefs.readString(USER_DETAILS_FILE, UserDetails.KEEP_AN_EYE_ID);
    }
}