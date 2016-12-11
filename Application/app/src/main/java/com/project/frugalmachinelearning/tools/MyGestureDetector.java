package com.project.frugalmachinelearning.tools;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.project.frugalmachinelearning.MainActivity;
import com.project.frugalmachinelearning.R;
import com.project.frugalmachinelearning.gui.Dialogs;

/**
 * Created by Mikhail on 20.11.2016.
 */

public class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

    private ImageButton bStop;
    private TextView mGenericActivity;
    private MainActivity activity;

    public MyGestureDetector(ImageButton bStop, TextView mGenericActivity, MainActivity activity) {
        this.bStop = bStop;
        this.mGenericActivity = mGenericActivity;
        this.activity = activity;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if (bStop.getVisibility() == View.VISIBLE) {
                mGenericActivity.setVisibility(View.INVISIBLE);
                bStop.setVisibility(View.INVISIBLE);
            } else {
                if (!(mGenericActivity.getText().toString().equals(activity.getResources().getString(R.string.new_activity)))) {
                    mGenericActivity.setVisibility(View.VISIBLE);
                }
                bStop.setVisibility(View.VISIBLE);
            }
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        Dialogs.showCustomDialog(activity);
    }

}
