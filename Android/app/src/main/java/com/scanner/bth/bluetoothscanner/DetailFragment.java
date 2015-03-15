package com.scanner.bth.bluetoothscanner;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private String beaconPrefix;
    private String proximityUUUID;
    private String major;
    private String minor;
    private String tx;

    private OnFragmentInteractionListener mListener;

    public static DetailFragment newInstance(
            String beaconPrefix,
            String proximityUUUID,
            String major,
            String minor,
            String tx) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(BEACON_PREFIX, beaconPrefix);
        args.putString(PROXIMITY_UUID, proximityUUUID);
        args.putString(MAJOR, major);
        args.putString(MINOR, minor);
        args.putString(TX, tx);

        fragment.setArguments(args);
        return fragment;
    }

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            beaconPrefix = getArguments().getString(BEACON_PREFIX);
            proximityUUUID = getArguments().getString(PROXIMITY_UUID);
            major = getArguments().getString(MAJOR);
            minor = getArguments().getString(MINOR);
            tx = getArguments().getString(TX);
        }
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

}
