package com.scanner.bth.bluetoothscanner;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.scanner.bth.auth.AuthHelper;
import com.scanner.bth.auth.AuthLoginActivity;
import com.scanner.bth.db.AccountDetails;
import com.scanner.bth.db.DbHelper;
import com.scanner.bth.db.Location;
import com.scanner.bth.db.LocationDevice;
import com.scanner.bth.update.DbUpdater;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


public class FlowPickActivity extends Activity implements FlowPickFragment.OnFragmentInteractionListener,
        LocationFragment.OnFragmentInteractionListener, SmokeScreenFragment.OnFragmentInteractionListener {
    private static final String FLOW_PICK_FRAGMENT = "flow_pick_fragment";
    private static final String LOCATION_LIST_FRAGMENT = "location_list_fragment";

    // Constants

    // The account name
    public static final String ACCOUNT = "shaon";
    public static final int AUTH_LOGIN = 1;
    private static final String SMOKE_SCREEN_FRAGMENT = "smoke_screen_fragment";
    // Instance fields
    Account mAccount;

    private static enum IntentSelect {NONE, NEW_LOG, OLD_LOG, SMOKE_SCREEN};

    private IntentSelect intentSelect = IntentSelect.NONE;


    FlowPickFragment mFlowPickFragment;
    Intent packagedIntent;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_pick);

        FragmentManager fm = getFragmentManager();
        mFlowPickFragment = (FlowPickFragment) fm.findFragmentByTag(FLOW_PICK_FRAGMENT);

        if (mFlowPickFragment == null) {
            mFlowPickFragment = new FlowPickFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.activity_flow_pick_container, mFlowPickFragment, FLOW_PICK_FRAGMENT)
                    .commit();
        }


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void finishedFakeScreen() {
        Log.d(FlowPickActivity.class.getSimpleName(), "finished fake screen");
        intentSelect = IntentSelect.NEW_LOG;
        LocationFragment locationFragment = LocationFragment.newInstance();
        getFragmentManager().beginTransaction()
                .replace(R.id.activity_flow_pick_container, locationFragment)
                .addToBackStack(LOCATION_LIST_FRAGMENT)
                .commit();
    }

    @Override
    public void onDownloadButtonClick() {
        new DownloadLocationAndDevicesTask().execute();
    }

    @Override
    public void onNewLogButtonClick() {
        intentSelect = IntentSelect.SMOKE_SCREEN;
        SmokeScreenFragment smokeFragment = SmokeScreenFragment.newInstance();
        getFragmentManager().beginTransaction()
                .replace(R.id.activity_flow_pick_container, smokeFragment)
                .addToBackStack(SMOKE_SCREEN_FRAGMENT)
                .commit();

    }

    @Override
    public void onSyncButtonClick() {
        Log.d("TAG", "Sync button clicked");
        Toast.makeText(this, "initiating sync...", Toast.LENGTH_SHORT).show();
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(mAccount, FlowPickFragment.AUTHORITY, settingsBundle);
    }

    @Override
    public void onOldLogButtonClick() {
        intentSelect = IntentSelect.OLD_LOG;

        LocationFragment locationFragment = LocationFragment.newInstance();
        getFragmentManager().beginTransaction()
                .replace(R.id.activity_flow_pick_container, locationFragment)
                .addToBackStack(LOCATION_LIST_FRAGMENT)
                .commit();
    }

    @Override
    public void onLocationPicked(Location location) {
        switch(intentSelect) {
            case OLD_LOG:
            {
                packagedIntent = new Intent(FlowPickActivity.this, LogListActivity.class);
                packagedIntent.putExtra(LogListActivity.LOCATION_ID, location.getLocationId());
                startActivity(packagedIntent);
                break;
            }
            case NEW_LOG:
            {
                packagedIntent = new Intent(FlowPickActivity.this, ScannerActivity.class);
                UUID logUUID = DbHelper.getInstance().createLog("test", location.getLocationId());

                List<LocationDevice> devices = DbHelper.getInstance().getLocalLocationDevices(location);

                for (LocationDevice device: devices) {
                    DbHelper.getInstance().createLogEntry(logUUID, "02011a1aff4c000215" + device.getUuid() + "0000" + "0000" + "00");
                }

                packagedIntent.putExtra(ScannerActivity.LOG_ID_EXTRA, logUUID.toString());
                startActivity(packagedIntent);
                break;
            }
            default: {
                break;
            }
        }

    }

    private class DownloadLocationAndDevicesTask extends AsyncTask<Void, String, Void>

    {

        @Override
        protected void onProgressUpdate(String... message) {
            Toast.makeText(FlowPickActivity.this, message[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            mFlowPickFragment.lockUi();
        }
        @Override
        protected Void doInBackground(Void... params) {
            DbUpdater.updateDatabaseWithLocations(FlowPickActivity.this);
            List<Location> locations = DbHelper.getInstance().getAllLocations();
            for(Location location: locations) {
                DbUpdater.updateDatabaseWithDevices(FlowPickActivity.this, location);
                publishProgress("Updating Devices for " + location.getAddress());
            }
            publishProgress("Done");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mFlowPickFragment.unlockUi();
        }
    }
}
