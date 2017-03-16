package com.twmiwi.com.washingcontrol;

/**
 * Created by tobia on 13.03.2017.
 */

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by tw on 06.03.2017.
 */

public class MiniSwitch extends AppCompatImageView {

    private int switchStatus;

    public MiniSwitch(Context context) {
        super(context);
        initialize();
    }

    public MiniSwitch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MiniSwitch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initialize() {

    }

    public void setActualStatus(int switchStatus) {

        this.switchStatus = switchStatus;
        setRotation(new Float(switchStatus*72));

    }

    public int getActualStatus() {
        return this.switchStatus;
    }
}