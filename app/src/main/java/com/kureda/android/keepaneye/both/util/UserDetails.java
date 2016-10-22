package com.kureda.android.keepaneye.both.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergei Kureda
 */

public class UserDetails {
    public static final String USER_DETAILS_FILE = "user_details";
    public static final String KEEP_AN_EYE_ID = "keep_an_eye_id";
    private static final String NAME = "name";
    private static final String PHONE = "phone";

    private String mKeepAnEyeId;
    private String mName;
    private String mPhone;

    private UserDetails() {
    }

    public UserDetails(String keepAnEyeId, String name, String phone) {
        mKeepAnEyeId = keepAnEyeId;
        mName = (name == null) ? "" : name;
        mPhone = (phone == null) ? "" : phone;
    }

    /**
     * Reads user details from designated file
     *
     * @return user details or empty strings if not found
     */
    public static UserDetails read() {
        UserDetails result = new UserDetails();
        String[] strings = {KEEP_AN_EYE_ID, NAME, PHONE};
        Map<String, String> map = Prefs.readStrings(USER_DETAILS_FILE, strings);

        result.mKeepAnEyeId = map.get(KEEP_AN_EYE_ID);
        result.mName = map.get(NAME);
        result.mPhone = map.get(PHONE);
        return result;
    }

    public void save() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(KEEP_AN_EYE_ID, mKeepAnEyeId);
        map.put(NAME, mName);
        map.put(PHONE, mPhone);
        Prefs.writeStrings(USER_DETAILS_FILE, map);
    }

    public String getName() {
        return mName;
    }

    public String getPhone() {
        return mPhone;
    }

    public String getKeepAnEyeId() {
        return mKeepAnEyeId;
    }

    public String toString() {
        return "keepAnEyeId=" + mKeepAnEyeId + ", name=" + mName + ", phone=" + mPhone;
    }

    public boolean isEmpty() {
        return mKeepAnEyeId.isEmpty() && mName.isEmpty() && mPhone.isEmpty();
    }
}
