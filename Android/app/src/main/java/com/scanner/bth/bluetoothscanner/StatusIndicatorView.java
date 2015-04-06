package com.scanner.bth.bluetoothscanner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;

public abstract class StatusIndicatorView extends View {

    HashMap<String, Integer> indicatorMap = new HashMap<String, Integer>();

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

    abstract public void initMap();
}
