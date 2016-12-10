package com.project.frugalmachinelearning;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project.frugalmachinelearning.classifiers.ActivityWindow;
import com.project.frugalmachinelearning.classifiers.FactoryClassifiers;
import com.project.frugalmachinelearning.external.CircleMenu;
import com.project.frugalmachinelearning.external.Classifiers;
import com.project.frugalmachinelearning.external.InstanceBuilder;
import com.project.frugalmachinelearning.tools.ApplicationStates;
import com.project.frugalmachinelearning.tools.Dialogs;
import com.project.frugalmachinelearning.tools.FileOperations;
import com.project.frugalmachinelearning.tools.FloatingActionButtonFlexibleActions;
import com.project.frugalmachinelearning.external.ReadDiscretizedValues;
import com.project.frugalmachinelearning.tools.SensorsActions;
import com.project.frugalmachinelearning.tools.StorageCurrentData;
import com.project.frugalmachinelearning.tools.StorageData;
import com.project.frugalmachinelearning.tools.StorageDataActions;
import com.project.frugalmachinelearning.tools.TextFileActions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

import static com.project.frugalmachinelearning.tools.StorageData.AMOUNT_OF_ATTRIBUTES;

public class MainActivity extends WearableActivity implements SensorEventListener {

    public static final String TAG = "MainActivity";

    private SensorManager mSensorManager;

    private Thread t;
    private Thread tClassifyActivity;

    private boolean needTitle;

    private AbstractClassifier selectedClassifier;

    private DenseInstance timeWindowInstance;

    private int posInstance;
    private boolean warmingUp;
    private int performingActivity;

    private TextView mTheoreticalActivity;
    private TextView mGenericActivity;

    private ImageButton bPause;

    private ImageButton bStop;

    private RelativeLayout fBackground;

    private PrintWriter pw;
    private final DateFormat df;

    private boolean firstRun;

    private long timeChangeActivityUpdateMs;

    private long previousBatteryUpdate;

    private long pTime;
    private long nTime;

    private FloatingActionButtonFlexibleActions leftCenterButton;

    private static int appState;
    private boolean computeComplexFeatures;

    /**
     * Custom 'what' for Message sent to Handler.
     */
    private static final int MSG_UPDATE_SCREEN = 0;

    /**
     * Milliseconds between updates based on state.
     */
    private static final long ACTIVE_INTERVAL_MS = TimeUnit.SECONDS.toMillis(1);
    private static final long AMBIENT_INTERVAL_MS = TimeUnit.SECONDS.toMillis(900);

    private final Handler mActiveModeUpdateHandler = new UpdateHandler(this);

    private boolean discretizeData = false;

    private Discretize discretizeItems;

    private AlarmManager mAmbientStateAlarmManager;
    private PendingIntent mAmbientStatePendingIntent;

    private StorageCurrentData newMeasurement;
    private StorageData measurements;
    private final String selectedClassifierName;

    public MainActivity() {

        // choose the algorithm for recognition
        selectedClassifierName = Classifiers.ADA_BOOST_M1.getAlgName();

        needTitle = true;

        df = new SimpleDateFormat("HH:mm:ss.SSS dd/MM/yyyy");

        firstRun = true;

        computeComplexFeatures = false;
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

                fBackground = (RelativeLayout) stub.findViewById(R.id.mBackRelativeLayout);

                refreshDisplayAndSetNextUpdate();
            }
        });

        // activate constant visibility for the activity
        setAmbientEnabled();

        // screen on for the whole run of the application
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initializeAmbientVariables();

        newMeasurement = new StorageCurrentData();
        measurements = new StorageData(2);

        Log.i(TAG, "onCreate()");
    }

    private void launchCollectingInformation() {
        t = getCollectionInformationThreadInstance();

        t.start();
    }

    private void launchingRecognitionActivities() {
        tClassifyActivity = getRecognitionActivitiesThreadInstance();

        tClassifyActivity.start();
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
                                ArrayList<Attribute> attributes = getNewAttributes();
                                DenseInstance[] instances = {timeWindowInstance};
                                Instances data = ActivityWindow.constructInstances(attributes, instances);

                                String activityFullName = getActivityName(data);

                                long currentTimeMs = System.currentTimeMillis();
                                if (currentTimeMs - previousBatteryUpdate > 29555) {
                                    String bInfoString = getStringForActivityRecognitionFIle(currentTimeMs);

                                    pw.println(bInfoString);
                                    pw.flush();

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

    private String getStringForActivityRecognitionFIle(long currentTimeMs) {
        StringBuilder bInfo = new StringBuilder();
        bInfo.append(currentTimeMs).append(",");
        bInfo.append(selectedClassifier.getClass()).append(",");
        bInfo.append(getBatteryLevel());
        return bInfo.toString();
    }

    private String getActivityName(Instances data) {

        String activityFullName = "";

        if (discretizeData && discretizeItems != null) {
            try {
                Instances discretData = Filter.useFilter(data, discretizeItems);
                activityFullName = ActivityWindow.getActivityName(selectedClassifier, discretData);
            } catch (Exception e) {
                Log.i(TAG, e.toString());
            }
        } else if (!discretizeData) {
            activityFullName = ActivityWindow.getActivityName(selectedClassifier, data);
        } else {
            activityFullName = "BEING COMPUTED";
        }
        return activityFullName;
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
                                            pw.println(TextFileActions.createTitle());
                                            needTitle = false;
                                        }

                                        // computing features
                                        StorageDataActions.sensorsFeaturesToArrays(measurements, newMeasurement, posInstance);
                                        if (computeComplexFeatures) {
                                            StorageDataActions.computeAdditionalFeatures(measurements);
                                        }

                                        if (appState == 0) {
                                            if (computeComplexFeatures && performingActivity != 6) {
                                                writeNewActivityDataToFile();
                                            }
                                        } else {
                                            timeWindowInstance = InstanceBuilder.getInstance(AMOUNT_OF_ATTRIBUTES * 5 + 1, computeComplexFeatures, measurements, appState);
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

    private void writeNewActivityDataToFile() {
        // pause data collection after activity change for a short period and wait for computed values
        long currentTimeMs = System.currentTimeMillis();
        if (currentTimeMs - timeChangeActivityUpdateMs >= 5000) {
            StringBuilder newInfo = arraysToString(posInstance);
            pw.println(newInfo);
        }
    }

    @Override
    protected void onDestroy() {

        if (t != null) {
            t.interrupt();

        }
        if (tClassifyActivity != null) {
            tClassifyActivity.interrupt();
        }

        pw.close();

        mSensorManager = null;

        mActiveModeUpdateHandler.removeMessages(MSG_UPDATE_SCREEN);
        mAmbientStateAlarmManager.cancel(mAmbientStatePendingIntent);

        super.onDestroy();
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        Log.i(TAG, "onAccuracyChanged()");
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
        Log.d(TAG, "loadDataAndUpdateScreen(): " + currentTimeMs + "(" + isAmbient() + ")");

        Date current = new Date();

        // mTime.setText(new SimpleDateFormat("HH:mm:ss.SSS").format(current) + " update time");

        if (firstRun) {
            firstRunActions();
        }

        if (isAmbient()) {

        } else {

        }
    }

    private void firstRunActions() {
        try {
            timeChangeActivityUpdateMs = System.currentTimeMillis();

            performingActivity = 6;


            Intent intent = getIntent();
            String stateFromIntent = intent.getStringExtra("APP STATE");
            appState = ApplicationStates.valueOf(stateFromIntent).ordinal();

            SimpleDateFormat shortName = new SimpleDateFormat("dd,HHmmss");
            String fileNumber = shortName.format(new Date());

            // change an interface and prepare tools for the assignment
            if (appState == 1) {

                final String batteryLevelName = FileOperations.getSensorStorageDir("SensorsInformation") + "/batteryInfo" + fileNumber + ".txt";
                FileOperations.deleteFile(batteryLevelName);
                final File batteryData = new File(batteryLevelName);

                previousBatteryUpdate = System.currentTimeMillis();

                pw = new PrintWriter(new BufferedWriter(new FileWriter(batteryData, true)));

                mGenericActivity.setVisibility(View.INVISIBLE);

                bPause.setVisibility(View.INVISIBLE);

                Log.i(TAG, batteryLevelName);

            } else {

                CircleMenu cMenu = new CircleMenu();
                cMenu.createCircleMenu(this, bStop, bPause, mGenericActivity);
                leftCenterButton = cMenu.getLeftCenterButton();

                final String sensorDataName = FileOperations.getSensorStorageDir("SensorsInformation") + "/StorageData" + fileNumber + ".txt";
                FileOperations.deleteFile(sensorDataName);
                final File sensorData = new File(sensorDataName);

                pw = new PrintWriter(new BufferedWriter(new FileWriter(sensorData, true)));

                mTheoreticalActivity.setVisibility(View.INVISIBLE);
                mGenericActivity.setVisibility(View.INVISIBLE);

                bPause.setVisibility(View.INVISIBLE);

                Log.i(TAG, sensorDataName);

            }

        } catch (IOException e) {
            Log.i(TAG, e.toString());
        }

        // clean old files with random results
        FileOperations.deleteFile("/storage/emulated/0/myfile_nbp.txt");
        FileOperations.deleteFile("myfile_nbp.txt");

        // create classifier from a file
        if (selectedClassifierName.equals(Classifiers.A1DE.getAlgName())) {
            discretizeData = true;
            discretizeItems = ReadDiscretizedValues.initFilter(this);
        }
        FactoryClassifiers fc = new FactoryClassifiers();
        String modelFileName = fc.getModelFile(selectedClassifierName);
        InputStream ins = getResources().openRawResource(getResources().getIdentifier(modelFileName, "raw", getPackageName()));

        selectedClassifier = fc.getModel(selectedClassifierName, ins);

        posInstance = 0;
        warmingUp = true;
        setSensors();

        if (appState == 0) {
            launchCollectingInformation();
        } else {
            launchCollectingInformation();
            launchingRecognitionActivities();
        }

        firstRun = false;

        Log.i(TAG, "First run is still active");
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        Log.d(TAG, "onNewIntent(): " + intent);

        // Described in the following section
        refreshDisplayAndSetNextUpdate();
    }

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

    private void setSensors() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        SensorsActions.setSensors(this, mSensorManager);
    }

    private void checkTimeDifference() {
        long currentTime = System.currentTimeMillis();

        if (pTime == 0) {
            pTime = currentTime;
        } else {
            pTime = nTime;
            nTime = currentTime;

            long difference = nTime - pTime;

            if (difference > 100) {
                Log.i(TAG, String.valueOf(nTime - pTime));
            }
        }
    }

    private StringBuilder arraysToString(int pos) {
        checkTimeDifference();
        StringBuilder allSensorsData = StorageDataActions.arraysToString(pos, performingActivity, measurements);
        return allSensorsData;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        StorageDataActions.updateSensorData(event, newMeasurement);

        if (warmingUp) {
            newMeasurement.heartRateVal = 0.0f;
            warmingUp = false;
        }
    }

    /**
     * Handles the button press to finish this activity and take the user back to the Home.
     */
    public void onFinishActivity(View view) {
        Dialogs.showCustomDialog(this);
    }

    public void onWalking() {
        performingActivity = 0;                                                      // ActivityType.valueOf("WALKING").ordinal();
        mGenericActivity.setText("Walking");
        timeChangeActivityUpdateMs = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;
    }

    public void onUpstairs() {
        performingActivity = 1;                                                      // ActivityType.valueOf("WALKING_UPSTAIRS").ordinal();
        mGenericActivity.setText("Upstairs");
        timeChangeActivityUpdateMs = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;
    }

    public void onDownstairs() {
        performingActivity = 2;                                                      // ActivityType.valueOf("WALKING_DOWNSTAIRS").ordinal();
        mGenericActivity.setText("Downstairs");
        timeChangeActivityUpdateMs = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;
    }

    public void onSitting() {
        performingActivity = 3;                                                      // ActivityType.valueOf("SITTING").ordinal();
        mGenericActivity.setText("Sitting");
        timeChangeActivityUpdateMs = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;
    }

    public void onStanding() {
        performingActivity = 4;                                                      // ActivityType.valueOf("STANDING").ordinal();
        mGenericActivity.setText("Standing");
        timeChangeActivityUpdateMs = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;
    }

    public void onLying() {
        performingActivity = 5;                                                      // ActivityType.valueOf("LYING").ordinal();
        mGenericActivity.setText("Lying");
        timeChangeActivityUpdateMs = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;
    }

    public void onPause(View view) {
        performingActivity = 6;
        mGenericActivity.setText("Pause");
        timeChangeActivityUpdateMs = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;

        leftCenterButton.setVisibility(View.VISIBLE);
        bPause.setVisibility(View.INVISIBLE);
        bStop.setVisibility(View.VISIBLE);
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

    public ArrayList<Attribute> getNewAttributes() {
        return InstanceBuilder.getNewAttributes();
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

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);

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

    public void setLeftCenterButton(FloatingActionButtonFlexibleActions leftCenterButton) {
        this.leftCenterButton = leftCenterButton;
    }

    @Override
    public void onBackPressed() {
        doExit();
    }

    public float getBatteryLevel() {
        Intent batteryIntent = getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if (level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float) level / (float) scale) * 100.0f;
    }


}
