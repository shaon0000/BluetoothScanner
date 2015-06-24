package com.scanner.bth.bluetoothscanner;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.scanner.bth.auth.AuthHelper;
import com.scanner.bth.auth.AuthLoginActivity;
import com.scanner.bth.auth.Authenticator;
import com.scanner.bth.db.AccountDetails;
import com.scanner.bth.db.DbHelper;

import java.io.IOException;


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
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.scanner.bth.bluetoothscanner.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "com.smartwave.android.datasync";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Button mLogoutButton;
    private Button mAboutButton;

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
        mLogoutButton = (Button) root.findViewById(R.id.flow_pick_logout);
        mAboutButton = (Button) root.findViewById(R.id.flow_pick_about);

        mAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAboutButtonClick();
            }
        });

        mSyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSyncButtonClick();
            }
        });
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

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthHelper.logout(getActivity());
                updateView();
            }
        });
        updateView();
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
        public void onAboutButtonClick();
    }


    public void updateView() {
        String username = AuthHelper.getUsername(getActivity());
        if (username == null) {
            Log.d(FlowPickFragment.class.getSimpleName(), "no username found");
            Intent intent = new Intent(getActivity(), AuthLoginActivity.class);
            intent.putExtra(AuthLoginActivity.ARG_AUTH_TYPE, "auth");
            intent.putExtra(AuthLoginActivity.ARG_ACCOUNT_TYPE, ACCOUNT_TYPE);
            intent.putExtra(AuthLoginActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
            startActivityForResult(intent, FlowPickActivity.AUTH_LOGIN);
            return;
        }

        Log.d(FlowPickFragment.class.getSimpleName(), "username: " + username);

        final Account account = new Account(username, ACCOUNT_TYPE);
        final AccountManagerFuture<Bundle> future = AccountManager.get(getActivity())
                .getAuthToken(account, "auth", null, getActivity(), null, null);
        final Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {

                Bundle bnd;
                try {
                    bnd = future.getResult();
                    if (bnd == null) {
                        return;
                    }
                    final String authToken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    if (!TextUtils.isEmpty(authToken)) {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AccountDetails details = DbHelper.getInstance().getAccountDetails();
                                if (details.getIsAdmin()) {
                                    mDownloadButton.setVisibility(View.VISIBLE);
                                    mPrevButton.setVisibility(View.VISIBLE);
                                    mNewButton.setVisibility(View.VISIBLE);
                                    mSyncButton.setVisibility(View.VISIBLE);
                                } else {
                                    mDownloadButton.setVisibility(View.GONE);
                                    mPrevButton.setVisibility(View.GONE);
                                    mNewButton.setVisibility(View.VISIBLE);
                                    mSyncButton.setVisibility(View.VISIBLE);
                                    Toast.makeText(getActivity(), "hiding the goods", Toast.LENGTH_LONG).show();
                                }



                                // Turn on automatic syncing for the default account and authority
                                ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
                            }
                        });


                    }
                } catch (OperationCanceledException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (AuthenticatorException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(FlowPickFragment.class.getSimpleName(), "returned from activity");
        if (requestCode == FlowPickActivity.AUTH_LOGIN) {
                updateView();
        }
    }
}
