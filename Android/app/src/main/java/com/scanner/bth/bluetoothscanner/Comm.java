package com.scanner.bth.bluetoothscanner;

import android.bluetooth.BluetoothDevice;

import com.scanner.bth.db.LogEntry;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;

/**
 * Communicate with a bluetooth device in order to extract data.
 */
public class Comm {
    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    private static LinkedList<Long> generateDates() {
        LinkedList<Long> dates = new LinkedList<>();
        Random rand = new Random();

        for (int i = 0; i < 16; i++) {
            Calendar cal = new GregorianCalendar();
            cal.add(Calendar.DAY_OF_MONTH, randInt(-30, 0));
            cal.add(Calendar.HOUR, randInt(-2, 0));
            cal.add(Calendar.MINUTE, randInt(-60, 0));
            cal.add(Calendar.SECOND, randInt(-60, 0));
            dates.add(cal.getTimeInMillis());

        }

        Collections.sort(dates);

        return dates;
    }

    private static class DeviceData {
        long deviceLastChecked;
        long mouseLastFound;
        long currentMouseDate;
        int batteryLevel;

        public DeviceData(long mouseLastFound, long deviceLastChecked,
            long currentMouseDate, int batterylevel) {
            this.mouseLastFound = mouseLastFound;
            this.currentMouseDate = currentMouseDate;
            this.deviceLastChecked = deviceLastChecked;
            this.batteryLevel = batterylevel;
        }
    }

    static HashMap<String, DeviceData> demoDevices = new HashMap<>();
    static LinkedList<Long> dates = generateDates();

    static {
        demoDevices.put("123456789ABCDEFFEDCBA98765432107", new DeviceData(dates.pop(), dates.pop(), dates.pop(), new Random().nextInt(99) + 1));
        demoDevices.put("123456789ABCDEFFEDCBA98765432110", new DeviceData(dates.pop(), dates.pop(), dates.pop(), new Random().nextInt(99) + 1));
        demoDevices.put("123456789ABCDEFFEDCBA98765432106", new DeviceData(dates.pop(), dates.pop(), dates.pop(), new Random().nextInt(99) + 1));
        demoDevices.put("123456789ABCDEFFEDCBA98765432109", new DeviceData(dates.pop(), dates.pop(), dates.pop(), new Random().nextInt(99) + 1));
    }

    public static void initExchange(LogEntry log, BluetoothDevice device) {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long now = System.currentTimeMillis();
        BeaconParser.BeaconData data = BeaconParser.read(log.getByteRecord());

        if (demoDevices.containsKey(data.getProximity_uuid())) {
            DeviceData demoDevice = demoDevices.get(data.getProximity_uuid());
            log.setLastMouseEvent(demoDevice.mouseLastFound);
            log.setCurrentMouseEventTime(demoDevice.currentMouseDate);
            log.setDeviceLastChecked(demoDevice.deviceLastChecked);
            log.setBatteryLevel(demoDevice.batteryLevel);

        } else {
            DeviceData demoDevice = demoDevices.get("123456789ABCDEFFEDCBA98765432107");
            log.setLastMouseEvent(demoDevice.mouseLastFound);
            log.setCurrentMouseEventTime(demoDevice.currentMouseDate);
            log.setDeviceLastChecked(demoDevice.deviceLastChecked);
            log.setBatteryLevel(demoDevice.batteryLevel);
        }

        log.setLastUpdated(now);
        log.setLastSigner("jrobert@company.com");
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
