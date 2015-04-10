package com.scanner.bth.bluetoothscanner;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.scanner.bth.db.DbHelper;
import com.scanner.bth.db.Location;
import com.scanner.bth.db.LocationDevice;
import com.scanner.bth.update.DbUpdater;
import java.util.List;
import java.util.UUID;


public class FlowPickActivity extends Activity implements FlowPickFragment.OnFragmentInteractionListener,
        LocationFragment.OnFragmentInteractionListener {
    private static final String FLOW_PICK_FRAGMENT = "flow_pick_fragment";
    private static final String LOCATION_LIST_FRAGMENT = "location_list_fragment";


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
        packagedIntent = new Intent(FlowPickActivity.this, ScannerActivity.class);
        LocationFragment locationFragment = LocationFragment.newInstance();
        getFragmentManager().beginTransaction()
                .replace(R.id.activity_flow_pick_container, locationFragment)
                .addToBackStack(LOCATION_LIST_FRAGMENT)
                .commit();

    }

    @Override
    public void onSyncButtonClick() {

    }

    @Override
    public void onOldLogButtonClick() {
        packagedIntent = new Intent(FlowPickActivity.this, LogListActivity.class);
        startActivity(packagedIntent);
    }

    @Override
    public void onLocationPicked(Location location) {
        UUID logUUID = DbHelper.getInstance().createLog("test", location.getLocationId());

        List<LocationDevice> devices = DbHelper.getInstance().getLocalLocationDevices(location);

        for (LocationDevice device: devices) {
            DbHelper.getInstance().createLogEntry(logUUID, "02011a1aff4c000215" + device.getUuid() + "0000" + "0000" + "00");
        }
        packagedIntent.putExtra(ScannerActivity.LOG_ID_EXTRA, logUUID.toString());
        startActivity(packagedIntent);
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
