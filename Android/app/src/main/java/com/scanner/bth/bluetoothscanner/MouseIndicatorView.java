package com.scanner.bth.bluetoothscanner;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

public class MouseIndicatorView extends StatusIndicatorView {

    public MouseIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MouseIndicatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initMap() {
        indicatorMap.put(BthScanResultsModel.ScanResult.MOUSE_FOUND, Color.RED);
        indicatorMap.put(BthScanResultsModel.ScanResult.NO_MOUSE, Color.GREEN);
        indicatorMap.put(BthScanResultsModel.ScanResult.SEARCHING, Color.GRAY);
        indicatorMap.put(BthScanResultsModel.ScanResult.COMM, Color.CYAN);
    }
}
