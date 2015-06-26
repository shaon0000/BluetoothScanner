package com.scanner.bth.update;

import android.accounts.Account;
import android.app.NotificationManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.scanner.bth.auth.AuthHelper;
import com.scanner.bth.bluetoothscanner.R;
import com.scanner.bth.db.DbHelper;
import com.scanner.bth.db.BthLog;
import com.scanner.bth.db.Location;
import com.scanner.bth.db.LogEntry;
import com.scanner.bth.db.LogTable;
import com.scanner.bth.http.Api;
import com.scanner.bth.http.LogMail;
import com.scanner.bth.report.LogReport;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

/**
 * Created by shaon on 4/23/2015.
 */
public class LogSyncAdapter extends AbstractThreadedSyncAdapter {

    Context mContext;
    NotificationManager mNotificationManager;

    private static long LOG_LIVE_TIME = 604800000;

    public LogSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
    }

    public LogSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        String syncEmail = AuthHelper.getSyncEmail(getContext());

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setContentTitle("Syncing logs");
        mBuilder.setSmallIcon(R.drawable.uploading);
        mBuilder.setProgress(0, 0, true);
        List<BthLog> logs = DbHelper.getInstance().getLogs(LogTable.LAST_SYNC, true);
        Log.d("LogSyncAdapter", "performing sync");
        if (syncEmail == null) {
            mBuilder.setContentText("ERROR: please update from data.dat file! Missing sync email in system. Sync could not be processed");
            mNotificationManager.notify(NotificationConstant.LOG_UPLOAD_PROGRESS, mBuilder.build());
            return;
        }

        for(BthLog log: logs) {

            Location location = DbHelper.getInstance().getLocation(log.getLocationId());
            mBuilder.setContentText(location.getAddress());
            mBuilder.setWhen(log.getTimeCreated());
            mNotificationManager.notify(NotificationConstant.LOG_UPLOAD_PROGRESS, mBuilder.build());
            Log.d("LogSyncAdapter", "sync request for: " + log.getFinished());
            if (log.getLastSynced() <= log.getLastUpdated()) {
                List<LogEntry> entries = DbHelper.getInstance().getLogEntries(log, null, false);
                boolean success = Api.uploadLogEntriesForLog(log, entries);
                if (!success) {
                    mBuilder.setSubText("failed to upload");
                    mNotificationManager.notify(NotificationConstant.LOG_UPLOAD_PROGRESS, mBuilder.build());
                    continue;
                }

                for (LogEntry entry: entries) {
                    DbHelper.getInstance().syncLogEntry(entry);
                }
                DbHelper.getInstance().syncLog(log);
                if (log.getFinished()) {
                    Log.d("LogSyncAdapter", "sending email for finished log: " + log.getUuid());
                    sendReportEmail(log, syncEmail);
                }
            } else if (log.getLastSynced() - System.currentTimeMillis() > LOG_LIVE_TIME) {
                // Log has been in the system for seven days, should be deleted.
                DbHelper.getInstance().deleteLog(log);
            }
        }
        if (logs.size() == 0) {
            return;
        }
        mBuilder.setContentText("Sync complete");
        mBuilder.setProgress(0, 0, false);
        mNotificationManager.notify(NotificationConstant.LOG_UPLOAD_PROGRESS, mBuilder.build());

    }

    private static final int TRY_LIMIT = 10;

    public boolean sendReportEmail(BthLog log, String email) {
        LogReport report = new LogReport(log.getUuid());
        report.loadReport();
        report.constructReport();
        String message = report.getReport();
        int tries = 0;
        boolean success = false;

        while (!success && tries < TRY_LIMIT) {
            tries++;
            Log.d(LogSyncAdapter.class.getSimpleName(), "attempt: " + tries);
            try {
                LogMail.sendDemoMessage(message, email);
                success = true;
                return true;
            } catch (MessagingException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
