package com.project.frugalmachinelearning;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.project.frugalmachinelearning.classifiers.ActivityType;
import com.project.frugalmachinelearning.classifiers.ActivityWindow;
import com.project.frugalmachinelearning.classifiers.FactoryClassifiers;
import com.project.frugalmachinelearning.external.MathStuff;
import com.project.frugalmachinelearning.tools.ApplicationStates;
import com.project.frugalmachinelearning.tools.FileOperations;
import com.project.frugalmachinelearning.tools.FloatingActionButtonFlexibleActions;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.bayes.AveragedNDependenceEstimators.A1DE;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";
    private static final int AMOUNT_OF_ATTRIBUTES = 18;

    private static final int UPDATES_PER_SECOND = 16;

    private static final int FRAME_SIZE = UPDATES_PER_SECOND * 2;

    private SensorManager mSensorManager;

    private float accelX;
    private float accelY;
    private float accelZ;

    private float gyroX;
    private float gyroY;
    private float gyroZ;

    private float gravityX;
    private float gravityY;
    private float gravityZ;

    private float linAccelX;
    private float linAccelY;
    private float linAccelZ;

    private float rotVecX;
    private float rotVecY;
    private float rotVecS;

    private float aiPreVal;

    private float magFielY;

    private float heartRateVal;


    private Thread t;
    private Thread tClassifyActivity;

    private boolean needTitle = true;

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
    private DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS dd/MM/yyyy");

    private boolean firstRun = true;

    private long timeChangeActivityUpdateMs;

    private float[] accelerometerFilter = new float[3];

    private double[] accelXArray = new double[FRAME_SIZE];
    private double[] accelYArray = new double[FRAME_SIZE];
    private double[] accelZArray = new double[FRAME_SIZE];
    private double accelXMin = 0.0;
    private double accelYMin = 0.0;
    private double accelZMin = 0.0;
    private double accelXMax = 0.0;
    private double accelYMax = 0.0;
    private double accelZMax = 0.0;
    private double accelXMean = 0.0;
    private double accelYMean = 0.0;
    private double accelZMean = 0.0;
    private double accelXRange = 0.0;
    private double accelYRange = 0.0;
    private double accelZRange = 0.0;
    private double accelXStd = 0.0;
    private double accelYStd = 0.0;
    private double accelZStd = 0.0;

    private double[] gyroXArray = new double[FRAME_SIZE];
    private double[] gyroYArray = new double[FRAME_SIZE];
    private double[] gyroZArray = new double[FRAME_SIZE];
    private double gyroXMin = 0.0;
    private double gyroYMin = 0.0;
    private double gyroZMin = 0.0;
    private double gyroXMax = 0.0;
    private double gyroYMax = 0.0;
    private double gyroZMax = 0.0;
    private double gyroXMean = 0.0;
    private double gyroYMean = 0.0;
    private double gyroZMean = 0.0;
    private double gyroXRange = 0.0;
    private double gyroYRange = 0.0;
    private double gyroZRange = 0.0;
    private double gyroXStd = 0.0;
    private double gyroYStd = 0.0;
    private double gyroZStd = 0.0;

    private double[] gravityXArray = new double[FRAME_SIZE];
    private double[] gravityYArray = new double[FRAME_SIZE];
    private double[] gravityZArray = new double[FRAME_SIZE];
    private double gravityXMin = 0.0;
    private double gravityYMin = 0.0;
    private double gravityZMin = 0.0;
    private double gravityXMax = 0.0;
    private double gravityYMax = 0.0;
    private double gravityZMax = 0.0;
    private double gravityXMean = 0.0;
    private double gravityYMean = 0.0;
    private double gravityZMean = 0.0;
    private double gravityXRange = 0.0;
    private double gravityYRange = 0.0;
    private double gravityZRange = 0.0;
    private double gravityXStd = 0.0;
    private double gravityYStd = 0.0;
    private double gravityZStd = 0.0;

    private double[] linAccelXArray = new double[FRAME_SIZE];
    private double[] linAccelYArray = new double[FRAME_SIZE];
    private double[] linAccelZArray = new double[FRAME_SIZE];
    private double linAccelXMin = 0.0;
    private double linAccelYMin = 0.0;
    private double linAccelZMin = 0.0;
    private double linAccelXMax = 0.0;
    private double linAccelYMax = 0.0;
    private double linAccelZMax = 0.0;
    private double linAccelXMean = 0.0;
    private double linAccelYMean = 0.0;
    private double linAccelZMean = 0.0;
    private double linAccelXRange = 0.0;
    private double linAccelYRange = 0.0;
    private double linAccelZRange = 0.0;
    private double linAccelXStd = 0.0;
    private double linAccelYStd = 0.0;
    private double linAccelZStd = 0.0;

    private double[] rotVecXArray = new double[FRAME_SIZE];
    private double[] rotVecYArray = new double[FRAME_SIZE];
    private double[] rotVecSArray = new double[FRAME_SIZE];
    private double rotVecXMin = 0.0;
    private double rotVecYMin = 0.0;
    private double rotVecSMin = 0.0;
    private double rotVecXMax = 0.0;
    private double rotVecYMax = 0.0;
    private double rotVecSMax = 0.0;
    private double rotVecXMean = 0.0;
    private double rotVecYMean = 0.0;
    private double rotVecSMean = 0.0;
    private double rotVecXRange = 0.0;
    private double rotVecYRange = 0.0;
    private double rotVecSRange = 0.0;
    private double rotVecXStd = 0.0;
    private double rotVecYStd = 0.0;
    private double rotVecSStd = 0.0;

    private double[] aiPreValArray = new double[FRAME_SIZE];
    private double aiPreValMin = 0.0;
    private double aiPreValMax = 0.0;
    private double aiPreValMean = 0.0;
    private double aiPreValRange  = 0.0;
    private double aiPreValStd = 0.0;

    private double[] magFielYArray = new double[FRAME_SIZE];
    private double magFielYMin = 0.0;
    private double magFielYMax = 0.0;
    private double magFielYMean = 0.0;
    private double magFielYRange = 0.0;
    private double magFielYStd = 0.0;

    private double[] heartRateValArray = new double[FRAME_SIZE];
    private double heartRateValMin = 0.0;
    private double heartRateValMax = 0.0;
    private double heartRateValMean = 0.0;
    private double heartRateValRange = 0.0;
    private double heartRateValStd = 0.0;

    private long previousBatteryUpdate;

    StandardDeviation stDev = new StandardDeviation();
    Mean mean = new Mean();

    private MathStuff msf;

    private long pTime;
    private long nTime;

    private FloatingActionButtonFlexibleActions leftCenterButton;

    private static int appState;
    boolean computeComplexFeatures = false;


    /**
     * Custom 'what' for Message sent to Handler.
     */
    private static final int MSG_UPDATE_SCREEN = 0;

    /**
     * Milliseconds between updates based on state.
     */
    private static final long ACTIVE_INTERVAL_MS = TimeUnit.SECONDS.toMillis(1);
    private static final long AMBIENT_INTERVAL_MS = TimeUnit.SECONDS.toMillis(900);

    /**
     * Tracks latest ambient details, such as burnin offsets, etc.
     */
    private Bundle mAmbientDetails;

    private final Handler mActiveModeUpdateHandler = new UpdateHandler(this);

    private volatile int mDrawCount = 0;

    private boolean discretizeData = false;

    private boolean initDiscretizeItem = true;

    Discretize discretizeItems;

    private AlarmManager mAmbientStateAlarmManager;
    private PendingIntent mAmbientStatePendingIntent;

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

        mAmbientStateAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent ambientStateIntent = new Intent(getApplicationContext(), MainActivity.class);

        mAmbientStatePendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0 /* requestCode */,
                ambientStateIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

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
                                    if (isExternalStorageWritable()) {

                                        if (needTitle && appState == 0) {
                                            pw.println(createTitle());
                                            needTitle = false;
                                        }

                                        // computing features
                                        sensorsFeaturesToArrays(posInstance);
                                        if (computeComplexFeatures) {
                                            computeAdditionalFeatures();
                                        }


                                        if (appState == 0) {
                                            if (computeComplexFeatures && performingActivity != 6) {

                                                // pause data collection after activity change for a short period and wait for computed values
                                                long currentTimeMs = System.currentTimeMillis();
                                                if (currentTimeMs - timeChangeActivityUpdateMs >= 5000) {
                                                    StringBuilder newInfo = arraysToString(posInstance);
                                                    pw.println(newInfo);
                                                }
                                            }
                                        } else {
                                            timeWindowInstance = getDenseInstances(AMOUNT_OF_ATTRIBUTES * 5 + 1, posInstance);
                                        }

                                        posInstance++;
                                        if (posInstance == 2 * UPDATES_PER_SECOND) {
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
                                DenseInstance[] instances = {timeWindowInstance};
                                Instances data = ActivityWindow.constructInstances(attributes, instances);

                                Instances discretData = null;

                                if (discretizeData && discretizeItems != null) {

                                    try {

                                        discretData = Filter.useFilter(data, discretizeItems);

                                    } catch (Exception e) {
                                        Log.i(TAG, e.toString());
                                    }
                                }

                                String activityFullName = "BEING COMPUTED";

                                if (discretData != null) {
                                    activityFullName = ActivityWindow.getActivityName(selectedClassifier, discretData);

                                } else  {
                                    if (!discretizeData) {
                                        activityFullName = ActivityWindow.getActivityName(selectedClassifier, data);
                                    }
                                }

                                long currentTimeMs = System.currentTimeMillis();
                                if (currentTimeMs - previousBatteryUpdate > 29555) {
                                    StringBuilder bInfo = new StringBuilder();
                                    bInfo.append(currentTimeMs).append(",");
                                    bInfo.append(selectedClassifier.getClass()).append(",");
                                    bInfo.append(getBatteryLevel());
                                    String bInfoString = bInfo.toString();
                                    pw.println(bInfoString);
                                    pw.flush();

                                    bInfo = null;
                                    previousBatteryUpdate = currentTimeMs;

                                    Log.i(TAG, bInfoString);

                                }

                                mTheoreticalActivity.setText("Activity is " + activityFullName);

                                Log.i(TAG, activityFullName);
                            }

                        });
                    }
                } catch (InterruptedException e) {
                    Log.i(TAG, e.toString());
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

        mDrawCount += 1;
        long currentTimeMs = System.currentTimeMillis();
        Log.d(TAG, "loadDataAndUpdateScreen(): " + currentTimeMs + "(" + isAmbient() + ")");

        Date current = new Date();

        msf = new MathStuff();

        // mTime.setText(new SimpleDateFormat("HH:mm:ss.SSS").format(current) + " update time");

        if (firstRun) {
            try {
                timeChangeActivityUpdateMs = System.currentTimeMillis();

                performingActivity = 6;

                /*    Random random = new Random();
                int fileNumber = random.nextInt(100);   */

                Intent intent = getIntent();
                String stateFromIntent = intent.getStringExtra("APP STATE");
                appState = ApplicationStates.valueOf(stateFromIntent).ordinal();

/*                String hexColor = "#" + intent.getStringExtra("background");
                fBackground.setBackgroundColor(Color.parseColor(hexColor)); */

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

                    createCircleMenu();

                    final String sensorDataName = FileOperations.getSensorStorageDir("SensorsInformation") + "/measurements" + fileNumber + ".txt";
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
            String selectedClassifierName = "A1DE";
            if (selectedClassifierName.equals("A1DE")) {
                discretizeData = true;
                initFilter();
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

        if (isAmbient()) {

        } else {

        }
    }

    private void initFilter() {
        discretizeItems = new Discretize();

        String[] options = new String[6];
        options[0] = "-B";
        options[1] = "3";
        options[2] = "-M";
        options[3] = "-1.0";
        options[4] = "-R";
        options[5] = "first-last";

        try {

            InputStream ins = getResources().openRawResource(getResources().getIdentifier("margins", "raw", getPackageName()));

            BufferedReader br = new BufferedReader(new InputStreamReader(ins, "UTF-8"));

            Instances data = new Instances(br);
            data.setClassIndex(data.numAttributes() - 1);

            discretizeItems.setOptions(options);
            discretizeItems.setInputFormat(data);

            br.close();

            ins = getResources().openRawResource(getResources().getIdentifier("features", "raw", getPackageName()));

            br = new BufferedReader(new InputStreamReader(ins, "UTF-8"));

            Instances dataInFile = new Instances(br);
            dataInFile.setClassIndex(dataInFile.numAttributes() - 1);

            Instances tData  = new Instances(dataInFile, 0, 32);

            tData = Filter.useFilter(tData, discretizeItems);

            br.close();

        } catch (Exception e) {
            Log.i(TAG, e.toString());
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

    private void sensorsFeaturesToArrays(int pos) {
        accelXArray[pos] = accelX;
        accelYArray[pos] = accelY;
        accelZArray[pos] = accelZ;

        gyroXArray[pos] = gyroX;
        gyroYArray[pos] = gyroY;
        gyroZArray[pos] = gyroZ;

        gravityXArray[pos] = gravityX;
        gravityYArray[pos] = gravityY;
        gravityZArray[pos] = gravityZ;

        linAccelXArray[pos] = linAccelX;
        linAccelYArray[pos] = linAccelY;
        linAccelZArray[pos] = linAccelZ;

        rotVecXArray[pos] = rotVecX;
        rotVecYArray[pos] = rotVecY;
        rotVecSArray[pos] = rotVecS;

        aiPreValArray[pos] = aiPreVal;

        magFielYArray[pos] = magFielY;

        heartRateValArray[pos] = heartRateVal;

    }

    private void computeAdditionalFeatures() {
        accelXMin = msf.getMin(accelXArray);
        accelYMin = msf.getMin(accelYArray);
        accelZMin = msf.getMin(accelZArray);
        accelXMax = msf.getMax(accelXArray);
        accelYMax = msf.getMax(accelYArray);
        accelZMax = msf.getMax(accelZArray);
        accelXMean = msf.getMean(accelXArray);
        accelYMean = msf.getMean(accelYArray);
        accelZMean = msf.getMean(accelZArray);
        accelXRange = accelXMax - accelXMin;
        accelYRange = accelYMax - accelYMin;
        accelZRange = accelZMax - accelZMin;
        accelXStd = msf.getStdDev(accelXArray);
        accelYStd = msf.getStdDev(accelYArray);
        accelZStd = msf.getStdDev(accelZArray);

        gyroXMin = msf.getMin(gyroXArray);
        gyroYMin = msf.getMin(gyroYArray);
        gyroZMin = msf.getMin(gyroZArray);
        gyroXMax = msf.getMax(gyroXArray);
        gyroYMax = msf.getMax(gyroYArray);
        gyroZMax = msf.getMax(gyroZArray);
        gyroXMean = msf.getMean(gyroXArray);
        gyroYMean = msf.getMean(gyroYArray);
        gyroZMean = msf.getMean(gyroZArray);
        gyroXRange = gyroXMax - gyroXMin;
        gyroYRange = gyroYMax - gyroYMin;
        gyroZRange = gyroZMax - gyroZMin;
        gyroXStd = msf.getStdDev(gyroXArray);
        gyroYStd = msf.getStdDev(gyroYArray);
        gyroZStd = msf.getStdDev(gyroZArray);

        gravityXMin = msf.getMin(gravityXArray);
        gravityYMin = msf.getMin(gravityYArray);
        gravityZMin = msf.getMin(gravityZArray);
        gravityXMax = msf.getMax(gravityXArray);
        gravityYMax = msf.getMax(gravityYArray);
        gravityZMax = msf.getMax(gravityZArray);
        gravityXMean = msf.getMean(gravityXArray);
        gravityYMean = msf.getMean(gravityYArray);
        gravityZMean = msf.getMean(gravityZArray);
        gravityXRange = gravityXMax - gravityXMin;
        gravityYRange = gravityYMax - gravityYMin;
        gravityZRange = gravityZMax - gravityZMin;
        gravityXStd = msf.getStdDev(gravityXArray);
        gravityYStd = msf.getStdDev(gravityYArray);
        gravityZStd = msf.getStdDev(gravityZArray);

        linAccelXMin = msf.getMin(linAccelXArray);
        linAccelYMin = msf.getMin(linAccelYArray);
        linAccelZMin = msf.getMin(linAccelZArray);
        linAccelXMax = msf.getMax(linAccelXArray);
        linAccelYMax = msf.getMax(linAccelYArray);
        linAccelZMax = msf.getMax(linAccelZArray);
        linAccelXMean = msf.getMean(linAccelXArray);
        linAccelYMean = msf.getMean(linAccelYArray);
        linAccelZMean = msf.getMean(linAccelZArray);
        linAccelXRange = linAccelXMax - linAccelXMin;
        linAccelYRange = linAccelYMax - linAccelYMin;
        linAccelZRange = linAccelZMax - linAccelZMin;
        linAccelXStd = msf.getStdDev(linAccelXArray);
        linAccelYStd = msf.getStdDev(linAccelYArray);
        linAccelZStd = msf.getStdDev(linAccelZArray);

        rotVecXMin = msf.getMin(rotVecXArray);
        rotVecYMin = msf.getMin(rotVecYArray);
        rotVecSMin = msf.getMin(rotVecSArray);
        rotVecXMax = msf.getMax(rotVecXArray);
        rotVecYMax = msf.getMax(rotVecYArray);
        rotVecSMax = msf.getMax(rotVecSArray);
        rotVecXMean = msf.getMean(rotVecXArray);
        rotVecYMean = msf.getMean(rotVecYArray);
        rotVecSMean = msf.getMean(rotVecSArray);
        rotVecXRange = rotVecXMax - rotVecXMin;
        rotVecYRange = rotVecYMax - rotVecYMin;
        rotVecSRange = rotVecSMax - rotVecSMin;
        rotVecXStd = msf.getStdDev(rotVecXArray);
        rotVecYStd = msf.getStdDev(rotVecYArray);
        rotVecSStd = msf.getStdDev(rotVecSArray);

        aiPreValMin = msf.getMin(aiPreValArray);
        aiPreValMax = msf.getMax(aiPreValArray);
        aiPreValMean = msf.getMean(aiPreValArray);
        aiPreValRange = aiPreValMax - aiPreValMin;
        aiPreValStd = msf.getStdDev(aiPreValArray);

        magFielYMin = msf.getMin(magFielYArray);
        magFielYMax = msf.getMax(magFielYArray);
        magFielYMean = msf.getMean(magFielYArray);
        magFielYRange = magFielYMax - magFielYMin;
        magFielYStd = msf.getStdDev(magFielYArray);

        heartRateValMin = msf.getMin(heartRateValArray);
        heartRateValMax = msf.getMax(heartRateValArray);
        heartRateValMean = msf.getMean(heartRateValArray);
        heartRateValRange = heartRateValMax - heartRateValMin;
        heartRateValStd = msf.getStdDev(heartRateValArray);
    }

    private StringBuilder arraysToString(int pos) {
        StringBuilder allSensorsData = new StringBuilder();
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

        allSensorsData.append(currentTime).append(",");

        allSensorsData.append(accelXArray[pos]).append(",").append(accelYArray[pos]).append(",").append(accelZArray[pos]).append(",");

        allSensorsData.append(gyroXArray[pos]).append(",").append(gyroYArray[pos]).append(",").append(gyroZArray[pos]).append(",");

        allSensorsData.append(gravityXArray[pos]).append(",").append(gravityYArray[pos]).append(",").append(gravityZArray[pos]).append(",");

        allSensorsData.append(linAccelXArray[pos]).append(",").append(linAccelYArray[pos]).append(",").append(linAccelZArray[pos]).append(",");

        allSensorsData.append(rotVecXArray[pos]).append(",").append(rotVecYArray[pos]).append(",").append(rotVecSArray[pos]).append(",");

        allSensorsData.append(aiPreValArray[pos]).append(",");

        allSensorsData.append(magFielYArray[pos]).append(",");

        allSensorsData.append(heartRateValArray[pos]).append(",");

        allSensorsData.append(performingActivity);

        return allSensorsData;
    }

    private String createTitle() {
        StringBuilder title = new StringBuilder();
        title.append("Timestamp ,");

        title.append("AccelX, ");
        title.append("AccelY, ");
        title.append("AccelZ, ");

        title.append("GyroX, ");
        title.append("GyroY, ");
        title.append("GyroZ, ");

        title.append("GravityX, ");
        title.append("GravityY, ");
        title.append("GravityZ, ");

        title.append("LinAccelX, ");
        title.append("LinAccelY, ");
        title.append("LinAccelZ, ");

        title.append("RotVecX, ");
        title.append("RotVecY, ");
        title.append("RotVecS, ");

        title.append("AiPreVal, ");

        title.append("MagFielY, ");

        title.append("HeartRateVal, ");

        title.append("Activity");

        String dataTitle = title.toString();

        return dataTitle;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

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

            accelX = ax;
            accelY = ay;
            accelZ = az;
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];

            gyroX = ax;
            gyroY = ay;
            gyroZ = az;
        }

        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];

            gravityX = ax;
            gravityY = ay;
            gravityZ = az;
        }

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];

            linAccelX = ax;
            linAccelY = ay;
            linAccelZ = az;
        }

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float ax = event.values[0];
            float ay = event.values[1];
            float as = event.values[2];

            rotVecX = ax;
            rotVecY = ay;
            rotVecS = as;
        }

        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            float ax = event.values[0];

            aiPreVal = ax;
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float ay = event.values[1];

            magFielY = ay;
        }

        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            float ax = event.values[0];

            heartRateVal = ax;
        }

        if (warmingUp) {
            heartRateVal = 0.0f;
            warmingUp = false;
        }
    }

    /**
     * Handles the button press to finish this activity and take the user back to the Home.
     */
    public void onFinishActivity(View view) {
        showCustomDialog(view);
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

    public void onLaying() {
        performingActivity = 5;                                                      // ActivityType.valueOf("LAYING").ordinal();
        mGenericActivity.setText("Laying");
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

    public void createCircleMenu() {

        // Set up the large red button on the center right side
        // With custom button and content sizes and margins
        int redActionButtonSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_size);
        int redActionButtonMargin = getResources().getDimensionPixelOffset(R.dimen.action_button_margin);
        int redActionButtonContentSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_size);
        int redActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_margin);
        int redActionMenuRadius = getResources().getDimensionPixelSize(R.dimen.red_action_menu_radius);
        int blueSubActionButtonSize = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size);
        int blueSubActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_content_margin);

        ImageView fabIconStar = new ImageView(this);
        fabIconStar.setImageDrawable(getResources().getDrawable(R.drawable.ic_touch_app_white_24dp));

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

        leftCenterButton = new FloatingActionButtonFlexibleActions.Builder(this)
                .setContentView(fabIconStar, fabIconStarParams)
                .setBackgroundDrawable(R.drawable.button_action_red_selector)
                .setPosition(FloatingActionButtonFlexibleActions.POSITION_CENTER)
                .setLayoutParams(starParams)
                .build();

        final GestureDetector gestureDetector = new GestureDetector(this, new MyGestureDetector());

        leftCenterButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return gestureDetector.onTouchEvent(event);

            }
        });

        // Set up customized SubActionButtons for the right center menu
        SubActionButton.Builder lCSubBuilder = new SubActionButton.Builder(this);
        lCSubBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_action_blue_selector));

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

        ImageView lcIcon1 = new ImageView(this);
        ImageView lcIcon2 = new ImageView(this);
        ImageView lcIcon3 = new ImageView(this);
        ImageView lcIcon4 = new ImageView(this);
        ImageView lcIcon5 = new ImageView(this);
        ImageView lcIcon6 = new ImageView(this);

        lcIcon1.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_walk_white_24dp));
        lcIcon2.setImageDrawable(getResources().getDrawable(R.drawable.ic_trending_up_white_24dp));
        lcIcon3.setImageDrawable(getResources().getDrawable(R.drawable.ic_trending_down_white_24dp));
        lcIcon4.setImageDrawable(getResources().getDrawable(R.drawable.ic_airline_seat_recline_normal_white_24dp));
        lcIcon5.setImageDrawable(getResources().getDrawable(R.drawable.ic_accessibility_white_24dp));
        lcIcon6.setImageDrawable(getResources().getDrawable(R.drawable.ic_local_hotel_white_24dp));

        SubActionButton buttonOnWalking = lCSubBuilder.setContentView(lcIcon1, blueContentParams).build();
        buttonOnWalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftCenterButton.performClick();

                bPause.setVisibility(View.VISIBLE);

                mGenericActivity.setVisibility(View.VISIBLE);

                leftCenterButton.setVisibility(View.INVISIBLE);

                onWalking();
                Toast toast = Toast.makeText(MainActivity.this, "Walking", Toast.LENGTH_SHORT);
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

                onUpstairs();
                Toast toast = Toast.makeText(MainActivity.this, "Walking upstairs", Toast.LENGTH_SHORT);
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

                onDownstairs();
                Toast toast = Toast.makeText(MainActivity.this, "Waking downstairs", Toast.LENGTH_SHORT);
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

                onSitting();
                Toast toast = Toast.makeText(MainActivity.this, "Sitting", Toast.LENGTH_SHORT);
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

                onStanding();
                Toast toast = Toast.makeText(MainActivity.this, "Standing", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        SubActionButton buttonOnLaying = lCSubBuilder.setContentView(lcIcon6, blueContentParams).build();
        buttonOnLaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftCenterButton.performClick();

                bPause.setVisibility(View.VISIBLE);

                mGenericActivity.setVisibility(View.VISIBLE);

                leftCenterButton.setVisibility(View.INVISIBLE);

                onLaying();
                Toast toast = Toast.makeText(MainActivity.this, "Laying", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        // Build another menu with custom options
        final FloatingActionMenu leftCenterMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(buttonOnWalking)
                .addSubActionView(buttonOnWalkingUpstairs)
                .addSubActionView(buttonOnWalkingDownstairs)
                .addSubActionView(buttonOnSitting)
                .addSubActionView(buttonOnStanding)
                .addSubActionView(buttonOnLaying)
                .setRadius(redActionMenuRadius)
                .setStartAngle(0)
                .setEndAngle(360)
                .attachTo(leftCenterButton)
                .build();

    }

    public void showCustomDialog(View view) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText(getResources().getString(R.string.dialog_title));

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
                final Toast toast = Toast.makeText(MainActivity.this, "Have a nice day, my wonderful friend", Toast.LENGTH_SHORT);

                // center text in toast message
                TextView tView = (TextView) toast.getView().findViewById(android.R.id.message);
                if (tView != null) tView.setGravity(Gravity.CENTER);

                toast.show();

                setResult(RESULT_OK);
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
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

        attributes.add(new Attribute("AccelXMin", numAttrib++));
        attributes.add(new Attribute("AccelYMin", numAttrib++));
        attributes.add(new Attribute("AccelZMin", numAttrib++));
        attributes.add(new Attribute("AccelXMax", numAttrib++));
        attributes.add(new Attribute("AccelYMax", numAttrib++));
        attributes.add(new Attribute("AccelZMax", numAttrib++));
        attributes.add(new Attribute("AccelXMean", numAttrib++));
        attributes.add(new Attribute("AccelYMean", numAttrib++));
        attributes.add(new Attribute("AccelZMean", numAttrib++));
        attributes.add(new Attribute("AccelXRange", numAttrib++));
        attributes.add(new Attribute("AccelYRange", numAttrib++));
        attributes.add(new Attribute("AccelZRange", numAttrib++));
        attributes.add(new Attribute("AccelXStd", numAttrib++));
        attributes.add(new Attribute("AccelYStd", numAttrib++));
        attributes.add(new Attribute("AccelZStd", numAttrib++));

        attributes.add(new Attribute("GyroXMin", numAttrib++));
        attributes.add(new Attribute("GyroYMin", numAttrib++));
        attributes.add(new Attribute("GyroZMin", numAttrib++));
        attributes.add(new Attribute("GyroXMax", numAttrib++));
        attributes.add(new Attribute("GyroYMax", numAttrib++));
        attributes.add(new Attribute("GyroZMax", numAttrib++));
        attributes.add(new Attribute("GyroXMean", numAttrib++));
        attributes.add(new Attribute("GyroYMean", numAttrib++));
        attributes.add(new Attribute("GyroZMean", numAttrib++));
        attributes.add(new Attribute("GyroXRange", numAttrib++));
        attributes.add(new Attribute("GyroYRange", numAttrib++));
        attributes.add(new Attribute("GyroZRange", numAttrib++));
        attributes.add(new Attribute("GyroXStd", numAttrib++));
        attributes.add(new Attribute("GyroYStd", numAttrib++));
        attributes.add(new Attribute("GyroZStd", numAttrib++));

        attributes.add(new Attribute("GravityXMin", numAttrib++));
        attributes.add(new Attribute("GravityYMin", numAttrib++));
        attributes.add(new Attribute("GravityZMin", numAttrib++));
        attributes.add(new Attribute("GravityXMax", numAttrib++));
        attributes.add(new Attribute("GravityYMax", numAttrib++));
        attributes.add(new Attribute("GravityZMax", numAttrib++));
        attributes.add(new Attribute("GravityXMean", numAttrib++));
        attributes.add(new Attribute("GravityYMean", numAttrib++));
        attributes.add(new Attribute("GravityZMean", numAttrib++));
        attributes.add(new Attribute("GravityXRange", numAttrib++));
        attributes.add(new Attribute("GravityYRange", numAttrib++));
        attributes.add(new Attribute("GravityZRange", numAttrib++));
        attributes.add(new Attribute("GravityXStd", numAttrib++));
        attributes.add(new Attribute("GravityYStd", numAttrib++));
        attributes.add(new Attribute("GravityZStd", numAttrib++));

        attributes.add(new Attribute("LinAccelXMin", numAttrib++));
        attributes.add(new Attribute("LinAccelYMin", numAttrib++));
        attributes.add(new Attribute("LinAccelZMin", numAttrib++));
        attributes.add(new Attribute("LinAccelXMax", numAttrib++));
        attributes.add(new Attribute("LinAccelYMax", numAttrib++));
        attributes.add(new Attribute("LinAccelZMax", numAttrib++));
        attributes.add(new Attribute("LinAccelXMean", numAttrib++));
        attributes.add(new Attribute("LinAccelYMean", numAttrib++));
        attributes.add(new Attribute("LinAccelZMean", numAttrib++));
        attributes.add(new Attribute("LinAccelXRange", numAttrib++));
        attributes.add(new Attribute("LinAccelYRange", numAttrib++));
        attributes.add(new Attribute("LinAccelZRange", numAttrib++));
        attributes.add(new Attribute("LinAccelXStd", numAttrib++));
        attributes.add(new Attribute("LinAccelYStd", numAttrib++));
        attributes.add(new Attribute("LinAccelZStd", numAttrib++));

        attributes.add(new Attribute("RotVecXMin", numAttrib++));
        attributes.add(new Attribute("RotVecYMin", numAttrib++));
        attributes.add(new Attribute("RotVecSMin", numAttrib++));
        attributes.add(new Attribute("RotVecXMax", numAttrib++));
        attributes.add(new Attribute("RotVecYMax", numAttrib++));
        attributes.add(new Attribute("RotVecSMax", numAttrib++));
        attributes.add(new Attribute("RotVecXMean", numAttrib++));
        attributes.add(new Attribute("RotVecYMean", numAttrib++));
        attributes.add(new Attribute("RotVecSMean", numAttrib++));
        attributes.add(new Attribute("RotVecXRange", numAttrib++));
        attributes.add(new Attribute("RotVecYRange", numAttrib++));
        attributes.add(new Attribute("RotVecSRange", numAttrib++));
        attributes.add(new Attribute("RotVecXStd", numAttrib++));
        attributes.add(new Attribute("RotVecYStd", numAttrib++));
        attributes.add(new Attribute("RotVecSStd", numAttrib++));

        attributes.add(new Attribute("AiPreValMin", numAttrib++));
        attributes.add(new Attribute("AiPreValMax", numAttrib++));
        attributes.add(new Attribute("AiPreValMean", numAttrib++));
        attributes.add(new Attribute("AiPreValRange", numAttrib++));
        attributes.add(new Attribute("AiPreValStd", numAttrib++));

        attributes.add(new Attribute("MagFielYMin", numAttrib++));
        attributes.add(new Attribute("MagFielYMax", numAttrib++));
        attributes.add(new Attribute("MagFielYMean", numAttrib++));
        attributes.add(new Attribute("MagFielYRange", numAttrib++));
        attributes.add(new Attribute("MagFielYStd", numAttrib++));

        attributes.add(new Attribute("HeartRateValMin", numAttrib++));
        attributes.add(new Attribute("HeartRateValMax", numAttrib++));
        attributes.add(new Attribute("HeartRateValMean", numAttrib++));
        attributes.add(new Attribute("HeartRateValRange", numAttrib++));
        attributes.add(new Attribute("HeartRateValStd", numAttrib++));

        List<String> values = getActivityValues();
        attributes.add(new Attribute("Activity", values, numAttrib));

        return attributes;
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

    @Override
    public void onBackPressed() {
        doExit();
    }

    public DenseInstance getDenseInstances(int numOfAttributes, int pos) {
        double[] attributeValues = new double[numOfAttributes];
        int currentAttNumber = 0;

        if (computeComplexFeatures) {
            attributeValues[currentAttNumber++] = accelXMin;
            attributeValues[currentAttNumber++] = accelYMin;
            attributeValues[currentAttNumber++] = accelZMin;
            attributeValues[currentAttNumber++] = accelXMax;
            attributeValues[currentAttNumber++] = accelYMax;
            attributeValues[currentAttNumber++] = accelZMax;
            attributeValues[currentAttNumber++] = accelXMean;
            attributeValues[currentAttNumber++] = accelYMean;
            attributeValues[currentAttNumber++] = accelZMean;
            attributeValues[currentAttNumber++] = accelXRange;
            attributeValues[currentAttNumber++] = accelYRange;
            attributeValues[currentAttNumber++] = accelZRange;
            attributeValues[currentAttNumber++] = accelXStd;
            attributeValues[currentAttNumber++] = accelYStd;
            attributeValues[currentAttNumber++] = accelZStd;

            attributeValues[currentAttNumber++] = gyroXMin;
            attributeValues[currentAttNumber++] = gyroYMin;
            attributeValues[currentAttNumber++] = gyroZMin;
            attributeValues[currentAttNumber++] = gyroXMax;
            attributeValues[currentAttNumber++] = gyroYMax;
            attributeValues[currentAttNumber++] = gyroZMax;
            attributeValues[currentAttNumber++] = gyroXMean;
            attributeValues[currentAttNumber++] = gyroYMean;
            attributeValues[currentAttNumber++] = gyroZMean;
            attributeValues[currentAttNumber++] = gyroXRange;
            attributeValues[currentAttNumber++] = gyroYRange;
            attributeValues[currentAttNumber++] = gyroZRange;
            attributeValues[currentAttNumber++] = gyroXStd;
            attributeValues[currentAttNumber++] = gyroYStd;
            attributeValues[currentAttNumber++] = gyroZStd;

            attributeValues[currentAttNumber++] = gravityXMin;
            attributeValues[currentAttNumber++] = gravityYMin;
            attributeValues[currentAttNumber++] = gravityZMin;
            attributeValues[currentAttNumber++] = gravityXMax;
            attributeValues[currentAttNumber++] = gravityYMax;
            attributeValues[currentAttNumber++] = gravityZMax;
            attributeValues[currentAttNumber++] = gravityXMean;
            attributeValues[currentAttNumber++] = gravityYMean;
            attributeValues[currentAttNumber++] = gravityZMean;
            attributeValues[currentAttNumber++] = gravityXRange;
            attributeValues[currentAttNumber++] = gravityYRange;
            attributeValues[currentAttNumber++] = gravityZRange;
            attributeValues[currentAttNumber++] = gravityXStd;
            attributeValues[currentAttNumber++] = gravityYStd;
            attributeValues[currentAttNumber++] = gravityZStd;

            attributeValues[currentAttNumber++] = linAccelXMin;
            attributeValues[currentAttNumber++] = linAccelYMin;
            attributeValues[currentAttNumber++] = linAccelZMin;
            attributeValues[currentAttNumber++] = linAccelXMax;
            attributeValues[currentAttNumber++] = linAccelYMax;
            attributeValues[currentAttNumber++] = linAccelZMax;
            attributeValues[currentAttNumber++] = linAccelXMean;
            attributeValues[currentAttNumber++] = linAccelYMean;
            attributeValues[currentAttNumber++] = linAccelZMean;
            attributeValues[currentAttNumber++] = linAccelXRange;
            attributeValues[currentAttNumber++] = linAccelYRange;
            attributeValues[currentAttNumber++] = linAccelZRange;
            attributeValues[currentAttNumber++] = linAccelXStd;
            attributeValues[currentAttNumber++] = linAccelYStd;
            attributeValues[currentAttNumber++] = linAccelZStd;

            attributeValues[currentAttNumber++] = rotVecXMin;
            attributeValues[currentAttNumber++] = rotVecYMin;
            attributeValues[currentAttNumber++] = rotVecSMin;
            attributeValues[currentAttNumber++] = rotVecXMax;
            attributeValues[currentAttNumber++] = rotVecYMax;
            attributeValues[currentAttNumber++] = rotVecSMax;
            attributeValues[currentAttNumber++] = rotVecXMean;
            attributeValues[currentAttNumber++] = rotVecYMean;
            attributeValues[currentAttNumber++] = rotVecSMean;
            attributeValues[currentAttNumber++] = rotVecXRange;
            attributeValues[currentAttNumber++] = rotVecYRange;
            attributeValues[currentAttNumber++] = rotVecSRange;
            attributeValues[currentAttNumber++] = rotVecXStd;
            attributeValues[currentAttNumber++] = rotVecYStd;
            attributeValues[currentAttNumber++] = rotVecSStd;

            attributeValues[currentAttNumber++] = aiPreValMin;
            attributeValues[currentAttNumber++] = aiPreValMax;
            attributeValues[currentAttNumber++] = aiPreValMean;
            attributeValues[currentAttNumber++] = aiPreValRange;
            attributeValues[currentAttNumber++] = aiPreValStd;

            attributeValues[currentAttNumber++] = magFielYMin;
            attributeValues[currentAttNumber++] = magFielYMax;
            attributeValues[currentAttNumber++] = magFielYMean;
            attributeValues[currentAttNumber++] = magFielYRange;
            attributeValues[currentAttNumber++] = magFielYStd;

            attributeValues[currentAttNumber++] = heartRateValMin;
            attributeValues[currentAttNumber++] = heartRateValMax;
            attributeValues[currentAttNumber++] = heartRateValMean;
            attributeValues[currentAttNumber++] = heartRateValRange;
            attributeValues[currentAttNumber++] = heartRateValStd;

            List<String> activityValues = getActivityValues();
            if (appState == 1) {
                // any value for a place of activity
                attributeValues[currentAttNumber] = activityValues.indexOf(activityValues.get(0));
            }
        }

        return new DenseInstance(1.0, attributeValues);
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

    private List<String> getActivityValues() {
        List<String> values = new ArrayList<String>();
        values.add("0");
        values.add("1");
        values.add("2");
        values.add("3");
        values.add("4");
        values.add("5");

        return values;
    }

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                if (bStop.getVisibility() == View.VISIBLE) {
                    mGenericActivity.setVisibility(View.INVISIBLE);
                    bStop.setVisibility(View.INVISIBLE);
                } else {
                    if (!(mGenericActivity.getText().toString().equals(getResources().getString(R.string.new_activity)))) {
                        mGenericActivity.setVisibility(View.VISIBLE);
                    }
                    bStop.setVisibility(View.VISIBLE);
                }
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            showCustomDialog(bStop);
        }

    }

}
