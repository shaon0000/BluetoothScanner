package com.scanner.bth.bluetoothscanner;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;

/**
 *
 *
 *
 */
public class StatusIndicatorView extends View {

    public static final String STATUS_ON = "on";
    public static final String STATUS_OFF = "off";

    HashMap<String, Integer> indicatorMap = new HashMap<String, Integer>();

    public void setIndicatorColour(int colour, String state) {
        indicatorMap.put(state, colour);
    }

    public void setState(String state) {
        Integer colour = indicatorMap.get(state);
        setBackgroundColor(colour);
    }

    public StatusIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMap();
    }
    public StatusIndicatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initMap();
    }

    public void initMap() {
        indicatorMap.put(STATUS_ON, Color.GREEN);
        indicatorMap.put(STATUS_OFF, Color.RED);
    }


}
