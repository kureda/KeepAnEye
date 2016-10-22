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

package com.kureda.android.keepaneye.carer.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Sergei Kureda
 * Field and table name constants for
 * {@link CaredDbProvider}.
 */
public class CaredDbContract {

    // Content provider authority.
    public static final String CONTENT_AUTHORITY = "com.kureda.android.keepaneye";

    // Base URI. (content://com.kureda.android.keepaneye)
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Path component for "entry"-type resources..
    private static final String PATH_ENTRIES = "entries";

    // To prevent accidental creation
    private CaredDbContract() {
    }

    //Columns supported by "entries" records.
    public static class Entry implements BaseColumns {

        // Fully qualified URI for "entry" resources.
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRIES).build();
        // Id of phone of cared. Taken from json. It is NOT database primary key _ID.
        public static final String COLUMN_NAME_ENTRY_ID = "entry_id";
        // Cared name. Something like "Dad"
        public static final String COLUMN_NAME_NAME = "name";
        // Carer phone number
        public static final String COLUMN_NAME_PHONE = "phone";
        // Time (in minutes) when last report from carer came
        public static final String COLUMN_NAME_REPORT = "report";
        // Time (in minutes) when cared last time logged in his smartphone
        public static final String COLUMN_NAME_LOG = "log";
        //Time (in minutes) when cared last time walked
        public static final String COLUMN_NAME_WALK = "walk";
        // Time (in minutes) when cared last time took a ride
        public static final String COLUMN_NAME_RIDE = "ride";
        // 1 means row is deleted (i.e. not displayed); 0 means not
        public static final String COLUMN_NAME_DELETED = "deleted";
        // Number of avatar icon to display
        public static final String COLUMN_NAME_AVATAR = "avatar";
        // Time (in gmt, minutes since midnight) when cared goes to sleep
        public static final String COLUMN_NAME_SLEEP = "sleep";
        // Time (in gmt, minutes since midnight) when cared wakes up
        public static final String COLUMN_NAME_WAKE = "wake";
        // Time (in minutes) since last report when displays turns yellow
        public static final String COLUMN_NAME_REPORT_YELLOW = "report_yellow";
        // Time (in minutes) since last cared report when displays turns red
        public static final String COLUMN_NAME_REPORT_RED = "report_red";
        // Time (in minutes) since last cared login when displays turns yellow
        public static final String COLUMN_NAME_LOG_YELLOW = "log_yellow";
        // Time (in minutes) since last cared login when displays turns red
        public static final String COLUMN_NAME_LOG_RED = "log_red";
        // Time (in minutes) since last cared walk when displays turns yellow
        public static final String COLUMN_NAME_WALK_YELLOW = "walk_yellow";
        // Time (in minutes) since last cared walk when displays turns red
        public static final String COLUMN_NAME_WALK_RED = "walk_red";
        // Time (in minutes) since last cared ride when displays turns yellow
        public static final String COLUMN_NAME_RIDE_YELLOW = "ride_yellow";
        // Time (in minutes) since last cared ride when displays turns red
        public static final String COLUMN_NAME_RIDE_RED = "ride_red";
        //MIME type for lists of entries.
        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.keepaneye.entries";
        //MIME type for individual entries.
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.keepaneye.entry";
        // Table name where records are stored for "entry" resources.
        static final String TABLE_NAME = "entry";

    }
}