package com.scanner.bth.bluetoothscanner;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.scanner.bth.db.DbHelper;
import com.scanner.bth.db.LocationDevice;
import com.scanner.bth.db.LogEntry;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment implements AbsListView.OnItemClickListener {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    public void reloadFromDb(Integer logEntryId) {
        mAdapter.reloadFromDb(logEntryId);
    }

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */

    private class LeDeviceListAdapter extends BaseAdapter {

        ArrayList<ScannerActivity.BthScanResult> bthList = new ArrayList<>();
        HashSet<ScannerActivity.BthScanResult> bthSet = new HashSet<>();

        Context context;

        public LeDeviceListAdapter(Context context) {
            super();
            this.context = context;
        }

        public void prePopulate(LogEntry entry, LocationDevice device) {
            ScannerActivity.BthScanResult result = new ScannerActivity.BthScanResult(entry, device);
            if (bthSet.contains(result)) {
                return;
            }
            bthList.add(result);
            bthSet.add(result);
        }

        public boolean addDevice(ScannerActivity.BthScanResult result) {
            if (!bthSet.contains(result)) {
                Log.d(LeDeviceListAdapter.class.getSimpleName(), "ignoring device not on list" + result.toString());
                return false;
            } else {

                ScannerActivity.BthScanResult priorResult = bthList.get(bthList.lastIndexOf(result));
                priorResult.update(result);
                DbHelper.getInstance().updateLogEntry(priorResult.getlogEntry());
                Log.d(LeDeviceListAdapter.class.getSimpleName(), "used data to update:" + result.toString());
                Log.d(LeDeviceListAdapter.class.getSimpleName(), "communicating with: " + priorResult.toString());

                priorResult.setCommunicating(true);
                new CommTask(priorResult).execute();
                return true;
            }

        }

        public void reloadFromDb(Integer logEntryId) {
            for (ScannerActivity.BthScanResult result : bthList) {
                if (result.getlogEntry().getId() == logEntryId) {
                    result.setlogEntry(DbHelper.getInstance().getLogEntry(logEntryId));
                    notifyDataSetChanged();
                    break;
                }
            }
        }

        public void clear() {

            bthList.clear();
            bthSet.clear();
        }

        @Override
        public int getCount() {

            return bthList.size();
        }

        @Override
        public Object getItem(int position) {

            return bthList.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.bth_scan_result_list_row, parent, false);
            TextView uuidView = (TextView) rowView.findViewById(R.id.bth_scan_result_uuid);

            StatusIndicatorView indicatorView = (StatusIndicatorView) rowView.findViewById(R.id.status_indicator);

            BluetoothDevice device = bthList.get(position).getDevice();
            ScannerActivity.BthScanResult result = bthList.get(position);

            if (device != null) {
                Log.d(LeDeviceListAdapter.class.getSimpleName(), "getting view: " + device.getAddress());
            }

            BeaconParser.BeaconData beaconData = result.getBeaconData();

            uuidView.setText(result.getLocationDevice().getName());


            if (result.getlogEntry().getCurrentDeviceCheckTime() == 0 && device == null) {
                // We never made shook hands with the device before, and we currently don't have a device.
                indicatorView.setState(MouseIndicatorView.SEARCHING);
            } else if (result.isCommunicating()) {
                indicatorView.setState(MouseIndicatorView.COMM);
            } else if (beaconData.getMinor().contentEquals("0")) {
                indicatorView.setState(MouseIndicatorView.NO_MOUSE);
            } else {
                indicatorView.setState(MouseIndicatorView.MOUSE_FOUND);
            }

            return rowView;
        }
    }

    public void addDevice(ScannerActivity.BthScanResult result) {
        if (mAdapter.addDevice(result)) {
            mAdapter.notifyDataSetChanged();

        }
    }

    public void prePopulate(LogEntry entry, LocationDevice device) {
        mAdapter.prePopulate(entry, device);
    }

    private LeDeviceListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static ItemFragment newInstance(String param1, String param2) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {

    }

    public void clear() {
        mAdapter.clear();
        mAdapter.notifyDataSetInvalidated();
        Log.d("FRAGMENT", "scanning item fragment");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // TODO: Change Adapter to display your content
        mAdapter = new LeDeviceListAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            mListener.onClickDevice((ScannerActivity.BthScanResult) mAdapter.getItem(position));
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onClickDevice(ScannerActivity.BthScanResult result);
    }

    public class CommTask extends AsyncTask<Void, Void, Void> {
        private final ScannerActivity.BthScanResult scannedObject;

        public CommTask(ScannerActivity.BthScanResult scannedObject) {
            this.scannedObject = scannedObject;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Comm.initExchange(scannedObject.getlogEntry(), scannedObject.getDevice());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            scannedObject.setCommunicating(false);
            mAdapter.notifyDataSetChanged();
        }
    }

}
