package com.scanner.bth.bluetoothscanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.bluetooth.le.ScanCallback;
import android.util.Log;

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
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BthScanModel {

    public static final boolean SPOOF_DEVICES = false;

    // View interface to know the current progress, result, or failure of a scan.
    public static interface BthScanView {
        public void onLeScan(ScanResult result);
        public void onScanStart();
        public void onScanFinish();
        public void onVirtualScan(byte[] record);
    }

    private VirtualBthScanner.VirtualScanCallback mVirtualCallback = new VirtualBthScanner.VirtualScanCallback() {
        @Override
        public void onScanResult(byte[] record) {
            String sRecord = BeaconParser.bytesToHex(record);
            if (targetList.contains(sRecord)) {
                return;
            }

            targetList.add(sRecord);
            for (BthScanView view : mViews) {
                view.onVirtualScan(record);
            }

        }
    };

    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (targetList.contains(result.getDevice().getAddress())) {
                return;
            }

            targetList.add(result.getDevice().getAddress());
            for (BthScanView view : mViews) {
                view.onLeScan(result);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (ScanResult result : results) {
                if (targetList.contains(result.getDevice().getAddress())) {
                    return;
                }

                targetList.add(result.getDevice().getAddress());

                for (BthScanView view : mViews) {
                    view.onLeScan(result);
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    private final BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning = false;
    private VirtualBthScanner spoofScanner = new VirtualBthScanner();

    // A list of targets to ignore. This is to prevent duplicates during a scan. We hit each
    // device only once.
    HashSet<String> targetList = new HashSet<String>();
    LinkedList<BthScanView> mViews = new LinkedList<BthScanView>();

    private static final long SCAN_PERIOD = 10000;

    public BthScanModel(BluetoothAdapter adapter) {
        mBluetoothAdapter = adapter;
    }

    public void scanLeDevice(final boolean enable) {
        targetList.clear();
        if (enable) {
            notifyScanStart();
            mScanning = true;
            if (SPOOF_DEVICES) {
                Log.d(BthScanModel.class.getSimpleName(), "spoofing devices");
                spoofScanner.startScan(mVirtualCallback);
            } else {
                mBluetoothAdapter.getBluetoothLeScanner().startScan(mLeScanCallback);
            }
        } else {
            notifyScanFinish();
            mScanning = false;
            if (SPOOF_DEVICES) {
                spoofScanner.stopScan(mVirtualCallback);
            } else {
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(mLeScanCallback);
            }
        }
    }

    public void attachView(BthScanView view) {
        mViews.add(view);
    }

    public void deattachView(BthScanView view) {
        mViews.remove(view);
    }

    public boolean isScanning() {
        return mScanning;
    }

    private void notifyScanStart() {
        for (BthScanView view : mViews) {
            view.onScanStart();
        }
    }

    private void notifyScanFinish() {
        for (BthScanView view : mViews) {
            view.onScanFinish();
        }
    }


}
