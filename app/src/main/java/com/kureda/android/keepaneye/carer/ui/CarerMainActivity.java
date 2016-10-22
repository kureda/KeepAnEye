package com.kureda.android.keepaneye.carer.ui;

import android.accounts.Account;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.kureda.android.keepaneye.R;
import com.kureda.android.keepaneye.both.util.KeepAnEyeIdValidator;
import com.kureda.android.keepaneye.both.util.MyJson;
import com.kureda.android.keepaneye.both.util.Phone;
import com.kureda.android.keepaneye.both.util.UserDetails;
import com.kureda.android.keepaneye.carer.db.CaredCursorAdapter;
import com.kureda.android.keepaneye.carer.db.CaredDbContract;
import com.kureda.android.keepaneye.carer.db.CaredDbProvider;
import com.kureda.android.keepaneye.carer.sync.AccountService;
import com.kureda.android.keepaneye.carer.sync.RegisterInMyJsonService;
import com.kureda.android.keepaneye.carer.sync.SyncUtils;
//import com.kureda.android.keepaneye.both.util.Prefs;
//import com.kureda.android.keepaneye.carer.sync.RegisterInMyJsonService;
//
//import static com.kureda.android.keepaneye.both.util.UserDetails.KEEP_AN_EYE_ID;
//import static com.kureda.android.keepaneye.both.util.UserDetails.USER_DETAILS_FILE;


public class CarerMainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String DIALOG_TAG = "dialog";

    private CaredCursorAdapter mAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Object mSyncObserverHandle;
    private Menu mMenu;

    /**
     * Crfate a new anonymous SyncStatusObserver. It's attached to the app's ContentResolver in
     * onResume(), and removed in onPause(). If status changes, it sets the state of the Refresh
     * button. If a sync is active or pending, the Refresh button is replaced by an indeterminate
     * ProgressBar; otherwise, the button itself is displayed.
     */
    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        /** Callback invoked with the sync adapter status changes. */
        @Override
        public void onStatusChanged(int which) {
            runOnUiThread(new Runnable() {
                /**
                 * The SyncAdapter runs on a background thread. To update the UI, onStatusChanged()
                 * runs on the UI thread.
                 */
                @Override
                public void run() {
                    // Create a handle to the account that was created by
                    // SyncService.CreateSyncAccount(). This will be used to query the system to
                    // see how the sync status has changed.
                    Account account = AccountService.GetAccount();
                    if (account == null) {
                        // GetAccount() returned an invalid value. This shouldn't happen, but
                        // we'll set the status to "not refreshing".
                        setRefreshActionButtonState(false);
                        return;
                    }

                    // Test the ContentResolver to see if the sync adapter is active or pending.
                    // Set the state of the refresh button accordingly.
                    boolean syncActive = ContentResolver.isSyncActive(
                            account, CaredDbContract.CONTENT_AUTHORITY);
                    boolean syncPending = ContentResolver.isSyncPending(
                            account, CaredDbContract.CONTENT_AUTHORITY);
                    setRefreshActionButtonState(syncActive || syncPending);
                }
            });
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_carer_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setEmptyListText();
        SyncUtils.CreateSyncAccount(this);
        setCursorAdapter();
        getLoaderManager().initLoader(0, null, this);
        registerInMyJson();
    }

    private void setEmptyListText() {
        TextView emptyListText = (TextView) findViewById(R.id.empty_list_message);
        Spanned coloredText = createKeepAnEyeIdText();
        emptyListText.setText(coloredText);
    }

    private Spanned createKeepAnEyeIdText() {
        String keepAnEyeId = UserDetails.read().getKeepAnEyeId();
        String part1 = getString(R.string.no_cared);
        String part2 = getString(R.string.no_cared2);
        String part3 = getString(R.string.no_cared3);
        String htmlText = part1 + "<br><br>" + part2 + "<br><br><big><b><font color='#AA0000'>"
                + keepAnEyeId + "</font></b></big><br><br>" + part3;
        Spanned coloredText;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            coloredText = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY);
        } else {
            coloredText = Html.fromHtml(htmlText);
        }
        return coloredText;
    }

    private void setCursorAdapter() {
        mAdapter = new CaredCursorAdapter(this, null);//null because no cursor yet
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setEmptyView(findViewById(android.R.id.empty));
        listView.setAdapter(mAdapter);
    }

    private void registerInMyJson() {
        if (!registeredInMyJson())
            startService(new Intent(this, RegisterInMyJsonService.class));
    }

    private boolean registeredInMyJson() {
        String id = MyJson.getKeepAnEyeId();
        return KeepAnEyeIdValidator.isValid(id);
    }


    @Override
    public void onResume() {
        super.onResume();
        mSyncStatusObserver.onStatusChanged(0);
        // Watch for sync state changes
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = "" + CaredDbContract.Entry.COLUMN_NAME_DELETED + " = 0 ";//not hidden
        return new CursorLoader(
                this,
                CaredDbContract.Entry.CONTENT_URI,
                CaredDbProvider.FULL_PROJECTION,
                selection,
                null,
                CaredDbContract.Entry._ID + " desc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    /**
     * Create the ActionBar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_carer_main, menu);
        mMenu = menu;
        return true;
    }

    /**
     * Respond to user gestures on the ActionBar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.carer_main_menu_refresh:
                SyncUtils.TriggerRefresh();
                return true;
            case R.id.carer_main_menu_edit:
                startActivity(new Intent(this, CarerConfigActivity.class));
                return true;
            case R.id.carer_main_menu_show_id:
                showId();
                return true;
            case R.id.carer_main_menu_edit_id:
                openIdChangingCard();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showId() {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setMessage(createKeepAnEyeIdText())
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing, just close
                    }
                }).create().show();
    }

    /**
     * Set the state of the Refresh button. If a sync is active, turn on the ProgressBar widget.
     * Otherwise, turn it off.
     *
     * @param refreshing True if an active sync is occuring, false otherwise
     */
    public void setRefreshActionButtonState(boolean refreshing) {
        if (mMenu == null) {
            return;
        }

        final MenuItem refreshItem = mMenu.findItem(R.id.carer_main_menu_refresh);
        if (refreshItem != null) {
            if (refreshing) {
                refreshItem.setActionView(R.layout.carer_actionbar_progress);
            } else {
                refreshItem.setActionView(null);
            }
        }
    }

    public void openIdChangingCard() {
        DialogFragment dialog = new IdDialogFragment();
        dialog.show(getFragmentManager(), DIALOG_TAG);
    }

    public void makePhoneCall(View view) {
        String phone = (String) view.getTag(R.id.TAG_PHONE_NUMBER);
         Phone.call(phone, this);
    }

}
