package com.scanner.bth.bluetoothscanner;

import android.os.Handler;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 * Emulates a bluetooth scanner.
 * Methods to copy:
 * starScan(Listener)
 * stopScan(Listener)
 *
 *
 * Created by shaon on 5/7/2015.
 */
public class VirtualBthScanner {

    static LinkedList<String> scanList = new LinkedList<>();
    static {
        scanList.add("02011a1aff4c000215" + "123456789ABCDEFFEDCBA98765432107" + "0001" + "0001" + "00");
        scanList.add("02011a1aff4c000215" + "123456789ABCDEFFEDCBA98765432110" + "0000" + "0000" + "00");
        scanList.add("02011a1aff4c000215" + "123456789ABCDEFFEDCBA98765432106" + "0000" + "0000" + "00");
        scanList.add("02011a1aff4c000215" + "123456789ABCDEFFEDCBA98765432109" + "0000" + "0000" + "00");
    }

    public static interface VirtualScanCallback {
        public void onScanResult(byte[] record);
    }

    boolean isScanning = false;
    Semaphore scannerLock = new Semaphore(1);
    Handler scanHandler = new Handler();

    public static class DemoBthDevice {

        String record;

        public DemoBthDevice(String record) {
            this.record = record;
        }

        public String getRecord() {
            return record;
        }
    }

    public void startScan(final VirtualScanCallback callback) {
        try {
            scannerLock.acquire();
            if (isScanning) {
                scannerLock.release();
                return;
            }
            isScanning = true;

            scanHandler.post(new ScanThread(callback));
            scannerLock.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopScan(final VirtualScanCallback callback) {
        try {
            scannerLock.acquire();
            if (!isScanning) {
                scannerLock.release();
                return;
            }

            isScanning = false;
            scannerLock.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class ScanThread implements Runnable {

        private static final long SCAN_INTERVAL_TIME = 50L;
        private final VirtualScanCallback callback;

        public ScanThread(VirtualScanCallback callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
                try {
                    scannerLock.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!isScanning) {
                    scannerLock.release();
                    return;
                }


                callback.onScanResult(BeaconParser.hexStringToByteArray(scanList.getFirst()));
                scanList.add(scanList.removeFirst());
                scanHandler.postDelayed(new ScanThread(callback), SCAN_INTERVAL_TIME);
                scannerLock.release();
        }
    }

}
