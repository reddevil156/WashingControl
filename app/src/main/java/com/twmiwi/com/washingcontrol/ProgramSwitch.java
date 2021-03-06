package com.twmiwi.com.washingcontrol;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by tw on 06.03.2017.
 */

public class ProgramSwitch extends AppCompatImageView {

    private int switchStatus;

    public ProgramSwitch(Context context) {
        super(context);
        initialize();
    }

    public ProgramSwitch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgramSwitch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initialize() {

    }

    public void setActualStatus(int switchStatus) {

        this.switchStatus = switchStatus;
        setRotation(new Float(switchStatus*15));

    }

    public int getActualStatus() {
        return this.switchStatus;
    }
}
