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

import com.scanner.bth.bluetoothscanner.R;
import com.scanner.bth.db.DbHelper;
import com.scanner.bth.db.BthLog;
import com.scanner.bth.db.Location;
import com.scanner.bth.db.LogEntry;
import com.scanner.bth.db.LogTable;
import com.scanner.bth.http.Api;

import java.util.List;

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
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setContentTitle("Syncing logs");
        mBuilder.setSmallIcon(R.drawable.uploading);
        mBuilder.setProgress(0, 0, true);
        List<BthLog> logs = DbHelper.getInstance().getLogs(LogTable.LAST_SYNC, true);
        Log.d("LogSyncAdapter", "performing sync");
        mBuilder.setVibrate(new long [] {50, 1000, 100, 1000});
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
            } else if (log.getLastSynced() - System.currentTimeMillis() > LOG_LIVE_TIME) {
                // Log has been in the system for seven days, should be deleted.
                DbHelper.getInstance().deleteLog(log);
            }
        }
        mBuilder.setContentText("Sync complete");
        mBuilder.setProgress(0, 0, false);
        mNotificationManager.notify(NotificationConstant.LOG_UPLOAD_PROGRESS, mBuilder.build());

    }
}
