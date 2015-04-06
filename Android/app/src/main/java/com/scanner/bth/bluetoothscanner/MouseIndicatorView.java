package com.scanner.bth.bluetoothscanner;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

public class MouseIndicatorView extends StatusIndicatorView {

    public static final String MOUSE_FOUND = "mouse_found";
    public static final String NO_MOUSE = "no_mouse";
    public static final String SEARCHING = "searching";
    public static final String COMM = "comm";

    public MouseIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MouseIndicatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initMap() {
        indicatorMap.put(MOUSE_FOUND, Color.GREEN);
        indicatorMap.put(NO_MOUSE, Color.RED);
        indicatorMap.put(SEARCHING, Color.GRAY);
        indicatorMap.put(COMM, Color.CYAN);
    }
}
