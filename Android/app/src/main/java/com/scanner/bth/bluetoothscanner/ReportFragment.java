package com.scanner.bth.bluetoothscanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.scanner.bth.db.DbHelper;
import com.scanner.bth.db.BthLog;
import com.scanner.bth.report.LogReport;

import java.util.UUID;

import static com.scanner.bth.bluetoothscanner.ScannerActivity.LOG_ID_EXTRA;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReportFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Fragment is used to show a finalized report preview.
 */
public class ReportFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOG_ID = "log_id";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private UUID logId;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Button mSubmitButton;
    private TextView mReportContentView;
    private LogReport mReportBuilder;
    private Button mCloseButton;
    private BthLog mLog;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment ReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportFragment newInstance(UUID logId) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putString(LOG_ID, logId.toString());
        fragment.setArguments(args);
        return fragment;
    }

    public ReportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            logId = UUID.fromString(getArguments().getString(LOG_ID));
        }

        mLog = DbHelper.getInstance().getLog(logId);
        mReportBuilder = new LogReport(logId);
        mReportBuilder.loadReport();
        mReportBuilder.constructReport();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_report, container, false);
        mSubmitButton = (Button) rootView.findViewById(R.id.fragment_report_button_submit_report);
        mReportContentView = (TextView) rootView.findViewById(R.id.fragment_report__report_content);
        mCloseButton = (Button) rootView.findViewById(R.id.fragment_report_close_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLog.setFinished(true);
                DbHelper.getInstance().updateLog(mLog);
                updateView();

            }
        });

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra(LOG_ID_EXTRA, mLog.getUuid().toString());
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
            }
        });
        updateView();
        return rootView;
    }

    public void updateView() {
        mReportContentView.setText(mReportBuilder.getReport());
        if (mLog.getFinished()) {
            mSubmitButton.setVisibility(View.GONE);
            mCloseButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.VISIBLE);
            mCloseButton.setVisibility(View.GONE);
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
        public void reportFinished();
    }

}
