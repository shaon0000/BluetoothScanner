package com.scanner.bth.bluetoothscanner;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.scanner.bth.auth.AuthHelper;
import com.scanner.bth.db.BthLog;
import com.scanner.bth.db.DbHelper;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment implements BthScanResultsModel.BthScanResultsView {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOG_ENTRY_ID = "log_id";
    private static final String OWNER = "owner";
    private static final String POSITION = "position";
    private static final String DEVICE_NAME = "device_name";

    private Integer logEntryId;
    private String mOwner;
    private String mDeviceName;
    private int mPosition;
    private BthLog mLog;

    private OnFragmentInteractionListener mListener;
    Button mFinishButton;
    EditText mMessageField;
    MouseIndicatorView mStatusView;
    private TextView mDeviceNameView;
    private TextView mPositionView;
    private BthScanResultsModel.ScanResult mScanResult;
    private ToggleButton mDeviceNotFound;
    private TextView mDeviceLastChecked;
    private TextView mPreviousMouseFound;
    private TextView mCurrentMouseFound;
    private Button mTechnicalDetailsButton;
    private Animation mFaderAnimation;

    public static DetailFragment newInstance(
            Integer logEntryId, String owner, String deviceName, int position) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(LOG_ENTRY_ID, logEntryId);
        args.putString(OWNER, owner);
        args.putString(DEVICE_NAME, deviceName);
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
            Log.d("DetailFragment", "log id: " + logEntryId);
            mScanResult = mListener.getBthScanResultsModel().getScanResult(logEntryId);
            mOwner = getArguments().getString(OWNER);
            mDeviceName = getArguments().getString(DEVICE_NAME);
            mPosition = mListener.getBthScanResultsModel().getPosition(mScanResult);
            mLog = mListener.getLog();
        }

    }

    public boolean recordErrorMessage(boolean isChecked) {
        if (isChecked == mScanResult.getlogEntry().getShouldIgnore()) {
            return false;
        }

        if (!isChecked) {
            mScanResult.getlogEntry().setShouldIgnore(false);
            DbHelper.getInstance().updateLogEntry(mScanResult.getlogEntry());
            mListener.getBthScanResultsModel().updateViews(mScanResult);
            return true;
        }

        String message = mMessageField.getText() == null ? "" : mMessageField.getText().toString();
        if(message.contentEquals("")) {
            Toast.makeText(DetailFragment.this.getActivity(), "Must enter a report", Toast.LENGTH_LONG).show();
            mDeviceNotFound.setChecked(false);
            return false;
        }
        mScanResult.getlogEntry().setShouldIgnore(true);

        return true;
    }

    public void recordMessage(String message) {
        mScanResult.getlogEntry().setMessage(message);
        DbHelper.getInstance().updateLogEntry(mScanResult.getlogEntry());
        mListener.getBthScanResultsModel().updateViews(mScanResult);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mFinishButton = (Button) rootView.findViewById(R.id.fragment_detail_finish_button);
        mMessageField = (EditText) rootView.findViewById(R.id.fragment_detail_message);
        mDeviceNameView = (TextView) rootView.findViewById(R.id.fragment_detail_device_name);
        mPositionView = (TextView) rootView.findViewById(R.id.fragment_detail_index);
        mStatusView = (MouseIndicatorView) rootView.findViewById(R.id.fragment_detail_status_indicator);
        mDeviceNotFound = (ToggleButton) rootView.findViewById(R.id.fragment_detail_device_not_found);
        mDeviceLastChecked = (TextView) rootView.findViewById(R.id.detail_fragment_device_last_checked);
        mPreviousMouseFound = (TextView) rootView.findViewById(R.id.detail_fragment_mouse_last_found);
        mCurrentMouseFound = (TextView) rootView.findViewById(R.id.detail_fragment_mouse_current_mouse_found);
        mTechnicalDetailsButton = (Button) rootView.findViewById(R.id.fragment_detail_technical_details_button);
        mFaderAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_out);
        updateView();

        mTechnicalDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onTechnicalDetailsClick(mScanResult);
            }
        });
        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mScanResult.getlogEntry().getShouldIgnore()) {
                    recordMessage(mMessageField.getText() == null ? "" : mMessageField.getText().toString());
                }

                if (((mScanResult.getStatus() & BthScanResultsModel.ScanResult.MOUSE_FOUND) != 0 || mScanResult.getlogEntry().getShouldIgnore())
                        && mMessageField.getText().toString().contentEquals("")) {
                    Toast.makeText(getActivity(), "A comment is required", Toast.LENGTH_SHORT).show();
                } else if (mScanResult.getlogEntry().getShouldIgnore() && ((mScanResult.getStatus() & BthScanResultsModel.ScanResult.SEARCHING) != 0)) {
                    Toast.makeText(getActivity(), "Recorded as broken", Toast.LENGTH_SHORT).show();
                } else {
                    new ConfirmTask(mMessageField.getText().toString()).execute();
                }
                }

        });
        mDeviceNotFound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                recordErrorMessage(isChecked);
                if (mScanResult.getlogEntry().getShouldIgnore()) {
                    recordMessage(mMessageField.getText() == null ? "" : mMessageField.getText().toString());
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
            mListener.getBthScanResultsModel().attachView(this);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.getBthScanResultsModel().detachView(this);
        mListener = null;
    }

    @Override
    public void updateView() {
        mDeviceNameView.setText(mDeviceName);
        mPositionView.setText(String.valueOf(mPosition));
        mMessageField.setText(mScanResult.getlogEntry().getMessage());
        int minor = Integer.valueOf(mScanResult.getBeaconData().getMinor());

        if ((mScanResult.getStatus() & BthScanResultsModel.ScanResult.MOUSE_FOUND) == 0) {
            mCurrentMouseFound.setText("No mouse found");
        } else {
            mCurrentMouseFound.setText("Mouse found, " + AuthHelper.getUsername(getActivity()) + ", " + new Date(mScanResult.getlogEntry().getCurrentMouseEventTime()).toString());
        }
        if (mScanResult.getlogEntry().getDeviceLastChecked() == 0) {
            mDeviceLastChecked.setText("Not checked yet");
        } else {
            mDeviceLastChecked.setText(new Date(mScanResult.getlogEntry().getDeviceLastChecked()).toString());
        }
        if (mScanResult.getlogEntry().getLastMouseEvent() == 0) {
            mPreviousMouseFound.setText("Not connected to device");
        } else {
            mPreviousMouseFound.setText(new Date(mScanResult.getlogEntry().getLastMouseEvent()).toString());
        }

        mDeviceNotFound.setChecked(mScanResult.getlogEntry().getShouldIgnore());

        mStatusView.setState(mScanResult.getStatus());
        if (mScanResult.getStatus() == BthScanResultsModel.ScanResult.COMM) {

            mStatusView.startAnimation(mFaderAnimation);
        } else {
            mStatusView.clearAnimation();
        }
        // If we're searching or we're communicating, we can't type up a message.
        if ((mScanResult.getStatus() & (BthScanResultsModel.ScanResult.SEARCHING | BthScanResultsModel.ScanResult.COMM)) != 0) {
            mFinishButton.setEnabled(false);
        } else {
            mFinishButton.setEnabled(true);
        }

        if ((mScanResult.getStatus() & BthScanResultsModel.ScanResult.COMM) != 0) {
            mMessageField.setEnabled(false);
            mDeviceNotFound.setEnabled(false);
        } else {
            mMessageField.setEnabled(true);
            mDeviceNotFound.setEnabled(true);
        }

        if (mDeviceNotFound.isChecked()) {
            mFinishButton.setEnabled(true);
        }

        if (mLog.getFinished()) {
            mDeviceNotFound.setEnabled(false);
            mMessageField.setEnabled(false);
            mFinishButton.setEnabled(false);
        }

    }

    @Override
    public void updateSingleItem(BthScanResultsModel.ScanResult result) {
        if (result.getBeaconData().getProximity_uuid() == mScanResult.getBeaconData().getProximity_uuid()) {
            updateView();
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
        public BthScanResultsModel getBthScanResultsModel();
        BthLog getLog();
        public void onTechnicalDetailsClick(BthScanResultsModel.ScanResult mScanResult);
    }

    public class ConfirmTask extends BthScanResultsModel.BaseCommTask<Void, Void, Void> {
        String message;
        public ConfirmTask(String message) {
            super(mScanResult, mListener.getBthScanResultsModel());
            this.message = message;
        }

        @Override
        public Void doInBackground(Void... params) {
            if (mScanResult.getlogEntry().getCurrentSigner() == null) {
                Comm.sign(mScanResult.getlogEntry(), mScanResult.getDevice(), mOwner);
            } else {

            }
            return null;
        }

        @Override
        protected  void onPostExecute(Void result) {
            mMessageField.setEnabled(true);
            mScanResult.getlogEntry().setMessage(message);
            DbHelper.getInstance().updateLogEntry(mScanResult.getlogEntry());

            super.onPostExecute(result);
        }
    }

}
