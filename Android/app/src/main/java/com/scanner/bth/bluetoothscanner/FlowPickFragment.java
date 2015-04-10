package com.scanner.bth.bluetoothscanner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.scanner.bth.db.DbHelper;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FlowPickFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FlowPickFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FlowPickFragment extends Fragment {

    Button mNewButton;
    Button mPrevButton;
    Button mSyncButton;
    Button mDownloadButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FlowPickFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FlowPickFragment newInstance(String param1, String param2) {
        FlowPickFragment fragment = new FlowPickFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FlowPickFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_flow_pick, container, false);

        mNewButton = (Button) root.findViewById(R.id.flow_pick_new_button);
        mPrevButton = (Button) root.findViewById(R.id.flow_pick_prev_log_button);
        mSyncButton = (Button) root.findViewById(R.id.flow_pick_sync);
        mDownloadButton = (Button) root.findViewById(R.id.flow_pick_download);

        mDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDownloadButtonClick();

            }
        });

        mNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onNewLogButtonClick();

            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onOldLogButtonClick();

            }
        });

        return root;
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

    public void lockUi() {
        mDownloadButton.setEnabled(false);
        mNewButton.setEnabled(false);
        mPrevButton.setEnabled(false);
        mSyncButton.setEnabled(false);
    }

    public void unlockUi() {
        mDownloadButton.setEnabled(true);
        mNewButton.setEnabled(true);
        mPrevButton.setEnabled(true);
        mSyncButton.setEnabled(true);
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
        public void onDownloadButtonClick();
        public void onNewLogButtonClick();
        public void onSyncButtonClick();
        public void onOldLogButtonClick();
    }



}
