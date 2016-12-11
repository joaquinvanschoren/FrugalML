package com.project.frugalmachinelearning.tools;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import com.project.frugalmachinelearning.MainActivity;

import java.util.List;

import jflex.Main;

/**
 * Created by Mikhail on 20.11.2016.
 */
public class SensorsActions {

    public static void setSensors(MainActivity activity, SensorManager mSensorManager) {

        List<Sensor> mList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        mSensorManager.registerListener(activity,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        mSensorManager.registerListener(activity,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        mSensorManager.registerListener(activity,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        mSensorManager.registerListener(activity,
                mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        mSensorManager.registerListener(activity,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        mSensorManager.registerListener(activity,
                mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE),
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        mSensorManager.registerListener(activity,
                mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        mSensorManager.registerListener(activity,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        // print the amount of sensors and short information
        Log.i(MainActivity.TAG, String.valueOf(mList.size()));
        for (int i = 0; i < mList.size(); i++) {
            Log.i(MainActivity.TAG, "\t" + i + " " + mList.get(i).getName());
            Log.i(MainActivity.TAG, "\t" + i + " " + mList.get(i).toString());
        }

    }

}
