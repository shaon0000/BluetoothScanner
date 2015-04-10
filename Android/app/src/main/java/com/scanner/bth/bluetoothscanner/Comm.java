package com.scanner.bth.bluetoothscanner;

import android.bluetooth.BluetoothDevice;

import com.scanner.bth.db.LogEntry;

/**
 * Communicate with a bluetooth device in order to extract data.
 */
public class Comm {


    public static void initExchange(LogEntry log, BluetoothDevice device) {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long now = System.currentTimeMillis();
        log.setLastUpdated(now);
        log.setLastMouseEvent(0L);
        log.setLastSigner("jrobert@company.com");
        log.setDeviceLastChecked(0L);
        log.setCurrentDeviceCheckTime(now);
    }

    public static void sign(LogEntry log, BluetoothDevice device, String owner) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long now = System.currentTimeMillis();
        log.setLastUpdated(now);
        log.setCurrentDeviceCheckTime(now);
        log.setCurrentSigner(owner);
    }
}
