/**
 * Runs synchronisation. The bigger half of this class and of all sync machinery (SyncUtils,
 * GenericAccountingService, SyncService etc) is taken from the developer.android.com tutorial, with
 * proper modifitactions according to my project requirements. So, all credit is their, all mistakes
 * are mine. :)
 */

package com.kureda.android.keepaneye.carer.sync;

import android.accounts.Account;
import android.appwidget.AppWidgetManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.kureda.android.keepaneye.both.util.MyJson;
import com.kureda.android.keepaneye.carer.db.CaredDbContract;
import com.kureda.android.keepaneye.carer.db.CaredDbProvider;
import com.kureda.android.keepaneye.carer.util.Cared;
import com.kureda.android.keepaneye.carer.widget.WidgetProvider;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sergei Kureda
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "sync.SyncAdapter";
    // Constants representing column positions from UPDATE_PROJECTION.
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_ENTRY_ID = 1;

    private final ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }


    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    /**
     * Major method. Downloads updated from myjson.com, compares them with database values
     * and if necessary, updates/inserts. (Database records are never deleted. They can be
     * marked as invisible instead).
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

         try {
            downloadUpdates(syncResult);
            updateWidget();
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(TAG, "Error updating database: " + e.toString());
            syncResult.databaseError = true;
            return;
        }
    }

    /**
     * Read json and synchronize it with local database though content provider.
     */
    private void downloadUpdates(final SyncResult syncResult) throws RemoteException,
            OperationApplicationException {
        final ContentResolver contentResolver = getContext().getContentResolver();
        String keepAnEyeId = MyJson.getKeepAnEyeId();
        JSONArray cared = MyJson.downloadCared(keepAnEyeId);
        if (cared == null) {
            return;
        }
        Log.i(TAG, "Parsing json");
        final List<Entry> entries = JsonParser.parse(cared);
        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        // Build hash table of incoming entries
        HashMap<String, Entry> entryMap = new HashMap<>();
        for (Entry e : entries) {
            entryMap.put(e.id, e);
        }

        // Get list of all items
        Log.i(TAG, "Fetching local entries for merge");
        Uri uri = CaredDbContract.Entry.CONTENT_URI; // Get all entries
        Cursor cursor = contentResolver.query(uri, CaredDbProvider.UPDATE_PROJECTION, null, null,
                null);
        assert cursor != null;

        // Find stale data
        String entryId, name;
        int id, report, log, walk, ride;
        while (cursor.moveToNext()) {
            syncResult.stats.numEntries++;
            id = cursor.getInt(COLUMN_ID);  // _ID, database primary index
            entryId = cursor.getString(COLUMN_ENTRY_ID); // id of cared's smartphone, from json file
            Entry match = entryMap.get(entryId);
            if (match != null) {
                name = match.name;
                report = match.report;
                log = match.login;
                walk = match.walk;
                ride = match.ride;
                entryMap.remove(entryId); //Entry exists. So don't need to insert it
                 // Put entry to the "to be updated" list
                Uri existingUri = CaredDbContract.Entry.CONTENT_URI.buildUpon()
                        .appendPath(Integer.toString(id)).build();

                // Update existing record
                Log.i(TAG, "Scheduling update: " + existingUri);
                batch.add(ContentProviderOperation.newUpdate(existingUri)
                        .withValue(CaredDbContract.Entry.COLUMN_NAME_NAME, name)
                        .withValue(CaredDbContract.Entry.COLUMN_NAME_REPORT, report)
                        .withValue(CaredDbContract.Entry.COLUMN_NAME_LOG, log)
                        .withValue(CaredDbContract.Entry.COLUMN_NAME_WALK, walk)
                        .withValue(CaredDbContract.Entry.COLUMN_NAME_RIDE, ride)
                        .build());
                syncResult.stats.numUpdates++;
            }
        }
        cursor.close();

        // Add new items
        for (Entry e : entryMap.values()) {
            Log.i(TAG, "Scheduling insert: entry_id=" + e.id);
            batch.add(ContentProviderOperation.newInsert(CaredDbContract.Entry.CONTENT_URI)
                    .withValue(CaredDbContract.Entry.COLUMN_NAME_ENTRY_ID, e.id)
                    .withValue(CaredDbContract.Entry.COLUMN_NAME_NAME, e.name)
                    .withValue(CaredDbContract.Entry.COLUMN_NAME_REPORT, e.report)
                    .withValue(CaredDbContract.Entry.COLUMN_NAME_LOG, e.login)
                    .withValue(CaredDbContract.Entry.COLUMN_NAME_WALK, e.walk)
                    .withValue(CaredDbContract.Entry.COLUMN_NAME_RIDE, e.ride)
                    .build());
            syncResult.stats.numInserts++;
        }
        Log.i(TAG, "Merge solution ready. Applying batch update");
        mContentResolver.applyBatch(CaredDbContract.CONTENT_AUTHORITY, batch);
        mContentResolver.notifyChange(
                CaredDbContract.Entry.CONTENT_URI, null, false);
    }

    private void updateWidget() {
        Cursor cursor;
        Context context = getContext().getApplicationContext();
        int widgetColorCode = 0;
        try {
             final ContentResolver contentResolver = context.getContentResolver();
            Uri uri = CaredDbContract.Entry.CONTENT_URI; // Get all entries
            cursor = contentResolver.query(uri, CaredDbProvider.FULL_PROJECTION, null, null, null);
            while (cursor.moveToNext()) {
                Cared cared = new Cared(null);
                cared.readFromCursor(cursor);
                int colorCode = cared.calculateColorCodes();
                widgetColorCode = Math.max(widgetColorCode, colorCode);
             }
        } catch (Exception ex) {
            Log.e(TAG, "execption: ", ex);
        }
        WidgetProvider.setWidgetColor(Cared.getColor(widgetColorCode, true));

        //It's weird way to pass info to a widget: by static variable and direct intitialisation
        //But it's simple and it works :)
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName
                (context, WidgetProvider.class));
        WidgetProvider widgetProvider = new WidgetProvider();
        widgetProvider.onUpdate(context, AppWidgetManager.getInstance(context), ids);
    }

}
