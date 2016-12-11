package com.project.frugalmachinelearning;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.project.frugalmachinelearning.activities.ActivityType;
import com.project.frugalmachinelearning.structures.ActivityWindow;
import com.project.frugalmachinelearning.classifiers.FactoryClassifiers;
import com.project.frugalmachinelearning.tools.StringInstruments;
import com.project.frugalmachinelearning.gui.CircleMenu;
import com.project.frugalmachinelearning.classifiers.Classifiers;
import com.project.frugalmachinelearning.structures.InstanceBuilder;
import com.project.frugalmachinelearning.tools.ApplicationStates;
import com.project.frugalmachinelearning.gui.Dialogs;
import com.project.frugalmachinelearning.tools.FileOperations;
import com.project.frugalmachinelearning.gui.FlexibleActions;
import com.project.frugalmachinelearning.structures.ReadDiscretizedValues;
import com.project.frugalmachinelearning.tools.SensorsActions;
import com.project.frugalmachinelearning.structures.StorageCurrentData;
import com.project.frugalmachinelearning.structures.StorageData;
import com.project.frugalmachinelearning.tools.StorageDataActions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Discretize;

public class MainActivity extends WearableActivity implements SensorEventListener {

    /**
     * Activity identifier for the activity
     */
    public static final String TAG = "MainActivity";

    /**
     * Variables for Android standard elements
     */
    private SensorManager mSensorManager;
    private TextView mTheoreticalActivity;
    private TextView mGenericActivity;
    private ImageButton bPause;
    private ImageButton bStop;
    private AlarmManager mAmbientStateAlarmManager;
    private PendingIntent mAmbientStatePendingIntent;
    private final Handler mActiveModeUpdateHandler;

    //  Custom 'what' for Message sent to Handler.
    private static final int MSG_UPDATE_SCREEN = 0;

    // Milliseconds between updates based on state.
    private static final long ACTIVE_INTERVAL_MS = TimeUnit.SECONDS.toMillis(1);
    private static final long AMBIENT_INTERVAL_MS = TimeUnit.SECONDS.toMillis(900);

    /**
     * Weka elements
     */

    // standard filter to convert data from continuous to discrete format
    private Discretize discretizeUnit;

    // used classifier
    private AbstractClassifier selectedClassifier;

    // new instance created from updated sensor data
    private DenseInstance timeWindowInstance;

    /**
     * Variables related to converting data from raw data to Weka format
     */
    private int posInstance;
    private boolean warmingUp;
    private int NumberOfPerformingActivity;
    private boolean needDiscretizeData;
    private final String selectedClassifierName;
    private StorageCurrentData newMeasurement;
    private StorageData measurements;

    /**
     * Application control
     */
    private boolean applicationFirstRunAfterStart;
    private long activityUpdateEvent;
    private long previousBatteryUpdate;
    private static int appState;
    private boolean computeComplexFeatures;

    // variable to call the menu in the center of a screen
    private FlexibleActions leftCenterButton;

    // application uses one of two processes, one for collecting data and one for activity recognition
    private Thread t;
    private Thread tClassifyActivity;

    /**
     * Variable that are used to store data to a file
     */
    private PrintWriter printWriter;
    private boolean needTitle;

    /**
     * Main constructor
     */
    public MainActivity() {

        // choose the algorithm for recognition
        selectedClassifierName = Classifiers.ADA_BOOST_M1.getAlgName();

        // checks if input should be discretized for a classifier
        needDiscretizeData = false;

        // create title row in file with activity data
        needTitle = true;

        // controls initialization
        applicationFirstRunAfterStart = true;

        // derived measurements can be calculated only with sufficient data
        computeComplexFeatures = false;

        // ambient mode variables
        mActiveModeUpdateHandler = new UpdateHandler(this);
        initializeAmbientVariables();

        // create storage for incoming data
        newMeasurement = new StorageCurrentData();
        measurements = new StorageData(2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTheoreticalActivity = (TextView) stub.findViewById(R.id.mTheoreticalActivity);
                mGenericActivity = (TextView) stub.findViewById(R.id.mGenericActivity);
                bPause = (ImageButton) stub.findViewById(R.id.pauseActivities);
                bStop = (ImageButton) stub.findViewById(R.id.finishActivities);
                refreshDisplayAndSetNextUpdate();
            }
        });

        // activate constant visibility for the activity
        setAmbientEnabled();

        // screen on for the whole run of the application
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Log.i(TAG, "onCreate()");
    }

    /**
     * Collect data from sensors and store to a file
     */
    private void launchCollectingInformation() {
        t = getCollectionInformationThreadInstance();
        t.start();
    }

    /**
     * Process data and recognize activity
     */
    private void launchingRecognitionActivities() {
        tClassifyActivity = getRecognitionActivitiesThreadInstance();
        tClassifyActivity.start();
    }

    private void firstRunActions() {
        try {
            activityUpdateEvent = System.currentTimeMillis();

            // number for a blank activity
            NumberOfPerformingActivity = 6;

            // get the state from the intro screen
            Intent intent = getIntent();
            String stateFromIntent = intent.getStringExtra("APP STATE");
            appState = ApplicationStates.valueOf(stateFromIntent).ordinal();

            // create a name for a file based on time
            SimpleDateFormat shortName = new SimpleDateFormat("dd,HHmmss", Locale.ENGLISH);
            String fileNumber = shortName.format(new Date());

            // change an interface and prepare tools for processing activity information
            if (appState == 1) {
                final String batteryLevelName = FileOperations.getSensorStorageDir("SensorsInformation") + "/batteryInfo" + fileNumber + ".txt";
                FileOperations.deleteFile(batteryLevelName);
                final File batteryData = new File(batteryLevelName);

                previousBatteryUpdate = System.currentTimeMillis();
                printWriter = new PrintWriter(new BufferedWriter(new FileWriter(batteryData, true)));

                mGenericActivity.setVisibility(View.INVISIBLE);
                bPause.setVisibility(View.INVISIBLE);

                Log.i(TAG, batteryLevelName);
            } else {
                final String sensorDataName = FileOperations.getSensorStorageDir("SensorsInformation") + "/StorageData" + fileNumber + ".txt";
                FileOperations.deleteFile(sensorDataName);
                final File sensorData = new File(sensorDataName);

                printWriter = new PrintWriter(new BufferedWriter(new FileWriter(sensorData, true)));

                mTheoreticalActivity.setVisibility(View.INVISIBLE);
                mGenericActivity.setVisibility(View.INVISIBLE);
                bPause.setVisibility(View.INVISIBLE);

                // create a menu in the center of a screen
                CircleMenu cMenu = new CircleMenu();
                cMenu.createCircleMenu(this, bStop, bPause, mGenericActivity);
                leftCenterButton = cMenu.getLeftCenterButton();

                Log.i(TAG, sensorDataName);
            }
        } catch (IOException e) {
            Log.i(TAG, e.toString());
        }

        // prepare data if a classifier requires discrete input
        if (selectedClassifierName.equals(Classifiers.A1DE.getAlgName())) {
            needDiscretizeData = true;
            discretizeUnit = ReadDiscretizedValues.initFilter(this);
        }

        // load a previously trained classifier from a file
        FactoryClassifiers fc = new FactoryClassifiers();
        String modelFileName = fc.getModelFile(selectedClassifierName);
        InputStream ins = getResources().openRawResource(getResources().getIdentifier(modelFileName, "raw", getPackageName()));
        selectedClassifier = fc.getModel(selectedClassifierName, ins);

        // initialize variables that collect data about activities
        posInstance = 0;
        warmingUp = true;
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        SensorsActions.setSensors(this, mSensorManager);

        // create new threads
        launchCollectingInformation();
        if (appState == 1) {
            launchingRecognitionActivities();
        }

        // set to false that initialization does not another time
        applicationFirstRunAfterStart = false;

        Log.i(TAG, "First run is still active");
    }

    /**
     * Handles the button press to finish this activity and take the user back to the Home.
     */
    public void onFinishActivity(View view) {
        Dialogs.showCustomDialog(this);
    }

    /**
     * Update screen in ambient mode
     */
    private void refreshDisplayAndSetNextUpdate() {
        loadDataAndUpdateScreen();
        long timeMs = System.currentTimeMillis();

        // this condition is true when called from ambient mode and can be used with timer value
        if (isAmbient()) {
            long delayMs = AMBIENT_INTERVAL_MS - (timeMs % AMBIENT_INTERVAL_MS);
            long triggerTimeMs = timeMs + delayMs;

            mAmbientStateAlarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMs,
                    mAmbientStatePendingIntent);
        } else {
            long delayMs = ACTIVE_INTERVAL_MS - (timeMs % ACTIVE_INTERVAL_MS);
            mActiveModeUpdateHandler.removeMessages(MSG_UPDATE_SCREEN);
            mActiveModeUpdateHandler.sendEmptyMessageDelayed(MSG_UPDATE_SCREEN, delayMs);
        }
    }

    public void onWalking() {
        NumberOfPerformingActivity = ActivityType.valueOf("WALKING").ordinal();
        mGenericActivity.setText(R.string.type_walking);
        activityUpdateEvent = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;
    }

    public void onUpstairs() {
        NumberOfPerformingActivity = ActivityType.valueOf("WALKING_UPSTAIRS").ordinal();
        mGenericActivity.setText(R.string.type_upstairs);
        activityUpdateEvent = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;
    }

    public void onDownstairs() {
        NumberOfPerformingActivity = ActivityType.valueOf("WALKING_DOWNSTAIRS").ordinal();
        mGenericActivity.setText(R.string.type_downstairs);
        activityUpdateEvent = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;
    }

    public void onSitting() {
        NumberOfPerformingActivity = ActivityType.valueOf("SITTING").ordinal();
        mGenericActivity.setText(R.string.type_sitting);
        activityUpdateEvent = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;
    }

    public void onStanding() {
        NumberOfPerformingActivity = ActivityType.valueOf("STANDING").ordinal();
        mGenericActivity.setText(R.string.type_standing);
        activityUpdateEvent = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;
    }

    public void onLying() {
        NumberOfPerformingActivity = ActivityType.valueOf("LYING").ordinal();
        mGenericActivity.setText(R.string.type_lying);
        activityUpdateEvent = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;
    }

    public void onPause(View view) {
        NumberOfPerformingActivity = 6;
        mGenericActivity.setText(R.string.type_blank_activity);
        activityUpdateEvent = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;

        leftCenterButton.setVisibility(View.VISIBLE);
        bPause.setVisibility(View.INVISIBLE);
        bStop.setVisibility(View.VISIBLE);
    }

    private void initializeAmbientVariables() {
        mAmbientStateAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent ambientStateIntent = new Intent(getApplicationContext(), MainActivity.class);

        mAmbientStatePendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0 /* requestCode */,
                ambientStateIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void doExit() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.setNegativeButton("No", null);
        alertDialog.setMessage("Do you want to exit?");
        alertDialog.setTitle("AppTitle");
        alertDialog.show();
    }

    public void setLeftCenterButton(FlexibleActions leftCenterButton) {
        this.leftCenterButton = leftCenterButton;
    }

    private Thread getRecognitionActivitiesThreadInstance() {
        Thread recognitionActivities = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                ArrayList<Attribute> attributes = InstanceBuilder.getNewAttributes();
                                DenseInstance[] instances = {timeWindowInstance};
                                Instances data = ActivityWindow.constructInstances(attributes, instances);

                                String activityFullName = StringInstruments.recognizeActivity(data, needDiscretizeData, discretizeUnit, selectedClassifier);

                                long currentTimeMs = System.currentTimeMillis();
                                if (currentTimeMs - previousBatteryUpdate > 29555) {
                                    String bInfoString = StringInstruments.getStringForActivityRecognitionFile(getApplicationContext(), currentTimeMs,
                                            selectedClassifier);

                                    printWriter.println(bInfoString);
                                    printWriter.flush();

                                    previousBatteryUpdate = currentTimeMs;

                                    Log.i(TAG, bInfoString);
                                }

                                String label = getString(R.string.activity_name) + activityFullName;
                                mTheoreticalActivity.setText(label);

                                Log.i(TAG, activityFullName);
                            }

                        });
                    }
                } catch (InterruptedException e) {
                    Log.i(TAG, e.toString());
                }
            }
        };

        return recognitionActivities;
    }

    private Thread getCollectionInformationThreadInstance() {
        Thread collectionInformation = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000 / StorageData.UPDATES_PER_SECOND);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (FileOperations.isExternalStorageWritable()) {
                                        if (needTitle && appState == 0) {
                                            printWriter.println(StringInstruments.createTitle());
                                            needTitle = false;
                                        }

                                        // computing features
                                        StorageDataActions.sensorsFeaturesToArrays(measurements, newMeasurement, posInstance);
                                        if (computeComplexFeatures) {
                                            StorageDataActions.computeAdditionalFeatures(measurements);
                                        }

                                        if (appState == 0) {
                                            if (computeComplexFeatures && NumberOfPerformingActivity != 6) {
                                                StringInstruments.writeNewActivityDataToFile(activityUpdateEvent, posInstance,
                                                        printWriter, NumberOfPerformingActivity, measurements);
                                            }
                                        } else {
                                            timeWindowInstance = InstanceBuilder.getInstance(StorageData.AMOUNT_OF_ATTRIBUTES * 5 + 1, computeComplexFeatures,
                                                    measurements, appState);
                                        }

                                        posInstance++;
                                        if (posInstance == measurements.windowTime * StorageData.UPDATES_PER_SECOND) {
                                            posInstance = 0;
                                            computeComplexFeatures = true;
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.i(TAG, e.toString());
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        };

        return collectionInformation;
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        Log.i(TAG, "onAccuracyChanged()");
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        Log.d(TAG, "onNewIntent(): " + intent);

        // Described in the following section
        refreshDisplayAndSetNextUpdate();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        StorageDataActions.updateSensorData(event, newMeasurement);

        if (warmingUp) {
            newMeasurement.heartRateVal = 0.0f;
            warmingUp = false;
        }
    }

    @Override
    public void onBackPressed() {
        doExit();
    }


    @Override
    protected void onDestroy() {
        if (t != null) {
            t.interrupt();

        }
        if (tClassifyActivity != null) {
            tClassifyActivity.interrupt();
        }

        printWriter.close();

        mSensorManager = null;
        mActiveModeUpdateHandler.removeMessages(MSG_UPDATE_SCREEN);
        mAmbientStateAlarmManager.cancel(mAmbientStatePendingIntent);

        super.onDestroy();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);

        /**
         * In this sample, we aren't using the ambient details bundle (EXTRA_BURN_IN_PROTECTION or
         * EXTRA_LOWBIT_AMBIENT), but if you need them, you can pull them from the local variable
         * set here.
         */

        // Tracks latest ambient details, such as burnin offsets, etc.
        Bundle mAmbientDetails = ambientDetails;

        /** Clears Handler queue (only needed for updates in active mode). */
        mActiveModeUpdateHandler.removeMessages(MSG_UPDATE_SCREEN);

        refreshDisplayAndSetNextUpdate();
        Log.d(TAG, "onEnterAmbient()");

        onExitAmbient();
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();

        mAmbientStateAlarmManager.cancel(mAmbientStatePendingIntent);
        Log.d(TAG, "onExitAmbient()");

        refreshDisplayAndSetNextUpdate();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();

        refreshDisplayAndSetNextUpdate();
    }

    /**
     * Updates display based on Ambient state. If you need to pull data, you should do it here.
     */
    private void loadDataAndUpdateScreen() {
        long currentTimeMs = System.currentTimeMillis();
        if (applicationFirstRunAfterStart) {
            firstRunActions();
        }
        Log.d(TAG, "loadDataAndUpdateScreen(): " + currentTimeMs + "(" + isAmbient() + ")");
    }

    /**
     * Handler separated into static class to avoid memory leaks.
     */
    private static class UpdateHandler extends Handler {

        private final WeakReference<MainActivity> mMainActivityWeakReference;

        public UpdateHandler(MainActivity reference) {
            mMainActivityWeakReference = new WeakReference<MainActivity>(reference);
        }

        @Override
        public void handleMessage(Message message) {
            MainActivity mainActivity = mMainActivityWeakReference.get();

            if (mainActivity != null) {
                switch (message.what) {
                    case MSG_UPDATE_SCREEN:
                        mainActivity.refreshDisplayAndSetNextUpdate();
                        break;
                }
            }
        }

    }
}

