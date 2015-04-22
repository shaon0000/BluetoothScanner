package com.scanner.bth.bluetoothscanner;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;
import android.os.AsyncTask;
import android.util.Log;

import com.scanner.bth.db.DbHelper;
import com.scanner.bth.db.LocationDevice;
import com.scanner.bth.db.LogEntry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;


/**
 * Created by shaon0000 on 2015-04-10.
 *
 * Used to manage the addition of log entries for a given log. The model automatically
 * syncs and updates the database as the state of each log entry changes over time. It also
 * notifies views when the whole data set changes so that it may update itself.
 */
public class BthScanResultsModel {



    private LinkedList<BthScanResultsView> views = new LinkedList<>();

    ArrayList<ScanResult> bthList = new ArrayList<>();
    HashSet<ScanResult> bthSet = new HashSet<>();

    public void attachView(BthScanResultsView view) {
        views.add(view);
    }

    public void detachView(BthScanResultsView view) {
        views.remove(view);
    }

    public ScanResult getScanResult(Integer logEntryId) {
        for (BthScanResultsModel.ScanResult result : bthList) {
            if (result.getlogEntry().getId() == logEntryId) {
                return result;
            }
        }

        throw new RuntimeException("No results had the longEntry");
    }

    public boolean isListAllChecked() {
        for (ScanResult result : bthList) {
            if (result.getlogEntry().getCurrentDeviceCheckTime() == 0 && result.getlogEntry().getShouldIgnore() == false) {
                return false;
            }
        }

        return true;
    }

    public void completeLog() {
        bthList.clear();
        bthSet.clear();
        updateViews();
    }

    public static interface BthScanResultsView {
        public void updateView();
        public void updateSingleItem(ScanResult result);
    }

    public static abstract class BaseCommTask<A, B, C> extends AsyncTask<A, B, C> {
        private final BthScanResultsModel.ScanResult scannedObject;
        private final BthScanResultsModel model;
        public BaseCommTask(BthScanResultsModel.ScanResult scannedObject, BthScanResultsModel model) {
            this.scannedObject = scannedObject;
            this.model = model;
        }

        @Override
        public void onPreExecute() {
            scannedObject.setCommunicating(true);
            model.updateViews(scannedObject);
        }


        @Override
        protected void onPostExecute(C result) {
            scannedObject.setCommunicating(false);
            model.updateViews(scannedObject);
        }
    }

    public class CommTask extends BaseCommTask<Void, Void, Void> {
        private final BthScanResultsModel.ScanResult scannedObject;

        public CommTask(BthScanResultsModel.ScanResult scannedObject) {
            super(scannedObject, BthScanResultsModel.this);
            this.scannedObject = scannedObject;
        }



        @Override
        protected Void doInBackground(Void... params) {
            Comm.initExchange(scannedObject.getlogEntry(), scannedObject.getDevice());
            DbHelper.getInstance().updateLogEntry(scannedObject.getlogEntry());
            return null;
        }

    }
    public static class ScanResult {

        public static final int SEARCHING = 1;
        public static final int COMM = 2;
        public static final int NO_MOUSE = 4;
        public static final int MOUSE_FOUND = 8;

        LocationDevice locationDevice;
        BluetoothDevice device;
        ScanRecord record;
        BeaconParser.BeaconData parsedData;

        public int getStatus() {
            if (device == null && logEntry.getCurrentDeviceCheckTime() == 0) {
                return SEARCHING;
            }

            if (isCommunicating()) {
                return COMM;
            }

            if (Integer.valueOf(parsedData.getMinor()) == 0) {
                return NO_MOUSE;
            } else {
                return MOUSE_FOUND;
            }
        }


        public boolean isCommunicating() {
            return communicating;
        }

        public void setCommunicating(boolean communicating) {
            this.communicating = communicating;
        }

        boolean communicating = false;
        public LocationDevice getLocationDevice() {
            return locationDevice;
        }

        LogEntry logEntry;

        public ScanResult(LogEntry entry, LocationDevice locationDevice) {
            this.device = null;
            this.record = null;
            String fullRecord = entry.getByteRecord();
            this.parsedData = BeaconParser.read(fullRecord);
            this.locationDevice = locationDevice;
            logEntry = entry;
            Log.d("blah", "tag");
        }

        public ScanResult(BluetoothDevice device, ScanRecord record) {
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
            return parsedData.equals(((ScanResult) other).getBeaconData());
        }

        public void update(ScanResult result) {
            device = result.device;
            record = result.record;
            parsedData = result.parsedData;

            long now = System.currentTimeMillis();
            logEntry.setByteRecord(BeaconParser.bytesToHex(result.getRecord().getBytes()));

            logEntry.setLastUpdated(now);

        }

        public LogEntry getlogEntry() {
            return logEntry;
        }

        public void setlogEntry(LogEntry logEntry) {
            this.logEntry = logEntry;
        }
    }

    private void updateViews() {
        for(BthScanResultsView view: views) {
            view.updateView();
        }
    }

    public void updateViews(ScanResult result) {
        for(BthScanResultsView view: views) {
            view.updateSingleItem(result);
        }
    }

    public void prePopulate(LogEntry entry, LocationDevice device) {
        BthScanResultsModel.ScanResult result = new BthScanResultsModel.ScanResult(entry, device);
        if (bthSet.contains(result)) {
            return;
        }
        bthList.add(result);
        bthSet.add(result);
        updateViews();
    }

    public boolean addDevice(BthScanResultsModel.ScanResult result) {
        if (!bthSet.contains(result)) {
            Log.d(BthScanResultsModel.class.getSimpleName(), "ignoring device not on list" + result.toString());
            return false;
        } else {

            BthScanResultsModel.ScanResult priorResult = bthList.get(bthList.lastIndexOf(result));
            priorResult.update(result);
            DbHelper.getInstance().updateLogEntry(priorResult.getlogEntry());
            Log.d(BthScanResultsModel.class.getSimpleName(), "used data to update:" + result.toString());
            Log.d(BthScanResultsModel.class.getSimpleName(), "communicating with: " + priorResult.toString());

            priorResult.setCommunicating(true);
            new CommTask(priorResult).execute();
            updateViews(result);
            return true;
        }

    }

    public ScanResult reloadFromDb(Integer logEntryId) {
        for (BthScanResultsModel.ScanResult result : bthList) {
            if (result.getlogEntry().getId() == logEntryId) {
                result.setlogEntry(DbHelper.getInstance().getLogEntry(logEntryId));
                updateViews(result);
                return result;
            }
        }
        throw new RuntimeException("log with id: " + logEntryId + " was not found");
    }

    public int getCount() {

        return bthList.size();
    }

    public ScanResult getItem(int position) {

        return bthList.get(position);
    }

    public long getItemId(int position) {

        return position;
    }

    public int getPosition(ScanResult result) {
        int i = 0;
        for(BthScanResultsModel.ScanResult obj : bthList) {
            if (result.equals(obj)) {
                return i;
            }
            i++;
        }

        throw new RuntimeException("result: " + result.toString() + " was not found in the model");
    }

}
