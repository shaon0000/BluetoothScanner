package com.scanner.bth.bluetoothscanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.os.Handler;
import android.bluetooth.le.ScanCallback;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Basic model that scans/targets/identifies devices.
 *
 * We should be able to specify a list of devices to look for when we scan. In Addition,
 * we should able to retrieve when was the last scan, are we currently scanning, and the results
 * of the last scan.
 *
 * Created by shaon on 2/21/2015.
 */
public class BthScanModel {

    public static interface BthScanView {
        public void onLeScan(ScanResult result);
    }
    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            for (BthScanView view : mViews) {
                view.onLeScan(result);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (ScanResult result : results) {
                for (BthScanView view : mViews) {
                    view.onLeScan(result);
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    }

    private final BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning = false;
    private Handler mHandler = new Handler();

    // A list of targets to scan for. Once we find all these, we end the scan if we find
    // them before the threshold timer.
    HashSet<String> targetList = new HashSet<String>();
    LinkedList<BthScanView> mViews = new LinkedList<BthScanView>();

    private static final long SCAN_PERIOD = 10000;

    public BthScanModel(BluetoothAdapter adapter) {
        mBluetoothAdapter = adapter;
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.getBluetoothLeScanner().stopScan();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.getBluetoothLeScanner().startScan(mLeScanCallback);
        }
    }
}
