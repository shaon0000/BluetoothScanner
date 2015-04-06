package com.scanner.bth.bluetoothscanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.scanner.bth.db.LocationDevice;


public class MainActivity extends ActionBarActivity implements
        ItemFragment.OnFragmentInteractionListener, DetailFragment.OnFragmentInteractionListener {

    // Used to update information about said device.
    public static final String LOG_ID_EXTRA = "com.scanner.bth.bluetoothscanner.MainActivity.LOG_ID_EXTRA";

    // Used to grab devices to pre-populate the list
    public static final String INTENT_EXTRA_LOCATION_ID = "com.scanner.bth.bluetoothscanner.MainActivity.location_id";

    private BluetoothAdapter mBluetoothAdapter;
    private BthScanModel mScanModel;
    private ItemFragment mScanResultFragment;

    public static final int REQUEST_ENABLE_BT = 1;

    private static final String TAG_LIST_FRAGMENT = "bth_list_fragment";
    private static final String TAG_DETAIL_FRAGMENT = "bth_detail_framgment";
    private MenuItem mStartScanButton;
    private MenuItem mStopScanButton;
    private long mLocationId;
    private String mLogId;

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public static class BthScanResult {
        BluetoothDevice device;
        ScanRecord record;
        BeaconParser.BeaconData parsedData;

        public BthScanResult(LocationDevice device) {
            this.device = null;
            this.record = null;
            this.parsedData = BeaconParser.read(device.getUuid());
        }
        public BthScanResult(BluetoothDevice device, ScanRecord record) {
            this.device = device;
            this.record = record;

            this.parsedData = BeaconParser.read(record.getBytes());

        }

        public BluetoothDevice getDevice() {
            return device;
        }

        public BeaconParser.BeaconData getBeaconData() {
            return parsedData;
        }
        public ScanRecord getRecord() {
            return record;
        }

        @Override
        public int hashCode() {
            return parsedData.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            return parsedData.equals(((BthScanResult) other).getBeaconData());
        }

        public void update(BthScanResult result) {
            // This isn't doing much now, but we might have to use this later on.
            // TODO (shaon): actually revisit whether this was useful.
            device = result.device;
            record = result.record;
            parsedData = result.parsedData;
        }
    }
    public BthScanModel.BthScanView mScanListener = new BthScanModel.BthScanView() {

        @Override
        public void onLeScan(ScanResult result) {
            Log.d("SCAN", "Found an item");
            BthScanResult scanResult = new BthScanResult(result.getDevice(), result.getScanRecord());
            mScanResultFragment.addDevice(scanResult);
        }

        @Override
        public void onScanStart() {
            Log.d("SCAN", "Starting to scan");
            // update the list everytime we scan.
        }

        @Override
        public void onScanFinish() {
            Log.d("SCAN", "Finished a scan");
            // if we have any animations, we should stop them.
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        mScanResultFragment = (ItemFragment) fm.findFragmentByTag(TAG_LIST_FRAGMENT);

        if (mScanResultFragment == null) {
            mScanResultFragment = new ItemFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mScanResultFragment, TAG_LIST_FRAGMENT)
                    .commit();
        }



        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        mScanModel = new BthScanModel(mBluetoothAdapter);
        mScanModel.attachView(mScanListener);


        mLocationId = getIntent().getExtras().getLong(MainActivity.INTENT_EXTRA_LOCATION_ID);
        mLogId = getIntent().getExtras().getString(MainActivity.LOG_ID_EXTRA);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScanModel.deattachView(mScanListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mStartScanButton = menu.findItem(R.id.action_search);
        mStopScanButton = menu.findItem(R.id.action_stop_search);
        mStopScanButton.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_settings: {
                return true;
            }

            case R.id.action_search: {
                Log.d("ACTION SCAN", "scan clicked in action bar");
                mScanModel.scanLeDevice(true);
                mStartScanButton.setVisible(false);
                mStopScanButton.setVisible(true);
                setSupportProgressBarIndeterminateVisibility(true);
                return true;
            }

            case R.id.action_stop_search: {
                Log.d("ACTION SCAN", "stop clicked in action bar");
                mScanModel.scanLeDevice(false);
                mStartScanButton.setVisible(true);
                mStopScanButton.setVisible(false);
                setSupportProgressBarIndeterminateVisibility(false);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickDevice(BthScanResult result) {
        Log.d(MainActivity.class.getSimpleName(), "value: " + result.getDevice().getAddress());
        DetailFragment detailFragment = DetailFragment.newInstance(
                result.getBeaconData().getBeacon_prefix(),
                result.getBeaconData().getProximity_uuid(),
                result.getBeaconData().getMajor(),
                result.getBeaconData().getMinor(),
                result.getBeaconData().getTx(),
                result.getRecord().getBytes());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, detailFragment)
                        // Add this transaction to the back stack
                .addToBackStack(TAG_DETAIL_FRAGMENT)
                .commit();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {



        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.bth_devices_fragment, container, false);

            return rootView;

        }
    }
}
