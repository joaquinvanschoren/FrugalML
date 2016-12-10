package com.project.frugalmachinelearning.tools;

/**
 * Created by Mikhail on 20.11.2016.
 */
public class StorageData {

    public static final int UPDATES_PER_SECOND = 16;

    public static final int AMOUNT_OF_ATTRIBUTES = 18;

    public final int windowTime;

    public double[] accelXArray;
    public double[] accelYArray;
    public double[] accelZArray;
    public double accelXMin;
    public double accelYMin;
    public double accelZMin;
    public double accelXMax;
    public double accelYMax;
    public double accelZMax;
    public double accelXMean;
    public double accelYMean;
    public double accelZMean;
    public double accelXRange;
    public double accelYRange;
    public double accelZRange;
    public double accelXStd;
    public double accelYStd;
    public double accelZStd;

    public double[] gyroXArray;
    public double[] gyroYArray;
    public double[] gyroZArray;
    public double gyroXMin;
    public double gyroYMin;
    public double gyroZMin;
    public double gyroXMax;
    public double gyroYMax;
    public double gyroZMax;
    public double gyroXMean;
    public double gyroYMean;
    public double gyroZMean;
    public double gyroXRange;
    public double gyroYRange;
    public double gyroZRange;
    public double gyroXStd;
    public double gyroYStd;
    public double gyroZStd;

    public double[] gravityXArray;
    public double[] gravityYArray;
    public double[] gravityZArray;
    public double gravityXMin;
    public double gravityYMin;
    public double gravityZMin;
    public double gravityXMax;
    public double gravityYMax;
    public double gravityZMax;
    public double gravityXMean;
    public double gravityYMean;
    public double gravityZMean;
    public double gravityXRange;
    public double gravityYRange;
    public double gravityZRange;
    public double gravityXStd;
    public double gravityYStd;
    public double gravityZStd;

    public double[] linAccelXArray;
    public double[] linAccelYArray;
    public double[] linAccelZArray;
    public double linAccelXMin;
    public double linAccelYMin;
    public double linAccelZMin;
    public double linAccelXMax;
    public double linAccelYMax;
    public double linAccelZMax;
    public double linAccelXMean;
    public double linAccelYMean;
    public double linAccelZMean;
    public double linAccelXRange;
    public double linAccelYRange;
    public double linAccelZRange;
    public double linAccelXStd;
    public double linAccelYStd;
    public double linAccelZStd;

    public double[] rotVecXArray;
    public double[] rotVecYArray;
    public double[] rotVecSArray;
    public double rotVecXMin;
    public double rotVecYMin;
    public double rotVecSMin;
    public double rotVecXMax;
    public double rotVecYMax;
    public double rotVecSMax;
    public double rotVecXMean;
    public double rotVecYMean;
    public double rotVecSMean;
    public double rotVecXRange;
    public double rotVecYRange;
    public double rotVecSRange;
    public double rotVecXStd;
    public double rotVecYStd;
    public double rotVecSStd;

    public double[] aiPreValArray;
    public double aiPreValMin;
    public double aiPreValMax;
    public double aiPreValMean;
    public double aiPreValRange ;
    public double aiPreValStd;

    public double[] magFielYArray;
    public double magFielYMin;
    public double magFielYMax;
    public double magFielYMean;
    public double magFielYRange;
    public double magFielYStd;

    public double[] heartRateValArray;
    public double heartRateValMin;
    public double heartRateValMax;
    public double heartRateValMean;
    public double heartRateValRange;
    public double heartRateValStd;

    public StorageData(int windowTime) {
        this.windowTime = windowTime;

        final int frameSize;
        frameSize = UPDATES_PER_SECOND * windowTime;

        accelXArray = new double[frameSize];
        accelYArray = new double[frameSize];
        accelZArray = new double[frameSize];

        gyroXArray = new double[frameSize];
        gyroYArray = new double[frameSize];
        gyroZArray = new double[frameSize];

        gravityXArray = new double[frameSize];
        gravityYArray = new double[frameSize];
        gravityZArray = new double[frameSize];

        linAccelXArray = new double[frameSize];
        linAccelYArray = new double[frameSize];
        linAccelZArray = new double[frameSize];

        rotVecXArray = new double[frameSize];
        rotVecYArray = new double[frameSize];
        rotVecSArray = new double[frameSize];

        aiPreValArray = new double[frameSize];

        magFielYArray = new double[frameSize];

        heartRateValArray = new double[frameSize];
    }

}
