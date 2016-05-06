package com.project.frugalmachinelearning;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.project.frugalmachinelearning.classifiers.ActivityType;
import com.project.frugalmachinelearning.classifiers.ActivityWindow;
import com.project.frugalmachinelearning.classifiers.FactoryClassifiers;
import com.project.frugalmachinelearning.tools.ApplicationStates;
import com.project.frugalmachinelearning.tools.FileOperations;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";
    private static final int AMOUNT_OF_ATTRIBUTES = 23;

    private static final int UPDATES_PER_SECOND = 16;

    private static final int FRAME_SIZE = UPDATES_PER_SECOND * 2;

    private SensorManager mSensorManager;
    private Map<String, Float> mResults = new LinkedHashMap<String, Float>();

    private Thread t;
    private Thread tClassifyActivity;

    private boolean needTitle = true;

    private AbstractClassifier selectedClassifier;
    private DenseInstance[] instances = new DenseInstance[FRAME_SIZE];
    private int posInstance;
    private boolean warmingUp;
    private int performingActivity;

    private TextView mActivityTextView;
    private TextView mTime;
    private TextView mNewActivity;

    private Button bWalking;
    private Button bWalkingUpstairs;
    private Button bWalkingDownstairs;
    private Button bSitting;
    private Button bStanding;
    private Button bLaying;
    private Button bEmpty;

    private PrintWriter pw;
    private DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS dd/MM/yyyy");

    private boolean firstRun = true;

    private long timeChangeActivityUpdateMs;

    private float[] accelerometerFilter = new float[3];

    private double[] accelXArray = new double[FRAME_SIZE];
    private double[] accelYArray = new double[FRAME_SIZE];
    private double[] accelZArray = new double[FRAME_SIZE];
    private double[] accelXMean = new double[FRAME_SIZE];
    private double[] accelYMean = new double[FRAME_SIZE];
    private double[] accelZMean = new double[FRAME_SIZE];
    private double[] accelXStd = new double[FRAME_SIZE];
    private double[] accelYStd = new double[FRAME_SIZE];
    private double[] accelZStd = new double[FRAME_SIZE];

    private double[] gyroXArray = new double[FRAME_SIZE];
    private double[] gyroYArray = new double[FRAME_SIZE];
    private double[] gyroZArray = new double[FRAME_SIZE];
    private double[] gyroXMean = new double[FRAME_SIZE];
    private double[] gyroYMean = new double[FRAME_SIZE];
    private double[] gyroZMean = new double[FRAME_SIZE];
    private double[] gyroXStd = new double[FRAME_SIZE];
    private double[] gyroYStd = new double[FRAME_SIZE];
    private double[] gyroZStd = new double[FRAME_SIZE];

    private double[] gravityXArray = new double[FRAME_SIZE];
    private double[] gravityYArray = new double[FRAME_SIZE];
    private double[] gravityZArray = new double[FRAME_SIZE];
    private double[] gravityXMean = new double[FRAME_SIZE];
    private double[] gravityYMean = new double[FRAME_SIZE];
    private double[] gravityZMean = new double[FRAME_SIZE];
    private double[] gravityXStd = new double[FRAME_SIZE];
    private double[] gravityYStd = new double[FRAME_SIZE];
    private double[] gravityZStd = new double[FRAME_SIZE];

    private double[] linAccelXArray = new double[FRAME_SIZE];
    private double[] linAccelYArray = new double[FRAME_SIZE];
    private double[] linAccelZArray = new double[FRAME_SIZE];
    private double[] linAccelXMean = new double[FRAME_SIZE];
    private double[] linAccelYMean = new double[FRAME_SIZE];
    private double[] linAccelZMean = new double[FRAME_SIZE];
    private double[] linAccelXStd = new double[FRAME_SIZE];
    private double[] linAccelYStd = new double[FRAME_SIZE];
    private double[] linAccelZStd = new double[FRAME_SIZE];

    private double[] rotVecXArray = new double[FRAME_SIZE];
    private double[] rotVecYArray = new double[FRAME_SIZE];
    private double[] rotVecZArray = new double[FRAME_SIZE];
    private double[] rotVecSArray = new double[FRAME_SIZE];
    private double[] rotVecXMean = new double[FRAME_SIZE];
    private double[] rotVecYMean = new double[FRAME_SIZE];
    private double[] rotVecZMean = new double[FRAME_SIZE];
    private double[] rotVecSMean = new double[FRAME_SIZE];
    private double[] rotVecXStd = new double[FRAME_SIZE];
    private double[] rotVecYStd = new double[FRAME_SIZE];
    private double[] rotVecZStd = new double[FRAME_SIZE];
    private double[] rotVecSStd = new double[FRAME_SIZE];

    private double[] stDetValArray = new double[FRAME_SIZE];

    private double[] aiPreValArray = new double[FRAME_SIZE];
    private double[] aiPreValMean = new double[FRAME_SIZE];
    private double[] aiPreValStd = new double[FRAME_SIZE];

    private double[] magFielXArray = new double[FRAME_SIZE];
    private double[] magFielYArray = new double[FRAME_SIZE];
    private double[] magFielZArray = new double[FRAME_SIZE];
    private double[] magFielXMean = new double[FRAME_SIZE];
    private double[] magFielYMean = new double[FRAME_SIZE];
    private double[] magFielZMean = new double[FRAME_SIZE];
    private double[] magFielXStd = new double[FRAME_SIZE];
    private double[] magFielYStd = new double[FRAME_SIZE];
    private double[] magFielZStd = new double[FRAME_SIZE];

    private double[] heartRateValArray = new double[FRAME_SIZE];
    private double[] heartRateValMean = new double[FRAME_SIZE];
    private double[] heartRateValStd = new double[FRAME_SIZE];


    StandardDeviation stDev = new StandardDeviation();
    Mean mean = new Mean();

    private static final int APP_STATE = ApplicationStates.valueOf("COLLECT_DATA").ordinal();
    boolean computeComplexFeatures = false;


    /** Custom 'what' for Message sent to Handler. */
    private static final int MSG_UPDATE_SCREEN = 0;

    /** Milliseconds between updates based on state. */
    private static final long ACTIVE_INTERVAL_MS = TimeUnit.SECONDS.toMillis(1);
    private static final long AMBIENT_INTERVAL_MS = TimeUnit.SECONDS.toMillis(900);

    /** Tracks latest ambient details, such as burnin offsets, etc. */
    private Bundle mAmbientDetails;

    private final Handler mActiveModeUpdateHandler = new UpdateHandler(this);

    private volatile int mDrawCount = 0;

    private AlarmManager mAmbientStateAlarmManager;
    private PendingIntent mAmbientStatePendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // activate constant visibility for the activity
        setAmbientEnabled();

        mAmbientStateAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent ambientStateIntent = new Intent(getApplicationContext(), MainActivity.class);

        mAmbientStatePendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0 /* requestCode */,
                ambientStateIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mActivityTextView = (TextView) stub.findViewById(R.id.mActivityTextView);
                mTime = (TextView) stub.findViewById(R.id.mTime);
                mNewActivity = (TextView) stub.findViewById(R.id.mNewActivity);

                bWalking = (Button) stub.findViewById(R.id.button);
                bWalkingUpstairs = (Button) stub.findViewById(R.id.button2);
                bWalkingDownstairs = (Button) stub.findViewById(R.id.button3);
                bSitting = (Button) stub.findViewById(R.id.button4);
                bStanding = (Button) stub.findViewById(R.id.button5);
                bLaying = (Button) stub.findViewById(R.id.button6);
                bEmpty = (Button) stub.findViewById(R.id.button7);

                refreshDisplayAndSetNextUpdate();
            }
        });

        Log.i(TAG, "onCreate()");
    }

    private void launchCollectingInformation() {
        t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000 / UPDATES_PER_SECOND);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (isExternalStorageWritable() && mResults.size() >= AMOUNT_OF_ATTRIBUTES - 1) {

                                        if (needTitle) {
                                            pw.println(createTitle());
                                            needTitle = false;
                                        }

                                        // computing features
                                        sensorsAndComplexFeaturesToArrays(posInstance);

                                        if (APP_STATE == 0) {
                                            if (computeComplexFeatures) {

                                                // convert all information to string
                                                String allSensorsData = arraysToString(posInstance);

                                                // pause data collection after activity change for a short period
                                                long currentTimeMs = System.currentTimeMillis();
                                                if (currentTimeMs - timeChangeActivityUpdateMs >= 5000) {
                                                    pw.println(allSensorsData);
                                                }
                                            }
                                        } else {
                                            DenseInstance instance = getDenseInstances(AMOUNT_OF_ATTRIBUTES * 3 - 4, posInstance);
                                            instances[posInstance] = instance;
                                        }

                                        posInstance++;
                                        if (posInstance == 2 * UPDATES_PER_SECOND) {
                                            posInstance = 0;
                                            computeComplexFeatures = true;
                                        }


                                        // adjust visual style
                                        Button empButton = (Button) findViewById(R.id.button7);
                                        if (empButton.getVisibility() != View.VISIBLE) {
                                            mTime.setTextSize(10);
                                            mNewActivity.setVisibility(View.INVISIBLE);
                                        } else {
                                            mActivityTextView.setVisibility(View.INVISIBLE);
                                        }

                                        Log.d(TAG, "Continue working");

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {

                }
            }
        };

        t.start();
    }

    private void launchingRecognitionActivities() {
        tClassifyActivity = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                ArrayList<Attribute> attributes = getNewAttributes();
                                Instances data = ActivityWindow.constructInstances(attributes, instances);
                                String activityFullName = ActivityWindow.getActivityName(selectedClassifier, data);

                                Button empButton = (Button) findViewById(R.id.button7);
                                if (empButton.getVisibility() != View.VISIBLE) {
                                    mActivityTextView.setText("Activity is " + activityFullName + " ambient is " + isAmbient());
                                    mActivityTextView.setTextSize(12);
                                    mTime.setTextSize(10);
                                    mNewActivity.setVisibility(View.INVISIBLE);
                                } else {
                                    mActivityTextView.setText("is " + activityFullName);

                                }

                                Log.i(TAG, String.valueOf(ActivityType.valueOf(activityFullName)));
                            }

                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        tClassifyActivity.start();
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
        mAmbientDetails = ambientDetails;

        /** Clears Handler queue (only needed for updates in active mode). */
        mActiveModeUpdateHandler.removeMessages(MSG_UPDATE_SCREEN);

        refreshDisplayAndSetNextUpdate();
        Log.d(TAG, "onEnterAmbient()");

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

        mDrawCount += 1;
        long currentTimeMs = System.currentTimeMillis();
        Log.d(TAG, "loadDataAndUpdateScreen(): " + currentTimeMs + "(" + isAmbient() + ")");

        Date current = new Date();
        mTime.setText(new SimpleDateFormat("HH:mm:ss.SSS").format(current) + " update time");

        if (firstRun) {
            try {
                timeChangeActivityUpdateMs = System.currentTimeMillis();

                if (performingActivity == 6) {
                    mNewActivity.setText("New activity is empty");
                }

                if (APP_STATE == 1) {
                    bWalking.setVisibility(View.INVISIBLE);
                    bWalkingUpstairs.setVisibility(View.INVISIBLE);
                    bWalkingDownstairs.setVisibility(View.INVISIBLE);
                    bSitting.setVisibility(View.INVISIBLE);
                    bStanding.setVisibility(View.INVISIBLE);
                    bLaying.setVisibility(View.INVISIBLE);
                    bEmpty.setVisibility(View.INVISIBLE);
                } else {
                    Random random = new Random();
                    int fileNumber = random.nextInt(100);
                    final String sensorDataName = FileOperations.getSensorStorageDir("SensorsInformation") + "/measurements" + fileNumber + ".txt";
                    FileOperations.deleteFile(sensorDataName);
                    final File sensorData = new File(sensorDataName);

                    pw = new PrintWriter(new BufferedWriter(new FileWriter(sensorData, true)));

                    Log.i(TAG, sensorDataName);

                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            // clean old files with random results
            FileOperations.deleteFile("/storage/emulated/0/myfile_nbp.txt");
            FileOperations.deleteFile("myfile_nbp.txt");

            // create classifier from a file
            String selectedClassifierName = "RandomForest";
            FactoryClassifiers fc = new FactoryClassifiers();
            String modelFileName = fc.getModelFile(selectedClassifierName);
            InputStream ins = getResources().openRawResource(getResources().getIdentifier(modelFileName, "raw", getPackageName()));
            selectedClassifier = fc.getModel(selectedClassifierName, ins);

            posInstance = 0;
            warmingUp = true;
            setSensors();

            performingActivity = 6;

            if (APP_STATE == 0) {
                launchCollectingInformation();
            } else {
                launchCollectingInformation();
                launchingRecognitionActivities();
            }

            firstRun = false;

            Log.i(TAG, "First run is still active");
        }

        if (isAmbient()) {

        } else {

        }
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
        List<Sensor> mList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE),
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        // print the amount of sensors and short information
        Log.i(TAG, String.valueOf(mList.size()));
        for (int i = 0; i < mList.size(); i++) {
            Log.i(TAG, "\t" + i + " " + mList.get(i).getName());
            Log.i(TAG, "\t" + i + " " + mList.get(i).toString());
        }

    }

    private void sensorsAndComplexFeaturesToArrays(int pos) {
        accelXArray[pos] = mResults.get("AccelX");
        accelYArray[pos] = mResults.get("AccelY");
        accelZArray[pos] = mResults.get("AccelZ");
        if (computeComplexFeatures) {
            accelXMean[pos] = mean.evaluate(accelXArray);
            accelYMean[pos] = mean.evaluate(accelYArray);
            accelZMean[pos] = mean.evaluate(accelZArray);
            accelXStd[pos] = stDev.evaluate(accelXArray);
            accelYStd[pos] = stDev.evaluate(accelYArray);
            accelZStd[pos] = stDev.evaluate(accelZArray);
        }

        gyroXArray[pos] = mResults.get("GyroX");
        gyroYArray[pos] = mResults.get("GyroY");
        gyroZArray[pos] = mResults.get("GyroZ");
        if (computeComplexFeatures) {
            gyroXMean[pos] = mean.evaluate(gyroXArray);
            gyroYMean[pos] = mean.evaluate(gyroYArray);
            gyroZMean[pos] = mean.evaluate(gyroZArray);
            gyroXStd[pos] = stDev.evaluate(gyroXArray);
            gyroYStd[pos] = stDev.evaluate(gyroYArray);
            gyroZStd[pos] = stDev.evaluate(gyroZArray);
        }

        gravityXArray[pos] = mResults.get("GravityX");
        gravityYArray[pos] = mResults.get("GravityY");
        gravityZArray[pos] = mResults.get("GravityZ");
        if (computeComplexFeatures) {
            gravityXMean[pos] = mean.evaluate(gravityXArray);
            gravityYMean[pos] = mean.evaluate(gravityYArray);
            gravityZMean[pos] = mean.evaluate(gravityZArray);
            gravityXStd[pos] = stDev.evaluate(gravityXArray);
            gravityYStd[pos] = stDev.evaluate(gravityYArray);
            gravityZStd[pos] = stDev.evaluate(gravityZArray);
        }

        linAccelXArray[pos] = mResults.get("LinAccelX");
        linAccelYArray[pos] = mResults.get("LinAccelY");
        linAccelZArray[pos] = mResults.get("LinAccelZ");
        if (computeComplexFeatures) {
            linAccelXMean[pos] = mean.evaluate(linAccelXArray);
            linAccelYMean[pos] = mean.evaluate(linAccelYArray);
            linAccelZMean[pos] = mean.evaluate(linAccelZArray);
            linAccelXStd[pos] = stDev.evaluate(linAccelXArray);
            linAccelYStd[pos] = stDev.evaluate(linAccelYArray);
            linAccelZStd[pos] = stDev.evaluate(linAccelZArray);
        }

        rotVecXArray[pos] = mResults.get("RotVecX");
        rotVecYArray[pos] = mResults.get("RotVecY");
        rotVecZArray[pos] = mResults.get("RotVecZ");
        rotVecSArray[pos] = mResults.get("RotVecS");
        if (computeComplexFeatures) {
            rotVecXMean[pos] = mean.evaluate(rotVecXArray);
            rotVecYMean[pos] = mean.evaluate(rotVecYArray);
            rotVecZMean[pos] = mean.evaluate(rotVecZArray);
            rotVecSMean[pos] = mean.evaluate(rotVecSArray);
            rotVecXStd[pos] = stDev.evaluate(rotVecXArray);
            rotVecYStd[pos] = stDev.evaluate(rotVecYArray);
            rotVecZStd[pos] = stDev.evaluate(rotVecZArray);
            rotVecSStd[pos] = stDev.evaluate(rotVecSArray);
        }

        stDetValArray[pos] = mResults.get("StDetVal");

        aiPreValArray[pos] = mResults.get("AiPreVal");
        if (computeComplexFeatures) {
            aiPreValMean[pos] = mean.evaluate(aiPreValArray);
            aiPreValStd[pos] = stDev.evaluate(aiPreValArray);
        }

        magFielXArray[pos] = mResults.get("MagFielX");
        magFielYArray[pos] = mResults.get("MagFielY");
        magFielZArray[pos] = mResults.get("MagFielZ");
        if (computeComplexFeatures) {
            magFielXMean[pos] = mean.evaluate(magFielXArray);
            magFielYMean[pos] = mean.evaluate(magFielYArray);
            magFielZMean[pos] = mean.evaluate(magFielZArray);
            magFielXStd[pos] = stDev.evaluate(magFielXArray);
            magFielYStd[pos] = stDev.evaluate(magFielYArray);
            magFielZStd[pos] = stDev.evaluate(magFielZArray);
        }

        heartRateValArray[pos] = mResults.get("HeartRateVal");
        if (computeComplexFeatures) {
            heartRateValMean[pos] = mean.evaluate(heartRateValArray);
            heartRateValStd[pos] = stDev.evaluate(heartRateValArray);
        }

    }

    private String arraysToString(int pos) {
        StringBuilder allSensorsData = new StringBuilder();
        long currentTime = System.currentTimeMillis();

        allSensorsData.append(currentTime).append(",");

        allSensorsData.append(accelXArray[pos]).append(",");
        allSensorsData.append(accelYArray[pos]).append(",");
        allSensorsData.append(accelZArray[pos]).append(",");
        allSensorsData.append(accelXMean[pos]).append(",");
        allSensorsData.append(accelYMean[pos]).append(",");
        allSensorsData.append(accelZMean[pos]).append(",");
        allSensorsData.append(accelXStd[pos]).append(",");
        allSensorsData.append(accelYStd[pos]).append(",");
        allSensorsData.append(accelZStd[pos]).append(",");

        allSensorsData.append(gyroXArray[pos]).append(",");
        allSensorsData.append(gyroYArray[pos]).append(",");
        allSensorsData.append(gyroZArray[pos]).append(",");
        allSensorsData.append(gyroXMean[pos]).append(",");
        allSensorsData.append(gyroYMean[pos]).append(",");
        allSensorsData.append(gyroZMean[pos]).append(",");
        allSensorsData.append(gyroXStd[pos]).append(",");
        allSensorsData.append(gyroYStd[pos]).append(",");
        allSensorsData.append(gyroZStd[pos]).append(",");

        allSensorsData.append(gravityXArray[pos]).append(",");
        allSensorsData.append(gravityYArray[pos]).append(",");
        allSensorsData.append(gravityZArray[pos]).append(",");
        allSensorsData.append(gravityXMean[pos]).append(",");
        allSensorsData.append(gravityYMean[pos]).append(",");
        allSensorsData.append(gravityZMean[pos]).append(",");
        allSensorsData.append(gravityXStd[pos]).append(",");
        allSensorsData.append(gravityYStd[pos]).append(",");
        allSensorsData.append(gravityZStd[pos]).append(",");

        allSensorsData.append(linAccelXArray[pos]).append(",");
        allSensorsData.append(linAccelYArray[pos]).append(",");
        allSensorsData.append(linAccelZArray[pos]).append(",");
        allSensorsData.append(linAccelXMean[pos]).append(",");
        allSensorsData.append(linAccelYMean[pos]).append(",");
        allSensorsData.append(linAccelZMean[pos]).append(",");
        allSensorsData.append(linAccelXStd[pos]).append(",");
        allSensorsData.append(linAccelYStd[pos]).append(",");
        allSensorsData.append(linAccelZStd[pos]).append(",");

        allSensorsData.append(rotVecXArray[pos]).append(",");
        allSensorsData.append(rotVecYArray[pos]).append(",");
        allSensorsData.append(rotVecZArray[pos]).append(",");
        allSensorsData.append(rotVecZArray[pos]).append(",");
        allSensorsData.append(rotVecXMean[pos]).append(",");
        allSensorsData.append(rotVecYMean[pos]).append(",");
        allSensorsData.append(rotVecZMean[pos]).append(",");
        allSensorsData.append(rotVecZMean[pos]).append(",");
        allSensorsData.append(rotVecXStd[pos]).append(",");
        allSensorsData.append(rotVecYStd[pos]).append(",");
        allSensorsData.append(rotVecZStd[pos]).append(",");
        allSensorsData.append(rotVecZStd[pos]).append(",");

        allSensorsData.append(stDetValArray[pos]).append(",");

        allSensorsData.append(aiPreValArray[pos]).append(",");
        allSensorsData.append(aiPreValMean[pos]).append(",");
        allSensorsData.append(aiPreValStd[pos]).append(",");

        allSensorsData.append(magFielXArray[pos]).append(",");
        allSensorsData.append(magFielYArray[pos]).append(",");
        allSensorsData.append(magFielZArray[pos]).append(",");
        allSensorsData.append(magFielXMean[pos]).append(",");
        allSensorsData.append(magFielYMean[pos]).append(",");
        allSensorsData.append(magFielZMean[pos]).append(",");
        allSensorsData.append(magFielXStd[pos]).append(",");
        allSensorsData.append(magFielYStd[pos]).append(",");
        allSensorsData.append(magFielZStd[pos]).append(",");

        allSensorsData.append(heartRateValArray[pos]).append(",");
        allSensorsData.append(heartRateValMean[pos]).append(",");
        allSensorsData.append(heartRateValStd[pos]).append(",");



        allSensorsData.append(performingActivity);

        return allSensorsData.toString();
    }

    private String createTitle() {
        StringBuilder title = new StringBuilder();
        title.append("Timestamp ,");

        title.append("AccelX, ");
        title.append("AccelY, ");
        title.append("AccelZ, ");
        title.append("AccelXMean, ");
        title.append("AccelYMean, ");
        title.append("AccelZMean, ");
        title.append("AccelXStd, ");
        title.append("AccelYStd, ");
        title.append("AccelZStd, ");

        title.append("GyroX, ");
        title.append("GyroY, ");
        title.append("GyroZ, ");
        title.append("GyroXMean, ");
        title.append("GyroYMean, ");
        title.append("GyroZMean, ");
        title.append("GyroXStd, ");
        title.append("GyroYStd, ");
        title.append("GyroZStd, ");

        title.append("GravityX, ");
        title.append("GravityY, ");
        title.append("GravityZ, ");
        title.append("GravityXMean, ");
        title.append("GravityYMean, ");
        title.append("GravityZMean, ");
        title.append("GravityXStd, ");
        title.append("GravityYStd, ");
        title.append("GravityZStd, ");

        title.append("LinAccelX, ");
        title.append("LinAccelY, ");
        title.append("LinAccelZ, ");
        title.append("LinAccelXMean, ");
        title.append("LinAccelYMean, ");
        title.append("LinAccelZMean, ");
        title.append("LinAccelXStd, ");
        title.append("LinAccelYStd, ");
        title.append("LinAccelZStd, ");

        title.append("RotVecX, ");
        title.append("RotVecY, ");
        title.append("RotVecZ, ");
        title.append("RotVecS, ");
        title.append("RotVecXMean, ");
        title.append("RotVecYMean, ");
        title.append("RotVecZMean, ");
        title.append("RotVecSMean, ");
        title.append("RotVecXStd, ");
        title.append("RotVecYStd, ");
        title.append("RotVecZStd, ");
        title.append("RotVecSStd, ");

        title.append("StDetVal, ");

        title.append("AiPreVal, ");
        title.append("AiPreValMean, ");
        title.append("AiPreValStd, ");

        title.append("MagFielX, ");
        title.append("MagFielY, ");
        title.append("MagFielZ, ");
        title.append("MagFielXMean, ");
        title.append("MagFielYMean, ");
        title.append("MagFielZMean, ");
        title.append("MagFielXStd, ");
        title.append("MagFielYStd, ");
        title.append("MagFielZStd, ");

        title.append("HeartRateVal, ");
        title.append("HeartRateValMean, ");
        title.append("HeartRateValStd, ");

        title.append("Activity");

        return title.toString();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            // In this example, alpha is calculated as t / (t + dT),
            // where t is the low-pass filter's time-constant and
            // dT is the event delivery rate.

            final float alpha = 0.1f;

            // Isolate the force of gravity with the low-pass filter.
            accelerometerFilter[0] = alpha * accelerometerFilter[0] + (1 - alpha) * event.values[0];
            accelerometerFilter[1] = alpha * accelerometerFilter[1] + (1 - alpha) * event.values[1];
            accelerometerFilter[2] = alpha * accelerometerFilter[2] + (1 - alpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            float ax = event.values[0] - accelerometerFilter[0];
            float ay = event.values[1] - accelerometerFilter[1];
            float az = event.values[2] - accelerometerFilter[2];

            mResults.put("AccelX", ax);
            mResults.put("AccelY", ay);
            mResults.put("AccelZ", az);
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];

            mResults.put("GyroX", ax);
            mResults.put("GyroY", ay);
            mResults.put("GyroZ", az);
        }

        if (event.sensor.getType() == Sensor.TYPE_GRAVITY){
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];

            mResults.put("GravityX", ax);
            mResults.put("GravityY", ay);
            mResults.put("GravityZ", az);
        }

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];

            mResults.put("LinAccelX", ax);
            mResults.put("LinAccelY", ay);
            mResults.put("LinAccelZ", az);
        }

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];
            float as = event.values[2];

            mResults.put("RotVecX", ax);
            mResults.put("RotVecY", ay);
            mResults.put("RotVecZ", az);
            mResults.put("RotVecS", as);
        }

        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            float ax = event.values[0];

            mResults.put("StDetVal", ax);
        }

        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            float ax = event.values[0];

            mResults.put("AiPreVal", ax);
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];

            mResults.put("MagFielX", ax);
            mResults.put("MagFielY", ay);
            mResults.put("MagFielZ", az);
        }

        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE){
            float ax = event.values[0];

            mResults.put("HeartRateVal", ax);
        }

        if (warmingUp) {
            mResults.put("StDetVal", 0.0f);
            mResults.put("HeartRateVal", Float.NaN);
            warmingUp = false;
        }
    }

    /**
     * Handles the button press to finish this activity and take the user back to the Home.
     */
    public void onFinishActivity(View view) {
        setResult(RESULT_OK);
        finish();
    }

    public void onWalking(View view) {
        performingActivity = ActivityType.valueOf("WALKING").ordinal();
        mNewActivity.setText("New activity is walking");
        timeChangeActivityUpdateMs = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;
    }

    public void onUpstairs(View view) {
        performingActivity = ActivityType.valueOf("WALKING_UPSTAIRS").ordinal();
        mNewActivity.setText("New activity is upstairs");
        timeChangeActivityUpdateMs = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;
    }

    public void onDownstairs(View view) {
        performingActivity = ActivityType.valueOf("WALKING_DOWNSTAIRS").ordinal();
        mNewActivity.setText("New activity is downstairs");
        timeChangeActivityUpdateMs = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;
    }

    public void onSitting(View view) {
        performingActivity = ActivityType.valueOf("SITTING").ordinal();
        mNewActivity.setText("New activity is sitting");
        timeChangeActivityUpdateMs = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;
    }

    public void onStanding(View view) {
        performingActivity = ActivityType.valueOf("STANDING").ordinal();
        mNewActivity.setText("New activity is standing");
        timeChangeActivityUpdateMs = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;
    }

    public void onLaying(View view) {
        performingActivity = ActivityType.valueOf("LAYING").ordinal();
        mNewActivity.setText("New activity is laying");
        timeChangeActivityUpdateMs = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;
    }

    public void onEmpty(View view) {
        performingActivity = 6;
        mNewActivity.setText("New activity is empty");
        timeChangeActivityUpdateMs = System.currentTimeMillis();
        posInstance = 0;
        computeComplexFeatures = false;
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

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public ArrayList<Attribute> getNewAttributes() {
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        int numAttrib = 0;

        attributes.add(new Attribute("AccelX", numAttrib++));
        attributes.add(new Attribute("AccelY", numAttrib++));
        attributes.add(new Attribute("AccelZ", numAttrib++));
        attributes.add(new Attribute("AccelXMean", numAttrib++));
        attributes.add(new Attribute("AccelYMean", numAttrib++));
        attributes.add(new Attribute("AccelZMean", numAttrib++));
        attributes.add(new Attribute("AccelXStd", numAttrib++));
        attributes.add(new Attribute("AccelYStd", numAttrib++));
        attributes.add(new Attribute("AccelZStd", numAttrib++));

        attributes.add(new Attribute("GyroX", numAttrib++));
        attributes.add(new Attribute("GyroY", numAttrib++));
        attributes.add(new Attribute("GyroZ", numAttrib++));
        attributes.add(new Attribute("GyroXMean", numAttrib++));
        attributes.add(new Attribute("GyroYMean", numAttrib++));
        attributes.add(new Attribute("GyroZMean", numAttrib++));
        attributes.add(new Attribute("GyroXStd", numAttrib++));
        attributes.add(new Attribute("GyroYStd", numAttrib++));
        attributes.add(new Attribute("GyroZStd", numAttrib++));

        attributes.add(new Attribute("GravityX", numAttrib++));
        attributes.add(new Attribute("GravityY", numAttrib++));
        attributes.add(new Attribute("GravityZ", numAttrib++));
        attributes.add(new Attribute("GravityXMean", numAttrib++));
        attributes.add(new Attribute("GravityYMean", numAttrib++));
        attributes.add(new Attribute("GravityZMean", numAttrib++));
        attributes.add(new Attribute("GravityXStd", numAttrib++));
        attributes.add(new Attribute("GravityYStd", numAttrib++));
        attributes.add(new Attribute("GravityZStd", numAttrib++));

        attributes.add(new Attribute("LinAccelX", numAttrib++));
        attributes.add(new Attribute("LinAccelY", numAttrib++));
        attributes.add(new Attribute("LinAccelZ", numAttrib++));
        attributes.add(new Attribute("LinAccelXMean", numAttrib++));
        attributes.add(new Attribute("LinAccelYMean", numAttrib++));
        attributes.add(new Attribute("LinAccelZMean", numAttrib++));
        attributes.add(new Attribute("LinAccelXStd", numAttrib++));
        attributes.add(new Attribute("LinAccelYStd", numAttrib++));
        attributes.add(new Attribute("LinAccelZStd", numAttrib++));

        attributes.add(new Attribute("RotVecX", numAttrib++));
        attributes.add(new Attribute("RotVecY", numAttrib++));
        attributes.add(new Attribute("RotVecZ", numAttrib++));
        attributes.add(new Attribute("RotVecS", numAttrib++));
        attributes.add(new Attribute("RotVecXMean", numAttrib++));
        attributes.add(new Attribute("RotVecYMean", numAttrib++));
        attributes.add(new Attribute("RotVecZMean", numAttrib++));
        attributes.add(new Attribute("RotVecSMean", numAttrib++));
        attributes.add(new Attribute("RotVecXStd", numAttrib++));
        attributes.add(new Attribute("RotVecYStd", numAttrib++));
        attributes.add(new Attribute("RotVecZStd", numAttrib++));
        attributes.add(new Attribute("RotVecSStd", numAttrib++));

        attributes.add(new Attribute("StDetVal", numAttrib++));

        attributes.add(new Attribute("AiPreVal", numAttrib++));
        attributes.add(new Attribute("AiPreValMean", numAttrib++));
        attributes.add(new Attribute("AiPreValStd", numAttrib++));

        attributes.add(new Attribute("MagFielX", numAttrib++));
        attributes.add(new Attribute("MagFielY", numAttrib++));
        attributes.add(new Attribute("MagFielZ", numAttrib++));
        attributes.add(new Attribute("MagFielXMean", numAttrib++));
        attributes.add(new Attribute("MagFielYMean", numAttrib++));
        attributes.add(new Attribute("MagFielZMean", numAttrib++));
        attributes.add(new Attribute("MagFielXStd", numAttrib++));
        attributes.add(new Attribute("MagFielYStd", numAttrib++));
        attributes.add(new Attribute("MagFielZStd", numAttrib++));

        attributes.add(new Attribute("HeartRateVal", numAttrib++));
        attributes.add(new Attribute("HeartRateValMean", numAttrib++));
        attributes.add(new Attribute("HeartRateValStd", numAttrib++));

        List<String> values = getActivityValues();
        attributes.add(new Attribute("Activity", values, numAttrib));

        return attributes;
    }

    public DenseInstance getDenseInstances(int numOfAttributes, int pos) {
        double[] attributeValues = new double[numOfAttributes];
        int currentAttNumber = 0;

        if (computeComplexFeatures) {
            attributeValues[currentAttNumber++] = accelXArray[pos];
            attributeValues[currentAttNumber++] = accelYArray[pos];
            attributeValues[currentAttNumber++] = accelZArray[pos];
            attributeValues[currentAttNumber++] = accelXMean[pos];
            attributeValues[currentAttNumber++] = accelYMean[pos];
            attributeValues[currentAttNumber++] = accelZMean[pos];
            attributeValues[currentAttNumber++] = accelXStd[pos];
            attributeValues[currentAttNumber++] = accelYStd[pos];
            attributeValues[currentAttNumber++] = accelZStd[pos];

            attributeValues[currentAttNumber++] = gyroXArray[pos];
            attributeValues[currentAttNumber++] = gyroYArray[pos];
            attributeValues[currentAttNumber++] = gyroZArray[pos];
            attributeValues[currentAttNumber++] = gyroXMean[pos];
            attributeValues[currentAttNumber++] = gyroYMean[pos];
            attributeValues[currentAttNumber++] = gyroZMean[pos];
            attributeValues[currentAttNumber++] = gyroXStd[pos];
            attributeValues[currentAttNumber++] = gyroYStd[pos];
            attributeValues[currentAttNumber++] = gyroZStd[pos];

            attributeValues[currentAttNumber++] = gravityXArray[pos];
            attributeValues[currentAttNumber++] = gravityYArray[pos];
            attributeValues[currentAttNumber++] = gravityZArray[pos];
            attributeValues[currentAttNumber++] = gravityXMean[pos];
            attributeValues[currentAttNumber++] = gravityYMean[pos];
            attributeValues[currentAttNumber++] = gravityZMean[pos];
            attributeValues[currentAttNumber++] = gravityXStd[pos];
            attributeValues[currentAttNumber++] = gravityYStd[pos];
            attributeValues[currentAttNumber++] = gravityZStd[pos];

            attributeValues[currentAttNumber++] = linAccelXArray[pos];
            attributeValues[currentAttNumber++] = linAccelYArray[pos];
            attributeValues[currentAttNumber++] = linAccelZArray[pos];
            attributeValues[currentAttNumber++] = linAccelXMean[pos];
            attributeValues[currentAttNumber++] = linAccelYMean[pos];
            attributeValues[currentAttNumber++] = linAccelZMean[pos];
            attributeValues[currentAttNumber++] = linAccelXStd[pos];
            attributeValues[currentAttNumber++] = linAccelYStd[pos];
            attributeValues[currentAttNumber++] = linAccelZStd[pos];

            attributeValues[currentAttNumber++] = rotVecXArray[pos];
            attributeValues[currentAttNumber++] = rotVecYArray[pos];
            attributeValues[currentAttNumber++] = rotVecZArray[pos];
            attributeValues[currentAttNumber++] = rotVecSArray[pos];
            attributeValues[currentAttNumber++] = rotVecXMean[pos];
            attributeValues[currentAttNumber++] = rotVecYMean[pos];
            attributeValues[currentAttNumber++] = rotVecZMean[pos];
            attributeValues[currentAttNumber++] = rotVecSMean[pos];
            attributeValues[currentAttNumber++] = rotVecXStd[pos];
            attributeValues[currentAttNumber++] = rotVecYStd[pos];
            attributeValues[currentAttNumber++] = rotVecZStd[pos];
            attributeValues[currentAttNumber++] = rotVecSStd[pos];

            attributeValues[currentAttNumber++] = stDetValArray[pos];

            attributeValues[currentAttNumber++] = aiPreValArray[pos];
            attributeValues[currentAttNumber++] = aiPreValMean[pos];
            attributeValues[currentAttNumber++] = aiPreValStd[pos];

            attributeValues[currentAttNumber++] = magFielXArray[pos];
            attributeValues[currentAttNumber++] = magFielYArray[pos];
            attributeValues[currentAttNumber++] = magFielZArray[pos];
            attributeValues[currentAttNumber++] = magFielXMean[pos];
            attributeValues[currentAttNumber++] = magFielYMean[pos];
            attributeValues[currentAttNumber++] = magFielZMean[pos];
            attributeValues[currentAttNumber++] = magFielXStd[pos];
            attributeValues[currentAttNumber++] = magFielYStd[pos];
            attributeValues[currentAttNumber++] = magFielZStd[pos];

            attributeValues[currentAttNumber++] = heartRateValArray[pos];
            attributeValues[currentAttNumber++] = heartRateValMean[pos];
            attributeValues[currentAttNumber++] = heartRateValStd[pos];

            List<String> activityValues = getActivityValues();
            attributeValues[currentAttNumber] = activityValues.indexOf(activityValues.get(performingActivity));
        }

        return new DenseInstance(1.0, attributeValues);
    }

    private List<String> getActivityValues() {
        List<String> values = new ArrayList<String>();
        values.add("0");
        values.add("1");
        values.add("2");
        values.add("3");
        values.add("4");
        values.add("5");
        values.add("NA");

        return values;
    }

}
