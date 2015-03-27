package com.scanner.bth.bluetoothscanner;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */

    private class LeDeviceListAdapter extends BaseAdapter {

        ArrayList<MainActivity.BthScanResult> bthList = new ArrayList<>();
        HashSet<MainActivity.BthScanResult> bthSet = new HashSet<>();

        Context context;

        public LeDeviceListAdapter(Context context) {
            super();
            this.context = context;
        }
        public boolean addDevice(MainActivity.BthScanResult result) {
            if (!bthSet.contains(result)) {
                bthList.add(result);
                bthSet.add(result);
                return true;
            } else {
                Log.d(LeDeviceListAdapter.class.getSimpleName(), "ignored repeat device:" + result.toString());
                return false;
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
            TextView majorView = (TextView) rowView.findViewById(R.id.bth_scan_result_major);
            TextView minorView = (TextView) rowView.findViewById(R.id.bth_scan_result_minor);
            TextView uuidView = (TextView) rowView.findViewById(R.id.bth_scan_result_uuid);
            TextView macView = (TextView) rowView.findViewById(R.id.bth_scan_result_mac_address);

            StatusIndicatorView indicatorView = (StatusIndicatorView) rowView.findViewById(R.id.status_indicator);

            BluetoothDevice device = bthList.get(position).getDevice();
            MainActivity.BthScanResult result = bthList.get(position);
            Log.d(LeDeviceListAdapter.class.getSimpleName(), "getting view: " + device.getAddress());
            BeaconParser.BeaconData beaconData = result.getBeaconData();

            uuidView.setText(beaconData.getProximity_uuid());
            majorView.setText(beaconData.getMajor());
            minorView.setText(beaconData.getMinor());
            macView.setText(device.getAddress());

            return rowView;
        }
    }

    public void addDevice(MainActivity.BthScanResult result) {
        if (mAdapter.addDevice(result)) {
            mAdapter.notifyDataSetChanged();
        }
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

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

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
            mListener.onClickDevice((MainActivity.BthScanResult) mAdapter.getItem(position));
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
        public void onClickDevice(MainActivity.BthScanResult result);
    }

}
