package com.project.frugalmachinelearning.structures;

import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.DenseInstance;

/**
 * Created by Mikhail on 20.11.2016.
 */
public abstract class InstanceBuilder {

    public static DenseInstance getInstance(int numOfAttributes, boolean computeComplexFeatures, StorageData measurements, int appState) {
        double[] attributeValues = new double[numOfAttributes];
        int currentAttNumber = 0;

        if (computeComplexFeatures) {
            attributeValues[currentAttNumber++] = measurements.accelXMin;
            attributeValues[currentAttNumber++] = measurements.accelYMin;
            attributeValues[currentAttNumber++] = measurements.accelZMin;
            attributeValues[currentAttNumber++] = measurements.accelXMax;
            attributeValues[currentAttNumber++] = measurements.accelYMax;
            attributeValues[currentAttNumber++] = measurements.accelZMax;
            attributeValues[currentAttNumber++] = measurements.accelXMean;
            attributeValues[currentAttNumber++] = measurements.accelYMean;
            attributeValues[currentAttNumber++] = measurements.accelZMean;
            attributeValues[currentAttNumber++] = measurements.accelXRange;
            attributeValues[currentAttNumber++] = measurements.accelYRange;
            attributeValues[currentAttNumber++] = measurements.accelZRange;
            attributeValues[currentAttNumber++] = measurements.accelXStd;
            attributeValues[currentAttNumber++] = measurements.accelYStd;
            attributeValues[currentAttNumber++] = measurements.accelZStd;

            attributeValues[currentAttNumber++] = measurements.gyroXMin;
            attributeValues[currentAttNumber++] = measurements.gyroYMin;
            attributeValues[currentAttNumber++] = measurements.gyroZMin;
            attributeValues[currentAttNumber++] = measurements.gyroXMax;
            attributeValues[currentAttNumber++] = measurements.gyroYMax;
            attributeValues[currentAttNumber++] = measurements.gyroZMax;
            attributeValues[currentAttNumber++] = measurements.gyroXMean;
            attributeValues[currentAttNumber++] = measurements.gyroYMean;
            attributeValues[currentAttNumber++] = measurements.gyroZMean;
            attributeValues[currentAttNumber++] = measurements.gyroXRange;
            attributeValues[currentAttNumber++] = measurements.gyroYRange;
            attributeValues[currentAttNumber++] = measurements.gyroZRange;
            attributeValues[currentAttNumber++] = measurements.gyroXStd;
            attributeValues[currentAttNumber++] = measurements.gyroYStd;
            attributeValues[currentAttNumber++] = measurements.gyroZStd;

            attributeValues[currentAttNumber++] = measurements.gravityXMin;
            attributeValues[currentAttNumber++] = measurements.gravityYMin;
            attributeValues[currentAttNumber++] = measurements.gravityZMin;
            attributeValues[currentAttNumber++] = measurements.gravityXMax;
            attributeValues[currentAttNumber++] = measurements.gravityYMax;
            attributeValues[currentAttNumber++] = measurements.gravityZMax;
            attributeValues[currentAttNumber++] = measurements.gravityXMean;
            attributeValues[currentAttNumber++] = measurements.gravityYMean;
            attributeValues[currentAttNumber++] = measurements.gravityZMean;
            attributeValues[currentAttNumber++] = measurements.gravityXRange;
            attributeValues[currentAttNumber++] = measurements.gravityYRange;
            attributeValues[currentAttNumber++] = measurements.gravityZRange;
            attributeValues[currentAttNumber++] = measurements.gravityXStd;
            attributeValues[currentAttNumber++] = measurements.gravityYStd;
            attributeValues[currentAttNumber++] = measurements.gravityZStd;

            attributeValues[currentAttNumber++] = measurements.linAccelXMin;
            attributeValues[currentAttNumber++] = measurements.linAccelYMin;
            attributeValues[currentAttNumber++] = measurements.linAccelZMin;
            attributeValues[currentAttNumber++] = measurements.linAccelXMax;
            attributeValues[currentAttNumber++] = measurements.linAccelYMax;
            attributeValues[currentAttNumber++] = measurements.linAccelZMax;
            attributeValues[currentAttNumber++] = measurements.linAccelXMean;
            attributeValues[currentAttNumber++] = measurements.linAccelYMean;
            attributeValues[currentAttNumber++] = measurements.linAccelZMean;
            attributeValues[currentAttNumber++] = measurements.linAccelXRange;
            attributeValues[currentAttNumber++] = measurements.linAccelYRange;
            attributeValues[currentAttNumber++] = measurements.linAccelZRange;
            attributeValues[currentAttNumber++] = measurements.linAccelXStd;
            attributeValues[currentAttNumber++] = measurements.linAccelYStd;
            attributeValues[currentAttNumber++] = measurements.linAccelZStd;

            attributeValues[currentAttNumber++] = measurements.rotVecXMin;
            attributeValues[currentAttNumber++] = measurements.rotVecYMin;
            attributeValues[currentAttNumber++] = measurements.rotVecSMin;
            attributeValues[currentAttNumber++] = measurements.rotVecXMax;
            attributeValues[currentAttNumber++] = measurements.rotVecYMax;
            attributeValues[currentAttNumber++] = measurements.rotVecSMax;
            attributeValues[currentAttNumber++] = measurements.rotVecXMean;
            attributeValues[currentAttNumber++] = measurements.rotVecYMean;
            attributeValues[currentAttNumber++] = measurements.rotVecSMean;
            attributeValues[currentAttNumber++] = measurements.rotVecXRange;
            attributeValues[currentAttNumber++] = measurements.rotVecYRange;
            attributeValues[currentAttNumber++] = measurements.rotVecSRange;
            attributeValues[currentAttNumber++] = measurements.rotVecXStd;
            attributeValues[currentAttNumber++] = measurements.rotVecYStd;
            attributeValues[currentAttNumber++] = measurements.rotVecSStd;

            attributeValues[currentAttNumber++] = measurements.aiPreValMin;
            attributeValues[currentAttNumber++] = measurements.aiPreValMax;
            attributeValues[currentAttNumber++] = measurements.aiPreValMean;
            attributeValues[currentAttNumber++] = measurements.aiPreValRange;
            attributeValues[currentAttNumber++] = measurements.aiPreValStd;

            attributeValues[currentAttNumber++] = measurements.magFielYMin;
            attributeValues[currentAttNumber++] = measurements.magFielYMax;
            attributeValues[currentAttNumber++] = measurements.magFielYMean;
            attributeValues[currentAttNumber++] = measurements.magFielYRange;
            attributeValues[currentAttNumber++] = measurements.magFielYStd;

            attributeValues[currentAttNumber++] = measurements.heartRateValMin;
            attributeValues[currentAttNumber++] = measurements.heartRateValMax;
            attributeValues[currentAttNumber++] = measurements.heartRateValMean;
            attributeValues[currentAttNumber++] = measurements.heartRateValRange;
            attributeValues[currentAttNumber++] = measurements.heartRateValStd;

            List<String> activityValues = getActivityValues();
            if (appState == 1) {
                // any value for a place of activity
                attributeValues[currentAttNumber] = activityValues.indexOf(activityValues.get(0));
            }

        }

        return new DenseInstance(1.0, attributeValues);
    }

    public static List<String> getActivityValues() {
        List<String> values = new ArrayList<String>();
        values.add("0");
        values.add("1");
        values.add("2");
        values.add("3");
        values.add("4");
        values.add("5");

        return values;
    }

    public static ArrayList<Attribute> getNewAttributes() {
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
}
