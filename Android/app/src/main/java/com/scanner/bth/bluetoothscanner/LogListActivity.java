package com.scanner.bth.bluetoothscanner;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.scanner.bth.db.DbHelper;
import com.scanner.bth.db.Location;
import com.scanner.bth.db.BthLog;
import com.scanner.bth.db.LogTable;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import static com.scanner.bth.bluetoothscanner.ScannerActivity.LOG_ID_EXTRA;


public class LogListActivity extends ActionBarActivity {

    public static final String LOCATION_ID = "com.scanner.bth.bluetoothscannner.location_id";
    private static final int SCANNER_ACTIVITY = 1;
    LogListAdapater mAdapter;

    // Pull all logs from database
    // Let user select a log.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_list);
        ListView listView = (ListView) findViewById(R.id.activity_log_list_view);
        long locationId = getIntent().getExtras().getLong(LogListActivity.LOCATION_ID);
        final List<BthLog> logs = DbHelper.getInstance().getLogsForLocation(locationId, LogTable.TIME_CREATED, false);
        mAdapter = new LogListAdapater(logs, this);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LogListActivity.this, ScannerActivity.class);
                intent.putExtra(LOG_ID_EXTRA, logs.get(position).getUuid().toString());
                startActivityForResult(intent, SCANNER_ACTIVITY);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class LogListAdapater extends BaseAdapter {

        List<BthLog> logList;
        Context context;

        public LogListAdapater(List<BthLog> logList, Context context) {
            this.context = context;
            this.logList = logList;
        }

        public void updateLog(UUID logId) {
            for (int i = 0; i < logList.size(); i++) {
                BthLog log = logList.get(i);
                if (log.getUuid().compareTo(logId) == 0) {
                    logList.set(i, DbHelper.getInstance().getLog(logId));
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return logList.size();
        }

        @Override
        public Object getItem(int position) {
            return logList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return (long) position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rootView;
            if (convertView != null) {
                rootView = convertView;
            } else {
                rootView = inflater.inflate(R.layout.log_list_item, parent, false);
            }

            BthLog item = (BthLog) getItem(position);
            Location location = DbHelper.getInstance().getLocation(item.getLocationId());
            boolean synced = item.getLastUpdated() < item.getLastSynced();
            boolean finished = item.getFinished();

            DateFormat format = DateFormat.getDateTimeInstance();
            format.setTimeZone(TimeZone.getDefault());


            ImageView syncView =  (ImageView) rootView.findViewById(R.id.log_list_item_synced);
            ImageView finishView = (ImageView) rootView.findViewById(R.id.log_list_item_finished);
            TextView log_title = (TextView) rootView.findViewById(R.id.log_list_address);

            TextView timeCreatedView = (TextView) rootView.findViewById(
                    R.id.log_list_item_time_created);

            log_title.setText(location.getAddress());

            timeCreatedView.setText(format.format(new Date(item.getTimeCreated())));

                Log.d("TAG", "synced : " + format.format(new Date(item.getLastSynced())).toString());
                Log.d("TAG", "updated: " + format.format(new Date(item.getLastUpdated())).toString());

            if (synced) {
                syncView.setImageResource(R.drawable.uploaded);
            } else {
                syncView.setImageResource(R.drawable.uploading);
            }

            if (finished) {
                finishView.setImageResource(R.drawable.check);
            } else {
                finishView.setImageResource(R.drawable.cancel);
            }

            return rootView;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("LogListActivity", "back from activity");
        if (requestCode == SCANNER_ACTIVITY && resultCode == RESULT_OK) {
            Log.d("LogListActivity", "result was ok");
            UUID logId = UUID.fromString(data.getExtras().getString(LOG_ID_EXTRA));

            mAdapter.updateLog(logId);
        }
    }
}
