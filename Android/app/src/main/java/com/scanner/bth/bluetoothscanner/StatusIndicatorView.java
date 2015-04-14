package com.scanner.bth.bluetoothscanner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;

public abstract class StatusIndicatorView extends View {

    HashMap<Integer, Integer> indicatorMap = new HashMap<Integer, Integer>();

    public void setState(int state) {
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
