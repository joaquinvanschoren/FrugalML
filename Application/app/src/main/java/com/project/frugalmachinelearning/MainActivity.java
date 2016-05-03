package com.project.frugalmachinelearning;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.project.frugalmachinelearning.classifiers.ActivityType;
import com.project.frugalmachinelearning.classifiers.ActivityWindow;
import com.project.frugalmachinelearning.classifiers.FactoryClassifiers;
import com.project.frugalmachinelearning.tools.FileOperations;
import com.project.frugalmachinelearning.tools.InstancesSaved;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

    private AbstractClassifier selectedClassifier = null;
    private DenseInstance[] instances = new DenseInstance[AMOUNT_OF_ATTRIBUTES + 2];
    private int posInstance;
    private boolean warmingUp;
    private int performingActivity;

    private TextView mActivityTextView;
    private TextView mTime;
    private TextView mNewActivity;

    private PrintWriter pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mActivityTextView = (TextView) stub.findViewById(R.id.mActivityTextView);
                mTime = (TextView) stub.findViewById(R.id.mTime);
                mNewActivity = (TextView) stub.findViewById(R.id.mNewActivity);
            }
        });

        setAmbientEnabled();

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

                                        mTime.setText(new SimpleDateFormat("HH:mm:ss.SSS").format(current) + " update time");

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
                                        DenseInstance instance = getDenseInstances(AMOUNT_OF_ATTRIBUTES);

                                        instances[posInstance] = instance;
                                        posInstance++;
                                        if (posInstance == instances.length) {
                                            posInstance = 0;
                                        }
*/


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

                                mActivityTextView.setText("is " + activityFullName);

                                Log.i(TAG, String.valueOf(ActivityType.valueOf(activityFullName)));
                                Log.i(TAG, activityFullName);
                            }

                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

//         tClassifyActivity.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        t.interrupt();
        tClassifyActivity.interrupt();

        pw.close();

        mSensorManager = null;

        Log.i(TAG, "Activity was stopped");
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);

    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();

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
        performingActivity = 0;
        mNewActivity.setText("New activity is walking");
    }

    public void onUpstairs(View view) {
        performingActivity = 1;
        mNewActivity.setText("New activity is upstairs");}

    public void onDownstairs(View view) {
        performingActivity = 2;
        mNewActivity.setText("New activity is downstairs");
    }

    public void onSitting(View view) {
        performingActivity = 3;
        mNewActivity.setText("New activity is sitting");
    }

    public void onStanding(View view) {
        performingActivity = 4;
        mNewActivity.setText("New activity is standing");
    }

    public void onLaying(View view) {
        performingActivity = 5;
        mNewActivity.setText("New activity is laying");
    }

    public void onEmpty(View view) {
        performingActivity = 6;
        mNewActivity.setText("New activity is empty");
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
