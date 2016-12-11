package com.project.frugalmachinelearning.tools;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

import com.project.frugalmachinelearning.external.MathStuff;

/**
 * Created by Mikhail on 20.11.2016.
 */
public class StorageDataActions {

    public static final MathStuff MATH_STUFF = new MathStuff();

    public static void sensorsFeaturesToArrays(StorageData measurements, StorageCurrentData newMeasurement, int pos) {
        measurements.accelXArray[pos] = newMeasurement.accelX;
        measurements.accelYArray[pos] = newMeasurement.accelY;
        measurements.accelZArray[pos] = newMeasurement.accelZ;

        measurements.gyroXArray[pos] = newMeasurement.gyroX;
        measurements.gyroYArray[pos] = newMeasurement.gyroY;
        measurements.gyroZArray[pos] = newMeasurement.gyroZ;

        measurements.gravityXArray[pos] = newMeasurement.gravityX;
        measurements.gravityYArray[pos] = newMeasurement.gravityY;
        measurements.gravityZArray[pos] = newMeasurement.gravityZ;

        measurements.linAccelXArray[pos] = newMeasurement.linAccelX;
        measurements.linAccelYArray[pos] = newMeasurement.linAccelY;
        measurements.linAccelZArray[pos] = newMeasurement.linAccelZ;

        measurements.rotVecXArray[pos] = newMeasurement.rotVecX;
        measurements.rotVecYArray[pos] = newMeasurement.rotVecY;
        measurements.rotVecSArray[pos] = newMeasurement.rotVecS;

        measurements.aiPreValArray[pos] = newMeasurement.aiPreVal;

        measurements.magFielYArray[pos] = newMeasurement.magFielY;

        measurements.heartRateValArray[pos] = newMeasurement.heartRateVal;
    }

    public static void computeAdditionalFeatures(StorageData measurements) {
        measurements.accelXMin = MATH_STUFF.getMin(measurements.accelXArray);
        measurements.accelYMin = MATH_STUFF.getMin(measurements.accelYArray);
        measurements.accelZMin = MATH_STUFF.getMin(measurements.accelZArray);
        measurements.accelXMax = MATH_STUFF.getMax(measurements.accelXArray);
        measurements.accelYMax = MATH_STUFF.getMax(measurements.accelYArray);
        measurements.accelZMax = MATH_STUFF.getMax(measurements.accelZArray);
        measurements.accelXMean = MATH_STUFF.getMean(measurements.accelXArray);
        measurements.accelYMean = MATH_STUFF.getMean(measurements.accelYArray);
        measurements.accelZMean = MATH_STUFF.getMean(measurements.accelZArray);
        measurements.accelXRange = measurements.accelXMax - measurements.accelXMin;
        measurements.accelYRange = measurements.accelYMax - measurements.accelYMin;
        measurements.accelZRange = measurements.accelZMax - measurements.accelZMin;
        measurements.accelXStd = MATH_STUFF.getStdDev(measurements.accelXArray);
        measurements.accelYStd = MATH_STUFF.getStdDev(measurements.accelYArray);
        measurements.accelZStd = MATH_STUFF.getStdDev(measurements.accelZArray);

        measurements.gyroXMin = MATH_STUFF.getMin(measurements.gyroXArray);
        measurements.gyroYMin = MATH_STUFF.getMin(measurements.gyroYArray);
        measurements.gyroZMin = MATH_STUFF.getMin(measurements.gyroZArray);
        measurements.gyroXMax = MATH_STUFF.getMax(measurements.gyroXArray);
        measurements.gyroYMax = MATH_STUFF.getMax(measurements.gyroYArray);
        measurements.gyroZMax = MATH_STUFF.getMax(measurements.gyroZArray);
        measurements.gyroXMean = MATH_STUFF.getMean(measurements.gyroXArray);
        measurements.gyroYMean = MATH_STUFF.getMean(measurements.gyroYArray);
        measurements.gyroZMean = MATH_STUFF.getMean(measurements.gyroZArray);
        measurements.gyroXRange = measurements.gyroXMax - measurements.gyroXMin;
        measurements.gyroYRange = measurements.gyroYMax - measurements.gyroYMin;
        measurements.gyroZRange = measurements.gyroZMax - measurements.gyroZMin;
        measurements.gyroXStd = MATH_STUFF.getStdDev(measurements.gyroXArray);
        measurements.gyroYStd = MATH_STUFF.getStdDev(measurements.gyroYArray);
        measurements.gyroZStd = MATH_STUFF.getStdDev(measurements.gyroZArray);

        measurements.gravityXMin = MATH_STUFF.getMin(measurements.gravityXArray);
        measurements.gravityYMin = MATH_STUFF.getMin(measurements.gravityYArray);
        measurements.gravityZMin = MATH_STUFF.getMin(measurements.gravityZArray);
        measurements.gravityXMax = MATH_STUFF.getMax(measurements.gravityXArray);
        measurements.gravityYMax = MATH_STUFF.getMax(measurements.gravityYArray);
        measurements.gravityZMax = MATH_STUFF.getMax(measurements.gravityZArray);
        measurements.gravityXMean = MATH_STUFF.getMean(measurements.gravityXArray);
        measurements.gravityYMean = MATH_STUFF.getMean(measurements.gravityYArray);
        measurements.gravityZMean = MATH_STUFF.getMean(measurements.gravityZArray);
        measurements.gravityXRange = measurements.gravityXMax - measurements.gravityXMin;
        measurements.gravityYRange = measurements.gravityYMax - measurements.gravityYMin;
        measurements.gravityZRange = measurements.gravityZMax - measurements.gravityZMin;
        measurements.gravityXStd = MATH_STUFF.getStdDev(measurements.gravityXArray);
        measurements.gravityYStd = MATH_STUFF.getStdDev(measurements.gravityYArray);
        measurements.gravityZStd = MATH_STUFF.getStdDev(measurements.gravityZArray);

        measurements.linAccelXMin = MATH_STUFF.getMin(measurements.linAccelXArray);
        measurements.linAccelYMin = MATH_STUFF.getMin(measurements.linAccelYArray);
        measurements.linAccelZMin = MATH_STUFF.getMin(measurements.linAccelZArray);
        measurements.linAccelXMax = MATH_STUFF.getMax(measurements.linAccelXArray);
        measurements.linAccelYMax = MATH_STUFF.getMax(measurements.linAccelYArray);
        measurements.linAccelZMax = MATH_STUFF.getMax(measurements.linAccelZArray);
        measurements.linAccelXMean = MATH_STUFF.getMean(measurements.linAccelXArray);
        measurements.linAccelYMean = MATH_STUFF.getMean(measurements.linAccelYArray);
        measurements.linAccelZMean = MATH_STUFF.getMean(measurements.linAccelZArray);
        measurements.linAccelXRange = measurements.linAccelXMax - measurements.linAccelXMin;
        measurements.linAccelYRange = measurements.linAccelYMax - measurements.linAccelYMin;
        measurements.linAccelZRange = measurements.linAccelZMax - measurements.linAccelZMin;
        measurements.linAccelXStd = MATH_STUFF.getStdDev(measurements.linAccelXArray);
        measurements.linAccelYStd = MATH_STUFF.getStdDev(measurements.linAccelYArray);
        measurements.linAccelZStd = MATH_STUFF.getStdDev(measurements.linAccelZArray);

        measurements.rotVecXMin = MATH_STUFF.getMin(measurements.rotVecXArray);
        measurements.rotVecYMin = MATH_STUFF.getMin(measurements.rotVecYArray);
        measurements.rotVecSMin = MATH_STUFF.getMin(measurements.rotVecSArray);
        measurements.rotVecXMax = MATH_STUFF.getMax(measurements.rotVecXArray);
        measurements.rotVecYMax = MATH_STUFF.getMax(measurements.rotVecYArray);
        measurements.rotVecSMax = MATH_STUFF.getMax(measurements.rotVecSArray);
        measurements.rotVecXMean = MATH_STUFF.getMean(measurements.rotVecXArray);
        measurements.rotVecYMean = MATH_STUFF.getMean(measurements.rotVecYArray);
        measurements.rotVecSMean = MATH_STUFF.getMean(measurements.rotVecSArray);
        measurements.rotVecXRange = measurements.rotVecXMax - measurements.rotVecXMin;
        measurements.rotVecYRange = measurements.rotVecYMax - measurements.rotVecYMin;
        measurements.rotVecSRange = measurements.rotVecSMax - measurements.rotVecSMin;
        measurements.rotVecXStd = MATH_STUFF.getStdDev(measurements.rotVecXArray);
        measurements.rotVecYStd = MATH_STUFF.getStdDev(measurements.rotVecYArray);
        measurements.rotVecSStd = MATH_STUFF.getStdDev(measurements.rotVecSArray);

        measurements.aiPreValMin = MATH_STUFF.getMin(measurements.aiPreValArray);
        measurements.aiPreValMax = MATH_STUFF.getMax(measurements.aiPreValArray);
        measurements.aiPreValMean = MATH_STUFF.getMean(measurements.aiPreValArray);
        measurements.aiPreValRange = measurements.aiPreValMax - measurements.aiPreValMin;
        measurements.aiPreValStd = MATH_STUFF.getStdDev(measurements.aiPreValArray);

        measurements.magFielYMin = MATH_STUFF.getMin(measurements.magFielYArray);
        measurements.magFielYMax = MATH_STUFF.getMax(measurements.magFielYArray);
        measurements.magFielYMean = MATH_STUFF.getMean(measurements.magFielYArray);
        measurements.magFielYRange = measurements.magFielYMax - measurements.magFielYMin;
        measurements.magFielYStd = MATH_STUFF.getStdDev(measurements.magFielYArray);

        measurements.heartRateValMin = MATH_STUFF.getMin(measurements.heartRateValArray);
        measurements.heartRateValMax = MATH_STUFF.getMax(measurements.heartRateValArray);
        measurements.heartRateValMean = MATH_STUFF.getMean(measurements.heartRateValArray);
        measurements.heartRateValRange = measurements.heartRateValMax - measurements.heartRateValMin;
        measurements.heartRateValStd = MATH_STUFF.getStdDev(measurements.heartRateValArray);
    }

    static public StringBuilder arraysToString(int pos, int performingActivity, StorageData measurements) {
        StringBuilder allSensorsData = new StringBuilder();
        long currentTime = System.currentTimeMillis();

        allSensorsData.append(currentTime).append(",");

        allSensorsData.append(measurements.accelXArray[pos]).append(",").append(measurements.accelYArray[pos]).append(",").append(measurements.accelZArray[pos]).append(",");

        allSensorsData.append(measurements.gyroXArray[pos]).append(",").append(measurements.gyroYArray[pos]).append(",").append(measurements.gyroZArray[pos]).append(",");

        allSensorsData.append(measurements.gravityXArray[pos]).append(",").append(measurements.gravityYArray[pos]).append(",").append(measurements.gravityZArray[pos]).append(",");

        allSensorsData.append(measurements.linAccelXArray[pos]).append(",").append(measurements.linAccelYArray[pos]).append(",").append(measurements.linAccelZArray[pos]).append(",");

        allSensorsData.append(measurements.rotVecXArray[pos]).append(",").append(measurements.rotVecYArray[pos]).append(",").append(measurements.rotVecSArray[pos]).append(",");

        allSensorsData.append(measurements.aiPreValArray[pos]).append(",");

        allSensorsData.append(measurements.magFielYArray[pos]).append(",");

        allSensorsData.append(measurements.heartRateValArray[pos]).append(",");

        allSensorsData.append(performingActivity);

        return allSensorsData;
    }

    public static void updateSensorData(SensorEvent event, StorageCurrentData newMeasurement) {
        float[] accelerometerFilter = new float[3];

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

            newMeasurement.accelX = ax;
            newMeasurement.accelY = ay;
            newMeasurement.accelZ = az;
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];

            newMeasurement.gyroX = ax;
            newMeasurement.gyroY = ay;
            newMeasurement.gyroZ = az;
        }

        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];

            newMeasurement.gravityX = ax;
            newMeasurement.gravityY = ay;
            newMeasurement.gravityZ = az;
        }

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];

            newMeasurement.linAccelX = ax;
            newMeasurement.linAccelY = ay;
            newMeasurement.linAccelZ = az;
        }

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float ax = event.values[0];
            float ay = event.values[1];
            float as = event.values[2];

            newMeasurement.rotVecX = ax;
            newMeasurement.rotVecY = ay;
            newMeasurement.rotVecS = as;
        }

        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            float ax = event.values[0];

            newMeasurement.aiPreVal = ax;
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float ay = event.values[1];

            newMeasurement.magFielY = ay;
        }

        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            float ax = event.values[0];

            newMeasurement.heartRateVal = ax;
        }
    }

}
