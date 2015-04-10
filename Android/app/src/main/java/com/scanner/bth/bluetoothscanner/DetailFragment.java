package com.scanner.bth.bluetoothscanner;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.scanner.bth.db.DbHelper;
import com.scanner.bth.db.LogEntry;


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
    private static final String LOG_ENTRY_ID = "log_id";
    private static final String OWNER = "owner";

    private Integer logEntryId;
    private LogEntry logEntry;
    private String mOwner;

    private OnFragmentInteractionListener mListener;
    Button mFinishButton;
    EditText mMessageField;
    MouseIndicatorView mStatusView;

    public static DetailFragment newInstance(
            Integer logEntryId, String owner) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(LOG_ENTRY_ID, logEntryId);
        args.putString(OWNER, owner);
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
            logEntryId = getArguments().getInt(LOG_ENTRY_ID);
            logEntry = DbHelper.getInstance().getLogEntry(logEntryId);
            mOwner = getArguments().getString(OWNER);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mFinishButton = (Button) rootView.findViewById(R.id.fragment_detail_finish_button);
        mMessageField = (EditText) rootView.findViewById(R.id.fragment_detail_message);
        mMessageField.setText(logEntry.getMessage());

        mStatusView = (MouseIndicatorView) rootView.findViewById(R.id.fragment_detail_status_indicator);
        BeaconParser.BeaconData data = BeaconParser.read(logEntry.getByteRecord());

        if (Integer.valueOf(data.getMinor()) == 0) {
            mStatusView.setState(MouseIndicatorView.NO_MOUSE);
        } else {
            mStatusView.setState(MouseIndicatorView.MOUSE_FOUND);
        }

        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConfirmTask(mListener.getDeviceForEntry(logEntry), mOwner, logEntry).execute();
            }
        });
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
        public void finishEntry(Integer logEntryId);
        public BluetoothDevice getDeviceForEntry(LogEntry entry);
    }

    public class ConfirmTask extends AsyncTask<Void, Void, Void> {
        private final BluetoothDevice device;
        private final String owner;
        private final LogEntry entry;

        public ConfirmTask(BluetoothDevice device, String owner, LogEntry entry) {
            this.device = device;
            this.owner = owner;
            this.entry = entry;
        }

        @Override
        protected void onPreExecute() {
            mFinishButton.setEnabled(false);
            mMessageField.setEnabled(false);
            mStatusView.setState(MouseIndicatorView.COMM);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Comm.sign(entry, device, owner);

            return null;
        }

        @Override
        protected  void onPostExecute(Void result) {
            mFinishButton.setEnabled(true);
            entry.setMessage(mMessageField.getText().toString());
            mMessageField.setEnabled(true);
            DbHelper.getInstance().updateLogEntry(entry);
            mListener.finishEntry(entry.getId());

            BeaconParser.BeaconData data = BeaconParser.read(logEntry.getByteRecord());

            if (Integer.valueOf(data.getMinor()) == 0) {
                mStatusView.setState(MouseIndicatorView.NO_MOUSE);
            } else {
                mStatusView.setState(MouseIndicatorView.MOUSE_FOUND);
            }
        }
    }

}
