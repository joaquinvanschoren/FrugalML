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
import com.project.frugalmachinelearning.tools.FileOperations;

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

    private SensorManager mSensorManager;
    private Map<String, Double> mResults = new LinkedHashMap<String, Double>();

    private Thread t;
    private Thread tClassifyActivity;

    private boolean needTitle = true;

    private AbstractClassifier selectedClassifier;
    private DenseInstance[] instances = new DenseInstance[1];
    private int posInstance;
    private boolean warmingUp;
    private int performingActivity;

    private TextView mActivityTextView;
    private TextView mTime;
    private TextView mNewActivity;

    private PrintWriter pw;


    boolean firstRun = true;



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
                        Thread.sleep(1000/16);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (isExternalStorageWritable() && mResults.size() >= AMOUNT_OF_ATTRIBUTES - 1) {

                                        StringBuilder allSensorsData = new StringBuilder();

                                        DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS dd/MM/yyyy");
                                        Date current = new Date();
                                        allSensorsData.append(df.format(current)).append(",");

                                        if (needTitle) {
                                            StringBuilder title = new StringBuilder();
                                            title.append("Time,");

                                            for (Map.Entry<String, Double> entry : mResults.entrySet()) {
                                                title.append(entry.getKey()).append(",");
                                            }

                                            title.append("Activity");
                                            pw.println(title.toString());
                                            needTitle = false;

                                            if (performingActivity == 6) {
                                                mNewActivity.setText("New activity is empty");
                                            }
                                        }

                                        for (Map.Entry<String, Double> sensorValue : mResults.entrySet()) {
                                            allSensorsData.append(sensorValue.getValue());
                                            allSensorsData.append(",");
                                        }

                                        allSensorsData.append(performingActivity);
                                        pw.println(allSensorsData.toString());



/*
                                        InputStream insValues = getResources().openRawResource(getResources().getIdentifier("measurements",
                                                "raw", getPackageName()));

                                        int stableValue = InstancesSaved.getLabelFromSavedFirstInstances(insValues, selectedClassifier);
                                        Log.i(TAG, String.valueOf(stableValue));
*/


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
                                DenseInstance instance = getDenseInstances(AMOUNT_OF_ATTRIBUTES);

                                instances[posInstance] = instance;
                                posInstance++;
                                if (posInstance == instances.length) {
                                    posInstance = 0;
                                }

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

                                Date current = new Date();
                                mTime.setText(new SimpleDateFormat("HH:mm:ss.SSS").format(current) + " update time");

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
        super.onDestroy();

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

        Log.i(TAG, "Activity was stopped");

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

        if (firstRun) {
            try {
                Random random = new Random();
                int fileNumber = random.nextInt(100);
                final String sensorDataName = FileOperations.getSensorStorageDir("SensorsInformation") + "/measurements" + fileNumber + ".txt";
                FileOperations.deleteFile(sensorDataName);
                final File sensorData = new File(sensorDataName);

                pw = new PrintWriter(new BufferedWriter(new FileWriter(sensorData, true)));

                Log.i(TAG, sensorDataName);

            }
            catch (IOException e) {
                e.printStackTrace();
            }

            // clean old files with random results
            FileOperations.deleteFile("/storage/emulated/0/myfile_nbp.txt");
            FileOperations.deleteFile("myfile_nbp.txt");

            // create classifier from a file
            String selectedClassifierName = "HyperPipes";
            FactoryClassifiers fc = new FactoryClassifiers();
            String modelFileName = fc.getModelFile(selectedClassifierName);
            InputStream ins = getResources().openRawResource(getResources().getIdentifier(modelFileName, "raw", getPackageName()));
            selectedClassifier = fc.getModel(selectedClassifierName, ins);

            posInstance = 0;
            warmingUp = true;
            setSensors();

            performingActivity = 6;

//            launchCollectingInformation();

            launchingRecognitionActivities();

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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            double ax = Math.round(event.values[0] * 100000) / 100000.0;
            double ay = Math.round(event.values[1] * 100000) / 100000.0;
            double az = Math.round(event.values[2] * 100000) / 100000.0;

            mResults.put("AccelX", ax);
            mResults.put("AccelY", ay);
            mResults.put("AccelZ", az);
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            double ax = Math.round(event.values[0] * 1000) / 1000.0;
            double ay = Math.round(event.values[1] * 1000) / 1000.0;
            double az = Math.round(event.values[2] * 1000) / 1000.0;

            mResults.put("GyroX", ax);
            mResults.put("GyroY", ay);
            mResults.put("GyroZ", az);
        }

        if (event.sensor.getType() == Sensor.TYPE_GRAVITY){
            double ax = Math.round(event.values[0] * 100000) / 100000.0;
            double ay = Math.round(event.values[1] * 100000) / 100000.0;
            double az = Math.round(event.values[2] * 100000) / 100000.0;

            mResults.put("GravityX", ax);
            mResults.put("GravityY", ay);
            mResults.put("GravityZ", az);
        }

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            double ax = Math.round(event.values[0] * 1000) / 1000.0;
            double ay = Math.round(event.values[1] * 1000) / 1000.0;
            double az = Math.round(event.values[2] * 1000) / 1000.0;

            mResults.put("LinAccelX", ax);
            mResults.put("LinAccelY", ay);
            mResults.put("LinAccelZ", az);
        }

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            double ax = Math.round(event.values[0] * 100000) / 100000.0;
            double ay = Math.round(event.values[1] * 100000) / 100000.0;
            double az = Math.round(event.values[2] * 100000) / 100000.0;
            double as = Math.round(event.values[2] * 100000) / 100000.0;

            mResults.put("RotVecX", ax);
            mResults.put("RotVecY", ay);
            mResults.put("RotVecZ", az);
            mResults.put("RotVecS", as);
        }

        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            double ax = Math.round(event.values[0] * 100000) / 100000.0;

            mResults.put("StDetVal", ax);
        }

        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            double ax = Math.round(event.values[0] * 100000) / 100000.0;

            mResults.put("AiPreVal", ax);
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            double ax = Math.round(event.values[0] * 100000) / 100000.0;
            double ay = Math.round(event.values[1] * 100000) / 100000.0;
            double az = Math.round(event.values[2] * 100000) / 100000.0;

            mResults.put("MagFielX", ax);
            mResults.put("MagFielY", ay);
            mResults.put("MagFielZ", az);
        }

        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE){
            double ax = Math.round(event.values[0] * 100000) / 100000.0;

            mResults.put("HeartRateVal", ax);
        }

        if (warmingUp) {
            mResults.put("StDetVal", 0.0);
            mResults.put("HeartRateVal", Double.NaN);
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
    }

    public void onUpstairs(View view) {
        performingActivity = ActivityType.valueOf("WALKING_UPSTAIRS").ordinal();
        mNewActivity.setText("New activity is upstairs");
    }

    public void onDownstairs(View view) {
        performingActivity = ActivityType.valueOf("WALKING_DOWNSTAIRS").ordinal();
        mNewActivity.setText("New activity is downstairs");
    }

    public void onSitting(View view) {
        performingActivity = ActivityType.valueOf("SITTING").ordinal();
        mNewActivity.setText("New activity is sitting");
    }

    public void onStanding(View view) {
        performingActivity = ActivityType.valueOf("STANDING").ordinal();
        mNewActivity.setText("New activity is standing");
    }

    public void onLaying(View view) {
        performingActivity = ActivityType.valueOf("LAYING").ordinal();
        mNewActivity.setText("New activity is laying");
    }

    public void onEmpty(View view) {
        performingActivity = 6;
        mNewActivity.setText("New activity is empty");
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
        int numOfAttributes = mResults.size();
        int numAttrib = 0;
        ArrayList<Attribute> attributes = new ArrayList<Attribute>(numOfAttributes + 1);

        if (mResults.size() != 0) {

            for (Map.Entry<String, Double> entry : mResults.entrySet()) {
                String key = entry.getKey();
                attributes.add(new Attribute(key, numAttrib));
                numAttrib++;
            }

            List<String> values = getActivityValues();
            String key = "Activity";

            attributes.add(new Attribute(key, values, numAttrib));
        }

        return attributes;
    }

    public DenseInstance getDenseInstances(int numOfAttributes) {
        double[] attributeValues = new double[numOfAttributes];

        int currentAttNumber = 0;

        for (Map.Entry<String, Double> entry : mResults.entrySet()) {
            Double value = entry.getValue();

            attributeValues[currentAttNumber] = value;
            currentAttNumber++;
        }

        List<String> activityValues = getActivityValues();
        attributeValues[currentAttNumber] = activityValues.indexOf(activityValues.get(performingActivity));

        return  new DenseInstance(1.0, attributeValues);
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
