package com.scanner.bth.bluetoothscanner;

import android.accounts.Account;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.scanner.bth.auth.AuthHelper;
import com.scanner.bth.db.DbHelper;
import com.scanner.bth.db.Location;
import com.scanner.bth.db.LocationDevice;
import com.scanner.bth.update.DbUpdater;

import java.util.List;
import java.util.UUID;


public class FlowPickActivity extends Activity implements FlowPickFragment.OnFragmentInteractionListener,
        LocationFragment.OnFragmentInteractionListener, AboutFragment.OnFragmentInteractionListener {
    private static final String FLOW_PICK_FRAGMENT = "flow_pick_fragment";
    private static final String LOCATION_LIST_FRAGMENT = "location_list_fragment";

    // Constants

    // The account name
    public static final String ACCOUNT = "shaon";
    public static final int AUTH_LOGIN = 1;
    private static final String SMOKE_SCREEN_FRAGMENT = "smoke_screen_fragment";
    private static final int OLD_LOG_REQUEST_CODE = 2;
    private static final int NEW_LOG_REQUEST_CODE = 3;
    private static final String ABOUT_FRAGMENT = "about_fragment";

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
    public void onDownloadButtonClick() {
        new DownloadLocationAndDevicesTask().execute();
    }

    @Override
    public void onNewLogButtonClick() {
        intentSelect = IntentSelect.NEW_LOG;

        List<Location> locations = DbHelper.getInstance().getAllLocations();
        if (locations.size() == 1) {
            onLocationPicked(locations.get(0));
        } else {
            LocationFragment locationFragment = LocationFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.activity_flow_pick_container, locationFragment)
                    .addToBackStack(LOCATION_LIST_FRAGMENT)
                    .commit();
        }

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
        List<Location> locations = DbHelper.getInstance().getAllLocations();
        if (locations.size() == 1) {
            onLocationPicked(locations.get(0));
        } else {
            LocationFragment locationFragment = LocationFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.activity_flow_pick_container, locationFragment)
                    .addToBackStack(LOCATION_LIST_FRAGMENT)
                    .commit();
        }
    }

    @Override
    public void onAboutButtonClick() {
        AboutFragment aboutFragment = AboutFragment.newInstance();
        getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.activity_flow_pick_container, aboutFragment)
                .addToBackStack(ABOUT_FRAGMENT)
                .commit();
    }

    @Override
    public void onLocationPicked(Location location) {
        switch(intentSelect) {
            case OLD_LOG:
            {
                packagedIntent = new Intent(FlowPickActivity.this, LogListActivity.class);
                packagedIntent.putExtra(LogListActivity.LOCATION_ID, location.getLocationId());
                startActivityForResult(packagedIntent, OLD_LOG_REQUEST_CODE);
                break;
            }
            case NEW_LOG:
            {
                packagedIntent = new Intent(FlowPickActivity.this, ScannerActivity.class);
                UUID logUUID = DbHelper.getInstance().createLog(AuthHelper.getUsername(this), location.getLocationId());

                List<LocationDevice> devices = DbHelper.getInstance().getLocalLocationDevices(location);

                for (LocationDevice device: devices) {
                    DbHelper.getInstance().createLogEntry(logUUID, "02011a1aff4c000215" + device.getUuid() + "0000" + "0000" + "00");
                }

                packagedIntent.putExtra(ScannerActivity.LOG_ID_EXTRA, logUUID.toString());
                startActivityForResult(packagedIntent, NEW_LOG_REQUEST_CODE);
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
            DbUpdater.updateExternalDirectoryWithImages(FlowPickActivity.this);
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
