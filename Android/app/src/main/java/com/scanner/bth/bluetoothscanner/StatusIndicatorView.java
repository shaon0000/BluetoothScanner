package com.scanner.bth.bluetoothscanner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;

/**
 *
 *
 *
 */
public class StatusIndicatorView extends View {

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
    }
    public StatusIndicatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


}
