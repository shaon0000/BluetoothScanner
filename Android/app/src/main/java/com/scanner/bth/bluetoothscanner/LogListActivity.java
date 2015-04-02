package com.scanner.bth.bluetoothscanner;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.scanner.bth.db.DbHelper;
import com.scanner.bth.db.Log;
import com.scanner.bth.db.LogTable;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class LogListActivity extends ActionBarActivity {

    // Pull all logs from database
    // Let user select a log.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_list);
        ListView listView = (ListView) findViewById(R.id.activity_log_list_view);
        List<Log> logs = new DbHelper(this).getLogs(LogTable.TIME_CREATED, false);
        LogListAdapater adapter = new LogListAdapater(logs, this);
        listView.setAdapter(adapter);
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

        List<Log> logList;
        Context context;

        public LogListAdapater(List<Log> logList, Context context) {
            this.context = context;
            this.logList = logList;
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
            return logList.get(position).getId();
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

            Log item = (Log) getItem(position);

            boolean synced = item.getLastUpdated() < item.getLastSynced();
            boolean finished = item.getFinished();

            DateFormat format = DateFormat.getDateTimeInstance();
            format.setTimeZone(TimeZone.getDefault());


            ImageView syncView =  (ImageView) rootView.findViewById(R.id.log_list_item_synced);
            ImageView finishView = (ImageView) rootView.findViewById(R.id.log_list_item_finished);
            TextView timeCreatedView = (TextView) rootView.findViewById(
                    R.id.log_list_item_time_created);

            timeCreatedView.setText(format.format(new Date(item.getTimeCreated())));

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
}
