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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment implements AbsListView.OnItemClickListener, BthScanResultsModel.BthScanResultsView {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOCATION_NAME = "location_name";


    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;
    private TextView mLogNameView;
    private Button mFinishButton;
    private String mLocationName;

    @Override
    public void updateView() {
        mAdapter.notifyDataSetChanged();
        if(mListener.getBthScanResultsModel().isListAllChecked()) {
            mFinishButton.setEnabled(true);
        } else {
            mFinishButton.setEnabled(false);
        }

        mLogNameView.setText(mLocationName);
    }

    @Override
    public void updateSingleItem(BthScanResultsModel.ScanResult result) {
        mAdapter.notifyDataSetChanged();
    }

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */

    private class LeDeviceListAdapter extends BaseAdapter {
        Context context;

        public LeDeviceListAdapter(Context context) {
            super();
            this.context = context;
        }


        @Override
        public int getCount() {

            return mListener.getBthScanResultsModel().getCount();
        }

        @Override
        public Object getItem(int position) {

            return mListener.getBthScanResultsModel().getItem(position);
        }

        @Override
        public long getItemId(int position) {

            return mListener.getBthScanResultsModel().getItemId(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.bth_scan_result_list_row, parent, false);
            TextView uuidView = (TextView) rowView.findViewById(R.id.bth_scan_result_uuid);
            ImageView finishedLogEntryView = (ImageView) rowView.findViewById(R.id.bth_scan_result_finished_log_entry);
            StatusIndicatorView indicatorView = (StatusIndicatorView) rowView.findViewById(R.id.status_indicator);
            BthScanResultsModel.ScanResult result = (BthScanResultsModel.ScanResult) getItem(position);
            BluetoothDevice device = result.getDevice();


            if (device != null) {
                Log.d(LeDeviceListAdapter.class.getSimpleName(), "getting view: " + device.getAddress());
            }

            BeaconParser.BeaconData beaconData = result.getBeaconData();

            uuidView.setText(result.getLocationDevice().getName());


            if(result.getlogEntry().getCurrentDeviceCheckTime() == 0) {
                finishedLogEntryView.setVisibility(View.INVISIBLE);
            } else {
                finishedLogEntryView.setVisibility(View.VISIBLE);
            }

            indicatorView.setState(result.getStatus());

            return rowView;
        }
    }

    private LeDeviceListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static ItemFragment newInstance(String locationName) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putString(LOCATION_NAME, locationName);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // TODO: Change Adapter to display your content
        mAdapter = new LeDeviceListAdapter(getActivity());

        if (getArguments() != null) {
            mLocationName = getArguments().getString(LOCATION_NAME);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mLogNameView = (TextView) view.findViewById(R.id.fragment_item_list_log_location);
        mFinishButton = (Button) view.findViewById(R.id.fragment_item_list_complete_log);

        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.generateReport();
            }
        });

        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        updateView();
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

        mListener.getBthScanResultsModel().attachView(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.getBthScanResultsModel().detachView(this);
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            mListener.onClickDevice((BthScanResultsModel.ScanResult) mAdapter.getItem(position));
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
        public void onClickDevice(BthScanResultsModel.ScanResult result);
        public BthScanResultsModel getBthScanResultsModel();
        void generateReport();
    }
}
