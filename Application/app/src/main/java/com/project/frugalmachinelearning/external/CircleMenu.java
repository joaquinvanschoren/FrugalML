package com.project.frugalmachinelearning.external;

import android.content.res.Resources;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.project.frugalmachinelearning.MainActivity;
import com.project.frugalmachinelearning.R;
import com.project.frugalmachinelearning.tools.FloatingActionButtonFlexibleActions;

/**
 * Created by Mikhail on 20.11.2016.
 * <p/>
 * Credits. Menu in this project uses informative example https://github.com/oguzbilgener/CircularFloatingActionMenu created by Oğuz Bilgener.
 */
public class CircleMenu {

    FloatingActionButtonFlexibleActions leftCenterButton;

    public void createCircleMenu(final MainActivity activity, final ImageButton bStop, final ImageButton bPause, final TextView mGenericActivity) {

        Resources resources = activity.getResources();

        // Set up the large red button on the center right side
        // With custom button and content sizes and margins
        int redActionButtonSize = resources.getDimensionPixelSize(R.dimen.red_action_button_size);
        int redActionButtonMargin = resources.getDimensionPixelOffset(R.dimen.action_button_margin);
        int redActionButtonContentSize = resources.getDimensionPixelSize(R.dimen.red_action_button_content_size);
        int redActionButtonContentMargin = resources.getDimensionPixelSize(R.dimen.red_action_button_content_margin);
        int redActionMenuRadius = resources.getDimensionPixelSize(R.dimen.red_action_menu_radius);
        int blueSubActionButtonSize = resources.getDimensionPixelSize(R.dimen.blue_sub_action_button_size);
        int blueSubActionButtonContentMargin = resources.getDimensionPixelSize(R.dimen.blue_sub_action_button_content_margin);

        ImageView fabIconStar = new ImageView(activity);
        fabIconStar.setImageDrawable(resources.getDrawable(R.drawable.ic_touch_app_white_24dp));

        FloatingActionButton.LayoutParams starParams = new FloatingActionButton.LayoutParams(redActionButtonSize, redActionButtonSize);
        starParams.setMargins(redActionButtonMargin,
                redActionButtonMargin,
                redActionButtonMargin,
                redActionButtonMargin);
        fabIconStar.setLayoutParams(starParams);

        FloatingActionButton.LayoutParams fabIconStarParams = new FloatingActionButton.LayoutParams(redActionButtonContentSize,
                redActionButtonContentSize);
        fabIconStarParams.setMargins(redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin);

        leftCenterButton = new FloatingActionButtonFlexibleActions.Builder(activity)
                .setContentView(fabIconStar, fabIconStarParams)
                .setBackgroundDrawable(R.drawable.button_action_red_selector)
                .setPosition(FloatingActionButtonFlexibleActions.POSITION_CENTER)
                .setLayoutParams(starParams)
                .build();

        activity.setLeftCenterButton(leftCenterButton);

        final GestureDetector gestureDetector = new GestureDetector(activity, new MyGestureDetector(bStop, mGenericActivity, activity));

        leftCenterButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return gestureDetector.onTouchEvent(event);

            }
        });

        // Set up customized SubActionButtons for the right center menu
        SubActionButton.Builder lCSubBuilder = new SubActionButton.Builder(activity);
        lCSubBuilder.setBackgroundDrawable(resources.getDrawable(R.drawable.button_action_blue_selector));

        FrameLayout.LayoutParams blueContentParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        blueContentParams.setMargins(blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin);
        lCSubBuilder.setLayoutParams(blueContentParams);
        // Set custom layout params
        FrameLayout.LayoutParams blueParams = new FrameLayout.LayoutParams(blueSubActionButtonSize, blueSubActionButtonSize);
        lCSubBuilder.setLayoutParams(blueParams);

        ImageView lcIcon1 = new ImageView(activity);
        ImageView lcIcon2 = new ImageView(activity);
        ImageView lcIcon3 = new ImageView(activity);
        ImageView lcIcon4 = new ImageView(activity);
        ImageView lcIcon5 = new ImageView(activity);
        ImageView lcIcon6 = new ImageView(activity);

        lcIcon1.setImageDrawable(resources.getDrawable(R.drawable.ic_directions_walk_white_24dp));
        lcIcon2.setImageDrawable(resources.getDrawable(R.drawable.ic_trending_up_white_24dp));
        lcIcon3.setImageDrawable(resources.getDrawable(R.drawable.ic_trending_down_white_24dp));
        lcIcon4.setImageDrawable(resources.getDrawable(R.drawable.ic_airline_seat_recline_normal_white_24dp));
        lcIcon5.setImageDrawable(resources.getDrawable(R.drawable.ic_accessibility_white_24dp));
        lcIcon6.setImageDrawable(resources.getDrawable(R.drawable.ic_local_hotel_white_24dp));

        SubActionButton buttonOnWalking = lCSubBuilder.setContentView(lcIcon1, blueContentParams).build();
        buttonOnWalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftCenterButton.performClick();

                bPause.setVisibility(View.VISIBLE);

                mGenericActivity.setVisibility(View.VISIBLE);

                leftCenterButton.setVisibility(View.INVISIBLE);

                activity.onWalking();
                Toast toast = Toast.makeText(activity, "Walking", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        SubActionButton buttonOnWalkingUpstairs = lCSubBuilder.setContentView(lcIcon2, blueContentParams).build();
        buttonOnWalkingUpstairs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftCenterButton.performClick();

                bPause.setVisibility(View.VISIBLE);

                mGenericActivity.setVisibility(View.VISIBLE);

                leftCenterButton.setVisibility(View.INVISIBLE);

                activity.onUpstairs();
                Toast toast = Toast.makeText(activity, "Walking upstairs", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        SubActionButton buttonOnWalkingDownstairs = lCSubBuilder.setContentView(lcIcon3, blueContentParams).build();
        buttonOnWalkingDownstairs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftCenterButton.performClick();

                bPause.setVisibility(View.VISIBLE);

                mGenericActivity.setVisibility(View.VISIBLE);

                leftCenterButton.setVisibility(View.INVISIBLE);

                activity.onDownstairs();
                Toast toast = Toast.makeText(activity, "Waking downstairs", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        SubActionButton buttonOnSitting = lCSubBuilder.setContentView(lcIcon4, blueContentParams).build();
        buttonOnSitting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftCenterButton.performClick();

                bPause.setVisibility(View.VISIBLE);

                mGenericActivity.setVisibility(View.VISIBLE);

                leftCenterButton.setVisibility(View.INVISIBLE);

                activity.onSitting();
                Toast toast = Toast.makeText(activity, "Sitting", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        SubActionButton buttonOnStanding = lCSubBuilder.setContentView(lcIcon5, blueContentParams).build();
        buttonOnStanding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftCenterButton.performClick();

                bPause.setVisibility(View.VISIBLE);
                bStop.setVisibility(View.VISIBLE);
                mGenericActivity.setVisibility(View.VISIBLE);

                leftCenterButton.setVisibility(View.INVISIBLE);

                activity.onStanding();
                Toast toast = Toast.makeText(activity, "Standing", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        SubActionButton buttonOnLying = lCSubBuilder.setContentView(lcIcon6, blueContentParams).build();
        buttonOnLying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftCenterButton.performClick();

                bPause.setVisibility(View.VISIBLE);

                mGenericActivity.setVisibility(View.VISIBLE);

                leftCenterButton.setVisibility(View.INVISIBLE);

                activity.onLying();
                Toast toast = Toast.makeText(activity, "Lying", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        // Build another menu with custom options
        final FloatingActionMenu leftCenterMenu = new FloatingActionMenu.Builder(activity)
                .addSubActionView(buttonOnWalking)
                .addSubActionView(buttonOnWalkingUpstairs)
                .addSubActionView(buttonOnWalkingDownstairs)
                .addSubActionView(buttonOnSitting)
                .addSubActionView(buttonOnStanding)
                .addSubActionView(buttonOnLying)
                .setRadius(redActionMenuRadius)
                .setStartAngle(0)
                .setEndAngle(360)
                .attachTo(leftCenterButton)
                .build();

    }

    public FloatingActionButtonFlexibleActions getLeftCenterButton() {
        return leftCenterButton;
    }
}
