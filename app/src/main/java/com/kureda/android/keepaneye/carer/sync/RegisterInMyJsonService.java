package com.kureda.android.keepaneye.carer.sync;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.kureda.android.keepaneye.R;
import com.kureda.android.keepaneye.both.ui.App;
import com.kureda.android.keepaneye.both.util.Internet;
import com.kureda.android.keepaneye.both.util.MyJson;
import com.kureda.android.keepaneye.both.util.Res;
import com.kureda.android.keepaneye.both.util.State;
import com.kureda.android.keepaneye.carer.ui.CarerMainActivity;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.kureda.android.keepaneye.both.util.Mode.CARER;
import static com.kureda.android.keepaneye.both.util.MyJson.JSON;
import static com.kureda.android.keepaneye.both.util.MyJson.MEMBERS;
import static com.kureda.android.keepaneye.both.util.MyJson.MY_JSON_URL;
import static com.kureda.android.keepaneye.both.util.UserDetails.KEEP_AN_EYE_ID;

/**
 * Creted by Sergei Kureda
 */
public class RegisterInMyJsonService extends IntentService {

    private static final String TAG = "RegisterInMyJsonService";

    /**
     * Need this, for IntentService threading internals
     */
    public RegisterInMyJsonService() {
        super("RegisterInMyJsonService");
    }

    /**
     * Register in myjson.com, to get id and put the skeleton json file there
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (Internet.isConnected()) {
            try {
                JSONObject json = createDummyJson();
                String id = postToMyJson(json); //uploading dummy json, to get url
                if (id == null)
                    return; //cannot create id
                json = createRealJson(id);
                putToMyJson(json, id); //uploading real json file to the url
                MyJson.saveKeepAnEyeId(id);
                goToMainCarerActivity();
            } catch (Exception ex) {
                Log.e(TAG, "Problem creating json.", ex);
            }
        } else {
            String noWifi = Res.getString(R.string.turn_wifi);
            Toast.makeText(App.getContext(), noWifi, Toast.LENGTH_LONG).show();
            State.noInternet();
        }
    }

    private void goToMainCarerActivity() {
        CARER.save();
        Intent intent = new Intent(this, CarerMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        stopSelf();
    }


    private String postToMyJson(JSONObject json) throws IOException {
         RequestBody body = RequestBody.create(MyJson.JSON, json.toString());
        Request request = new Request.Builder().url(MY_JSON_URL).post(body).build();
        Response response = Internet.getHttpClient().newCall(request).execute();
        String string = response.body().string();
        String alphanum = parseResponseFromMyJson(string);
        response.body().close();//just in case
        return alphanum;
    }

    /**
     * Parse response string from myjson.com to get id
     * expected string is: 201 Created - {"uri":"https://api.myjson.com/bins/:id"}
     *
     * @param string input from server,
     *               something like expected string: 201 Created - {"uri":"https://api.myjson
     *               .com/bins/:id"}
     * @return id or null if string was different
     */
    private String parseResponseFromMyJson(String string) {
        try {
            int from = string.lastIndexOf("/") + 1;
            int to = string.length() - 2;//remove "}
            String alphanum = string.substring(from, to);
            return alphanum;
        } catch (Exception ex) {
            Log.e(TAG, ": wrong response from mysjon.com:" + string);
            return null;
        }
    }

    private void putToMyJson(JSONObject json, String urlId) throws IOException {
         RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder().url(MY_JSON_URL + "/" + urlId).put(body).build();
        Response response = Internet.getHttpClient().newCall(request).execute();
        response.body().close();//just in case
    }

    private JSONObject createDummyJson() throws JSONException {
        return new JSONObject("{\"a\":\"b\"}");
    }

    /**
     * Create json file for group of cared users
     *
     * @return generated json
     */

    private JSONObject createRealJson(String id) throws JSONException {
        String keepAnEyeId = MyJson.toNumbers(id);
        JSONObject json = new JSONObject();
        json.put(KEEP_AN_EYE_ID, keepAnEyeId);
        json.put(MEMBERS, new JSONArray());
        return json;
    }

/*
  {
 "keep_an_eye_id":"1000000000",
 "members":[
 {
 "id":"0000000000000",
 "name":"Mom",
 "report":"0",
 "login":"0",
 "walk":"0",
 "ride":"0"
 },
 {
 "id":"1111111111111111",
 "name":"Granny",
 "report":"0",
 "login":"0",
 "walk":"0",
 "ride":"0"
 },
 {
 "id":"fe00d71afbe39fd3",
 "name":"",
 "report":"0",
 "login":"24589719",
 "walk":"0",
 "ride":"0"
 }
 ]
 }
  */

}