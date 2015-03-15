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


public class MainActivity extends ActionBarActivity implements
        ItemFragment.OnFragmentInteractionListener, DetailFragment.OnFragmentInteractionListener {

    private BluetoothAdapter mBluetoothAdapter;
    private BthScanModel mScanModel;
    private ItemFragment mScanResultFragment;

    public static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG_LIST_FRAGMENT = "bth_list_fragment";

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public static class BthScanResult {
        BluetoothDevice device;
        ScanRecord record;
        BeaconParser.BeaconData parsedData;

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
            // clear the list of items. start animation.
            mScanResultFragment.clear();
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
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickDevice(BthScanResult result) {
        result.getDevice();
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
