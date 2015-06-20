package com.scanner.bth.bluetoothscanner;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SmokeScreenFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SmokeScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SmokeScreenFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private Handler mHandler;
    private Runnable mSmokeRun;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SmokeScreenFragment.
     */
    public static SmokeScreenFragment newInstance() {
        SmokeScreenFragment fragment = new SmokeScreenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SmokeScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_smoke_screen, container, false);
        WebView wv = (WebView) rootView.findViewById(R.id.smoke_web_view);
        wv.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        wv.setWebViewClient(new WebViewController());
        wv.setBackgroundColor(Color.TRANSPARENT);
        wv.loadDataWithBaseURL("file:///android_asset/", "<html><center><img src=\"earth_spinning.gif\"></html>", "text/html", "utf-8", "");
        return rootView;
    }
    private class WebViewController extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (mHandler != null) {
            throw new RuntimeException("onAttach was somehow called twice in a row");
        }
        mHandler = new Handler();
        mSmokeRun = new Runnable() {
            @Override
            public void run() {
                mListener.finishedFakeScreen();
            }
        };

        mHandler.postDelayed(mSmokeRun, 5000);

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
        mHandler.removeCallbacks(mSmokeRun);
        mHandler = null;
        mSmokeRun = null;
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
        void finishedFakeScreen();
    }

}
