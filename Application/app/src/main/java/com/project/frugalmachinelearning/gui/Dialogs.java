package com.project.frugalmachinelearning.gui;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.project.frugalmachinelearning.MainActivity;
import com.project.frugalmachinelearning.R;

/**
 * Created by Mikhail on 20.11.2016.
 */
public abstract class Dialogs {

    public static void showCustomDialog(final MainActivity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText(activity.getResources().getString(R.string.dialog_title));

        Button bCancel = (Button) dialog.findViewById(R.id.cancel);
        // close dialog when this button is pressed

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        Button bExit = (Button) dialog.findViewById(R.id.exit);
        // if button is clicked, go to browser to display content

        bExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Toast toast = Toast.makeText(activity, "Have a nice day, my wonderful friend", Toast.LENGTH_SHORT);

                // center text in toast message
                TextView tView = (TextView) toast.getView().findViewById(android.R.id.message);
                if (tView != null) tView.setGravity(Gravity.CENTER);

                toast.show();

                activity.setResult(Activity.RESULT_OK);
                dialog.dismiss();
                activity.finish();
            }
        });

        dialog.show();
    }

}
