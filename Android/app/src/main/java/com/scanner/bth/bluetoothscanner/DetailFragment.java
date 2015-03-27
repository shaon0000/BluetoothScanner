package com.scanner.bth.bluetoothscanner;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String BEACON_PREFIX = "beacon_prefix";
    private static final String PROXIMITY_UUID = "proximity_uuid";
    private static final String MAJOR = "major";
    private static final String MINOR = "minor";
    private static final String TX = "tx";
    private static final String SCAN_RECORD = "scan_record";

    private String beaconPrefix;
    private String proximityUUUID;
    private String major;
    private String minor;
    private String tx;
    private byte[] scanRecord;

    private OnFragmentInteractionListener mListener;
    private ScanRecordAdapter mScanRecordAdapter;

    public static DetailFragment newInstance(
            String beaconPrefix,
            String proximityUUUID,
            String major,
            String minor,
            String tx,
            byte[] scanRecord) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(BEACON_PREFIX, beaconPrefix);
        args.putString(PROXIMITY_UUID, proximityUUUID);
        args.putString(MAJOR, major);
        args.putString(MINOR, minor);
        args.putString(TX, tx);
        args.putByteArray(SCAN_RECORD, scanRecord);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {
            beaconPrefix = getArguments().getString(BEACON_PREFIX);
            proximityUUUID = getArguments().getString(PROXIMITY_UUID);
            major = getArguments().getString(MAJOR);
            minor = getArguments().getString(MINOR);
            tx = getArguments().getString(TX);
            scanRecord = getArguments().getByteArray(SCAN_RECORD);
        }

        mScanRecordAdapter = new ScanRecordAdapter(scanRecord, getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView majorText = (TextView) rootView.findViewById(R.id.detail_layout_major);
        TextView minorText = (TextView) rootView.findViewById(R.id.detail_layout_minor);
        TextView prefixText = (TextView) rootView.findViewById(R.id.detail_layout_beacon_prefix);
        TextView uuidText = (TextView) rootView.findViewById(R.id.detail_layout_uuid);
        TextView txText = (TextView) rootView.findViewById(R.id.detail_layout_tx);
        GridView scanRecordGrid = (GridView) rootView.findViewById(R.id.bth_detail_scan_record);

        scanRecordGrid.setAdapter(mScanRecordAdapter);

        majorText.setText(major);
        minorText.setText(minor);
        prefixText.setText(beaconPrefix);
        uuidText.setText(proximityUUUID);
        txText.setText(tx);

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        public void onFragmentInteraction(Uri uri);
    }

    public class ScanRecordAdapter extends BaseAdapter {

        byte[] items;
        Context context;

        public ScanRecordAdapter(byte[] items, Context context) {
            this.items = items;
            this.context = context;
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View cellView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                cellView = inflater.inflate(R.layout.detail_cell, parent, false);
            } else {
                cellView = convertView;
            }

            TextView byteValueView = (TextView) cellView.findViewById(R.id.bth_detail_cell_value);
            TextView byteTypeView = (TextView) cellView.findViewById(R.id.bth_detail_cell_type);
            TextView cellIndexView = (TextView) cellView.findViewById(R.id.bth_detail_cell_index);
            byteValueView.setText(BeaconParser.subBytesToHex(items, position, position));
            cellIndexView.setText(String.valueOf(position));
            byteTypeView.setText(BeaconParser.getByteTypeForIndex(position).getType());

            if (position < 9) {
                cellView.setBackgroundResource(R.color.brigh_orange);
            } else if (position < 30) {
                cellView.setBackgroundResource(R.color.light_blue);
            } else {
                cellView.setBackgroundResource(R.color.smokey_grey);
            }


            return cellView;
        }
    }

}
