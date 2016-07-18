package com.example.android.project7;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * Created by Jon on 6/28/2016.
 */
public class MyLinearLayout extends LinearLayout implements Checkable {
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    private boolean mChecked = false;

    public MyLinearLayout(Context context, AttributeSet attrs){
        super(context,attrs);
    }
    @Override
    public void setChecked(boolean checked) {
        if (checked != mChecked){
            mChecked = checked;
            refreshDrawableState();
        }

    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace){
        final int[] drawableState = super.onCreateDrawableState(extraSpace+1);
        if(isChecked()){
            mergeDrawableStates(drawableState,CHECKED_STATE_SET);
        }
        return drawableState;
    }
}
