package com.kureda.android.keepaneye.cared.serv;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.kureda.android.keepaneye.R;
import com.kureda.android.keepaneye.both.ui.App;
import com.kureda.android.keepaneye.both.util.Internet;
import com.kureda.android.keepaneye.both.util.MyJson;
import com.kureda.android.keepaneye.both.util.Res;
import com.kureda.android.keepaneye.both.util.State;
import com.kureda.android.keepaneye.both.util.UserActivity;
import com.kureda.android.keepaneye.both.util.UserDetails;
import com.kureda.android.keepaneye.both.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Sergei Kureda
 * Uploads json file to myjson.com
 */
public class Uploader {

    private static final String TAG = "cared.serv.Uploader";
    private static final int period = getUploadInterval();

    /**
     * If time is right, upload user activity file to myjson.com
     * Since it involves read-write and networking, it is done in a separate thread
     */
    public static void upload() {
        if (timeIsRipe()) {
            if (!Internet.isConnected()) {
                String turnWifiPlease = Res.getString(R.string.turn_wifi);
                Toast.makeText(App.getContext(), turnWifiPlease, Toast.LENGTH_LONG).show();
                State.noInternet();
            } else {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            actuallyUpload();
                        } catch (Exception ex) {
                            Log.e(TAG, "Didn't uploaded this time.", ex);
                            State.noUpload();
                            Util.toast(R.string.could_not_upload);
                        }
                        return null;
                    }
                }.execute();
            }
        }
    }

    //Get upload interval in minutes. From settings.xml (it is there in seconds!)
    private static int getUploadInterval() {
        return Res.getInt(R.integer.activity_upload_interval) / 60;
    }

    private static boolean timeIsRipe() {
        int timeElapsed = UserActivity.getTimeSinceLastUpload();
        boolean result = timeElapsed >= period;
        return result;
    }

    private static void actuallyUpload() throws IOException, JSONException {
        //get user id
        UserDetails user = UserDetails.read();
        String keepAnEyeId = user.getKeepAnEyeId();
        if (keepAnEyeId.isEmpty()) {
            return;
        }

        //get common json
        JSONObject commonJson = MyJson.downloadJson(keepAnEyeId);
        if (commonJson == null) {
            State.noJson();
            Util.toast(R.string.could_not_upload);
            return;
        }

        //generate user json
        JSONObject myJson = MyJson.makeJson(
                App.getId(),
                user.getName(),
                user.getPhone(),
                Integer.toString(Util.now()),
                UserActivity.getLastLoginTime(),
                UserActivity.getLastWalkTime(),
                UserActivity.getLastRideTime()
        );

        //embed user json to general json
        MyJson.insert(commonJson, App.getId(), myJson);

        //upload general json
        MyJson.uploadJson(commonJson, keepAnEyeId);
        State.ok();
        UserActivity.logReporting();

        updateTextOnScreen();
    }

    private static void updateTextOnScreen() {
        App.getContext();

    }
}
