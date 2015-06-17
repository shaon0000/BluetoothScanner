package com.scanner.bth.report;

import com.scanner.bth.bluetoothscanner.BeaconParser;
import com.scanner.bth.db.DbHelper;
import com.scanner.bth.db.LocationDevice;
import com.scanner.bth.db.BthLog;
import com.scanner.bth.db.LogEntry;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by shaon0000 on 2015-04-11.
 */
public class LogReport {
    private final UUID mLogId;
    private boolean locked = false;
    private BthLog mLog;

    String mReportName;

    long mTimeCreated;
    long mTimeFinished;

    String mOwner;

    int mDeviceCount;
    int mMouseFound;
    int mNoMouse;
    int mDevicesNotFound;

    TreeMap<String, String> mDeviceToCommentMap = new TreeMap<String, String>();
    private String mFinalReport;

    /** Report content:

    Meta -
    Log created: <time_created>
    Log finished: <last_updated>
    by: <owner>

    Summary -
    Total device count: N
    Mouse found: x
    No mouse: y
    devices not found or broken: z

    Report Comments -
    <Device Name> - <Comment>
    **/
    public LogReport(UUID logId) {
        mLogId = logId;
    }

    public void loadReport() {
        if (locked) {
            throw new RuntimeException("Tried to load a report that was already constructed");
        }

        mLog = DbHelper.getInstance().getLog(mLogId);
    }

    public void constructReport() {
        if (locked) {
            throw new RuntimeException("Tried to construct a report twice");
        }

        if (mLog == null) {
            throw new RuntimeException("Did not load the report yet. Call loadReport() first");
        }

        mReportName = DbHelper.getInstance().getLocation(mLog.getLocationId()).getAddress();
        mTimeCreated = mLog.getTimeCreated();
        mTimeFinished = mLog.getLastUpdated();

        DateFormat format = DateFormat.getDateInstance();
        format.setTimeZone(TimeZone.getDefault());

        mReportName += " (" + format.format(new Date(mTimeFinished)) + ")";

        mOwner = mLog.getOwner();

        List<LogEntry> entries = DbHelper.getInstance().getLogEntries(mLog, null, false);
        mDeviceCount = entries.size();
        for (LogEntry entry : entries ) {
            BeaconParser.BeaconData data = BeaconParser.read(entry.getByteRecord());
            if (entry.getCurrentDeviceCheckTime() == 0) {
                mDevicesNotFound++;
            } else if (data.getMinor().equals("0")) {
                mNoMouse++;
            } else {
                mMouseFound++;
            }

            if (entry.getMessage() != null && entry.getMessage().length() != 0) {
                LocationDevice device = DbHelper.getInstance().getLocationDevice(mLog.getLocationId(), data.getProximity_uuid());
                mDeviceToCommentMap.put(device.getName(), entry.getMessage());
            }
        }

        String template = "{0}\n" +
                "\n" +
                "by {1} " +
                "created on {2, date} {2, time}\n" +
                "submitted by {3, date} {3, time}\n" +
                "\n" +
                "- SUMMARY -\n" +
                "\n" +
                "Total device count: {4, number, integer}\n" +
                "Mouse found: {5, number, integer}\n" +
                "No Mouse found: {6, number, integer}\n" +
                "Devices not found or broken {7, number, integer}\n" +
                "\n" +
                "- DEVICE REPORTS -\n" +
                "\n";

        for (TreeMap.Entry<String, String> entry : mDeviceToCommentMap.entrySet())
        {
            template += entry.getKey() + " - " + entry.getValue() + "\n";
        }

        mFinalReport = MessageFormat.format(template, mReportName, mOwner, mTimeCreated, mTimeFinished, mDeviceCount,
                mMouseFound, mNoMouse, mDevicesNotFound);


        locked = true;
    }

    public String getReport() {
        if (!locked) {
            throw new RuntimeException("attempted to retrieve report without constructing it first");
        }

        return mFinalReport;
    }
}
