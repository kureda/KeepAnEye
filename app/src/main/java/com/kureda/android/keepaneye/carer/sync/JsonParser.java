/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kureda.android.keepaneye.carer.sync;

import com.kureda.android.keepaneye.both.util.MyJson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergei Kureda
 * Parsers json file to a list of Entry
 */
public class JsonParser {

    static List<Entry> parse (JSONArray jsonArray){

        String[][] members = MyJson.toDoubleArray(jsonArray);
        List<Entry> result = new ArrayList<>();
        for(String[] m: members){
            Entry memberE = new Entry(m[0],m[1],m[2],m[3],m[4],m[5],m[6]);
            result.add(memberE);
        }
        return result;
    }


    static int parseInt(String integer){
        try {
            return Integer.parseInt(integer);
        }catch (NumberFormatException ex){
            return 0;
        }
    }
}
