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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by Sergei Kureda
 */
public class CaredDbProvider extends ContentProvider {
    /**
     * URI ID for route: /entries
     */
    public static final int ROUTE_ENTRIES = 1;
    /**
     * URI ID for route: /entries/{ID}
     */
    public static final int ROUTE_ENTRIES_ID = 2;

    /**
     * Project used when updating content provider. All fields that loaded from cloud (myjson.com)
     */
    public static final String[] UPDATE_PROJECTION = new String[]{
            CaredDbContract.Entry._ID,
            CaredDbContract.Entry.COLUMN_NAME_ENTRY_ID,
            CaredDbContract.Entry.COLUMN_NAME_NAME,
            CaredDbContract.Entry.COLUMN_NAME_PHONE,
            CaredDbContract.Entry.COLUMN_NAME_REPORT,
            CaredDbContract.Entry.COLUMN_NAME_LOG,
            CaredDbContract.Entry.COLUMN_NAME_WALK,
            CaredDbContract.Entry.COLUMN_NAME_RIDE};

    /**
     * Projection for querying the content provider. All fields. Used for display.
     */
    public static final String[] FULL_PROJECTION = new String[]{
            CaredDbContract.Entry._ID,
            CaredDbContract.Entry.COLUMN_NAME_ENTRY_ID,
            CaredDbContract.Entry.COLUMN_NAME_NAME,
            CaredDbContract.Entry.COLUMN_NAME_PHONE,
            CaredDbContract.Entry.COLUMN_NAME_REPORT,
            CaredDbContract.Entry.COLUMN_NAME_LOG,
            CaredDbContract.Entry.COLUMN_NAME_WALK,
            CaredDbContract.Entry.COLUMN_NAME_RIDE,
            CaredDbContract.Entry.COLUMN_NAME_DELETED,
            CaredDbContract.Entry.COLUMN_NAME_AVATAR,
            CaredDbContract.Entry.COLUMN_NAME_SLEEP,
            CaredDbContract.Entry.COLUMN_NAME_WAKE,
            CaredDbContract.Entry.COLUMN_NAME_REPORT_YELLOW,
            CaredDbContract.Entry.COLUMN_NAME_REPORT_RED,
            CaredDbContract.Entry.COLUMN_NAME_LOG_YELLOW,
            CaredDbContract.Entry.COLUMN_NAME_LOG_RED,
            CaredDbContract.Entry.COLUMN_NAME_WALK_YELLOW,
            CaredDbContract.Entry.COLUMN_NAME_WALK_RED,
            CaredDbContract.Entry.COLUMN_NAME_RIDE_YELLOW,
            CaredDbContract.Entry.COLUMN_NAME_RIDE_RED
    };

    /**
     * Content authority for this provider.
     */
    private static final String AUTHORITY = CaredDbContract.CONTENT_AUTHORITY;
    /**
     * UriMatcher, used to decode incoming URIs.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, "entries", ROUTE_ENTRIES);
        sUriMatcher.addURI(AUTHORITY, "entries/*", ROUTE_ENTRIES_ID);
    }

    FeedDatabase mDatabaseHelper;

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new FeedDatabase(getContext());
        return true;
    }

    /**
     * Determine the mime type for entries returned by a given URI.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ROUTE_ENTRIES:
                return CaredDbContract.Entry.CONTENT_TYPE;
            case ROUTE_ENTRIES_ID:
                return CaredDbContract.Entry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Perform a database query by URI.
     * <p>
     * <p>Currently supports returning all entries (/entries) and individual entries by ID
     * (/entries/{ID}).
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[]
            selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        SelectionBuilder builder = new SelectionBuilder();
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case ROUTE_ENTRIES_ID:
                // Return a single entry, by ID.
                String id = uri.getLastPathSegment();
                builder.where(CaredDbContract.Entry._ID + "=?", id);
            case ROUTE_ENTRIES:
                // Return all known entries.
                builder.table(CaredDbContract.Entry.TABLE_NAME)
                        .where(selection, selectionArgs);
                Cursor cursor = builder.query(db, projection, sortOrder);
                // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                Context context = getContext();
                assert context != null;
                cursor.setNotificationUri(context.getContentResolver(), uri);
                return cursor;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Insert a new entry into the database.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        assert db != null;
        final int match = sUriMatcher.match(uri);
        Uri result;
        switch (match) {
            case ROUTE_ENTRIES:
                long id = db.insertOrThrow(CaredDbContract.Entry.TABLE_NAME, null, values);
                result = Uri.parse(CaredDbContract.Entry.CONTENT_URI + "/" + id);
                break;
            case ROUTE_ENTRIES_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return result;
    }

    /**
     * Delete an entry by database by URI.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case ROUTE_ENTRIES:
                count = builder.table(CaredDbContract.Entry.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_ENTRIES_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(CaredDbContract.Entry.TABLE_NAME)
                        .where(CaredDbContract.Entry._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    /**
     * Update an etry in the database by URI.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[]
            selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case ROUTE_ENTRIES:
                count = builder.table(CaredDbContract.Entry.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_ENTRIES_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(CaredDbContract.Entry.TABLE_NAME)
                        .where(CaredDbContract.Entry._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    /**
     * SQLite backend for @{link FeedProvider}.
     * <p>
     * Provides access to an disk-backed, SQLite datastore which is utilized by FeedProvider. This
     * database should never be accessed by other parts of the application directly.
     */
    static class FeedDatabase extends SQLiteOpenHelper {
        /**
         * Schema version.
         */
        static final int DATABASE_VERSION = 1;
        /**
         * Filename for SQLite file.
         */
        static final String DATABASE_NAME = "feed.db";

        private static final String TYPE_TEXT = " TEXT";
        private static final String TYPE_INTEGER = " INTEGER";
        private static final String DEFAULT_0 = " DEFAULT 0, ";
        private static final String DEFAULT_1 = " DEFAULT 1, ";
        private static final String DEFAULT_HD = " DEFAULT 720, "; // 720 minutes i.e. half-day
        private static final String DEFAULT_DAY = " DEFAULT 1440, ";
        private static final String DEFAULT_WEEK = " DEFAULT 10080, ";
        private static final String DEFAULT_MONTH = " DEFAULT 43200";
        private static final String COMMA_SEP = ",";
        /**
         * SQL statement to create "entry" table.
         */
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + CaredDbContract.Entry.TABLE_NAME + " (" +
                        CaredDbContract.Entry._ID + " INTEGER PRIMARY KEY," +
                        CaredDbContract.Entry.COLUMN_NAME_ENTRY_ID + TYPE_TEXT + COMMA_SEP +
                        CaredDbContract.Entry.COLUMN_NAME_NAME + TYPE_TEXT + COMMA_SEP +
                        CaredDbContract.Entry.COLUMN_NAME_PHONE + TYPE_TEXT + COMMA_SEP +
                        CaredDbContract.Entry.COLUMN_NAME_REPORT + TYPE_INTEGER + DEFAULT_0 +
                        CaredDbContract.Entry.COLUMN_NAME_LOG + TYPE_INTEGER + DEFAULT_0 +
                        CaredDbContract.Entry.COLUMN_NAME_WALK + TYPE_INTEGER + DEFAULT_0 +
                        CaredDbContract.Entry.COLUMN_NAME_RIDE + TYPE_INTEGER + DEFAULT_0 +
                        CaredDbContract.Entry.COLUMN_NAME_DELETED + TYPE_INTEGER + DEFAULT_0 +
                        CaredDbContract.Entry.COLUMN_NAME_AVATAR + TYPE_INTEGER + DEFAULT_0 +
                        CaredDbContract.Entry.COLUMN_NAME_SLEEP + TYPE_INTEGER + DEFAULT_0 +
                        CaredDbContract.Entry.COLUMN_NAME_WAKE + TYPE_INTEGER + DEFAULT_1 +
                        CaredDbContract.Entry.COLUMN_NAME_REPORT_YELLOW + TYPE_INTEGER +
                        DEFAULT_HD +
                        CaredDbContract.Entry.COLUMN_NAME_REPORT_RED + TYPE_INTEGER + DEFAULT_DAY +
                        CaredDbContract.Entry.COLUMN_NAME_LOG_YELLOW + TYPE_INTEGER + DEFAULT_HD +
                        CaredDbContract.Entry.COLUMN_NAME_LOG_RED + TYPE_INTEGER + DEFAULT_DAY +
                        CaredDbContract.Entry.COLUMN_NAME_WALK_YELLOW + TYPE_INTEGER + DEFAULT_DAY +
                        CaredDbContract.Entry.COLUMN_NAME_WALK_RED + TYPE_INTEGER + DEFAULT_WEEK +
                        CaredDbContract.Entry.COLUMN_NAME_RIDE_YELLOW + TYPE_INTEGER +
                        DEFAULT_WEEK +
                        CaredDbContract.Entry.COLUMN_NAME_RIDE_RED + TYPE_INTEGER + DEFAULT_MONTH +
                        ")";


        /**
         * SQL statement to drop "entry" table.
         */
        private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " +
                CaredDbContract.Entry.TABLE_NAME;

        FeedDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }
}
