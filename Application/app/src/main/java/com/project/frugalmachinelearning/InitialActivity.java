package com.project.frugalmachinelearning;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Mikhail on 09.06.2016.
 */

public class InitialActivity extends Activity {

    private FrameLayout fLeft;
    private FrameLayout fRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.intro_screen);

        // screen on for the whole run of the application
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        fLeft = (FrameLayout) findViewById(R.id.collect_framelayout);
        fRight = (FrameLayout) findViewById(R.id.recognize_framelayout);
    }

    public void collectData(View view) {
        Toast toast = Toast.makeText(InitialActivity.this, "Data collection started", Toast.LENGTH_SHORT);
        toast.show();

        Intent callMainIntent = new Intent(InitialActivity.this, MainActivity.class);
        callMainIntent.putExtra("APP STATE", "COLLECT_DATA");
        callMainIntent.putExtra("background", getBackgroundHexColor(fLeft));

        startActivity(callMainIntent);
    }

    public void recognizeActivities (View view) {
        Toast toast = Toast.makeText(InitialActivity.this, "Activity recognition started", Toast.LENGTH_SHORT);

        // center text in toast message
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);

        toast.show();

        Intent callMainIntent = new Intent(InitialActivity.this, MainActivity.class);
        callMainIntent.putExtra("APP STATE", "RECOGNIZE_ACTIVITY");
        callMainIntent.putExtra("background", getBackgroundHexColor(fRight));

        startActivity(callMainIntent);
    }

    public String getBackgroundHexColor(View element) {
        int color = Color.TRANSPARENT;
        Drawable background = element.getBackground();
        if (background instanceof ColorDrawable)
            color = ((ColorDrawable) background).getColor();

        String previousHexValue = Integer.toHexString(color);

        return previousHexValue;
    }

}
