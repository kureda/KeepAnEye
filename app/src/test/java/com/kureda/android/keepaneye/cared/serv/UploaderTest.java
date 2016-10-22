package com.kureda.android.keepaneye.cared.serv;

import com.kureda.android.keepaneye.both.util.MyJson;

import org.json.JSONObject;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Serg on 10/1/2016.
 */

public class UploaderTest {

    String before = " {                                                              " +
            "  \"group_id\": \"5265465456546\",                                      " +
            "  \"members\": [{                                                       " +
            "        \"name\": \"Dad\",                                              " +
            "        \"id\": \"1000001\",                                            " +
            "                \"last_report\": \"346235623454\",                      " +
            "                \"last_login\": \"3462398978978\",                      " +
            "                \"last_walk\": \"34623234213\",                         " +
            "                \"last_ride\": \"34623111515\"                          " +
            "  }, {                                                                  " +
            "        \"name\": \"Mom\",                                              " +
            "        \"id\": \"1000002\",                                            " +
            "                \"last_report\": \"34623567777\",                       " +
            "                \"last_login\": \"34623984444\",                        " +
            "                \"last_walk\": \"346239999\",                           " +
            "                \"last_ride\": \"346222222\"                            " +
            "    }]                                                                  " +
            " }                                                                      ";
    String change = " {                                                              " +
            "        \"name\": \"Mommy\",                                            " +
            "        \"id\": \"1000002\",                                            " +
            "                \"last_walk\": \"fasdf\",                               " +
            "                \"last_ride\": \"sdfgh\"                                " +
            " }                                                                      ";
    String after = " {                                                                " +
            "  \"group_id\": \"5265465456546\",                                      " +
            "  \"members\": [{                                                       " +
            "        \"id\": \"1000001\",                                            " +
            "        \"name\": \"Dad\",                                              " +
            "                \"last_report\": \"346235623454\",                      " +
            "                \"last_login\": \"3462398978978\",                      " +
            "                \"last_walk\": \"34623234213\",                         " +
            "                \"last_ride\": \"34623111515\"                          " +
            "  }, {                                                                  " +
            "        \"id\": \"1000002\",                                            " +
            "        \"name\": \"Mommy\",                                            " +
            "                \"last_walk\": \"fasdf\",                               " +
            "                \"last_ride\": \"sdfgh\"                                " +
            "    }]                                                                  " +
            " }                                                                      ";

    @Test
    public void testReplaceJson() {
        JSONObject a = null, b = null, c = null;
        try {
            b = new JSONObject(before);
            c = new JSONObject(change);
            a = new JSONObject(after);
            MyJson.insert(b, "1000002", c);
        } catch (Exception ex) {
            System.out.println(ex.getStackTrace());
        }
        assertThat(b.toString(), is(a.toString()));
    }

}

