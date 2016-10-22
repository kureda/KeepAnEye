package com.kureda.android.keepaneye.both.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.kureda.android.keepaneye.R;
import com.kureda.android.keepaneye.both.ui.App;

/**
 * Created by Sergei Kureda
 */

public class Phone {
    static boolean isPhoneNumber(String phoneNumber) {
        return PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber);
    }

    private static boolean itIsPhone() {
        Context context = App.getContext();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context
                .TELEPHONY_SERVICE);
        return tm != null && tm.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    public static void call(String phoneNumber, Activity activity) {
        if (!itIsPhone()) {
            Util.toast(R.string.this_is_not_phone);
        } else if (!isPhoneNumber(phoneNumber)) {
            Util.toast(R.string.cared_fix_phone_number);
        } else {
            Intent dial = new Intent(Intent.ACTION_DIAL); // you don't need permits for dial :)
            dial.setData(Uri.parse("tel:" + phoneNumber));
            activity.startActivity(dial);
        }
    }

}
