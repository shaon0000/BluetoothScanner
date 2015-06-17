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
 * {@link TechnicalDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TechnicalDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TechnicalDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MAJOR = "major";
    private static final String MINOR = "minor";
    private static final String LEVEL = "battery_level";
    private static final String RANGE = "range";
    private static final String UUID = "uuid";

    private String uuid;
    private int major;
    private int minor;
    private int level;
    private int range;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param major major value of the id. If major is -1, value was not found.
     * @param minor minor value of the id. If minor is -1, value was not found.
     * @param level current battery level of the device. Same as major/minor for value.
     * @param range current estimated range of device. Same as major/minor for value.
     * @return A new instance of fragment TechnicalDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TechnicalDetailFragment newInstance(String uuid, int major, int minor, int level, int range) {
        TechnicalDetailFragment fragment = new TechnicalDetailFragment();
        Bundle args = new Bundle();
        args.putInt(MAJOR, major);
        args.putInt(MINOR, minor);
        args.putInt(LEVEL, level);
        args.putInt(RANGE, range);
        args.putString(UUID, uuid);

        fragment.setArguments(args);
        return fragment;
    }

    public TechnicalDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            major = getArguments().getInt(MAJOR);
            minor = getArguments().getInt(MINOR);
            range = getArguments().getInt(RANGE);
            level = getArguments().getInt(LEVEL);
            uuid = getArguments().getString(UUID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_technical_detail, container, false);
        TextView minorView = (TextView) rootView.findViewById(R.id.technical_detail_fragment_minor);
        TextView majorView = (TextView) rootView.findViewById(R.id.technical_detail_fragment_major);
        TextView rangeView = (TextView) rootView.findViewById(R.id.technical_detail_fragment_range);
        TextView levelView = (TextView) rootView.findViewById(R.id.technical_detail_fragment_battery);
        TextView uuidView = (TextView) rootView.findViewById(R.id.technical_detail_fragment_uuid);

        if (major == -1) {
            majorView.setText("?");
        } else {
            majorView.setText(String.valueOf(major));
        }

        if (minor == -1) {
            minorView.setText("?");
        } else {
            minorView.setText(String.valueOf(minor));
        }

        if (range == -1) {
            rangeView.setText("?");
        } else {
            rangeView.setText(String.valueOf(range) + " m");
        }

        if (level == -1) {
            levelView.setText("?");
        } else {
            levelView.setText(String.valueOf(level) + "%");
        }

        uuidView.setText(uuid);

        return rootView;
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
    }

}
