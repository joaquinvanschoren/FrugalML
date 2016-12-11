package com.project.frugalmachinelearning.tools;

import android.content.Context;
import android.util.Log;

import com.project.frugalmachinelearning.MainActivity;
import com.project.frugalmachinelearning.activities.ActivityType;
import com.project.frugalmachinelearning.external.BatteryInstruments;
import com.project.frugalmachinelearning.structures.StorageData;

import java.io.PrintWriter;

import weka.classifiers.AbstractClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

/**
 * Created by Mikhail on 11.12.2016.
 */
public abstract class StringInstruments {

    /**
     * Recognize activity name with a classifier from the input data
     *
     * @param data
     *              sensor measurements
     * @param needDiscretizeData
     *              if a classifiers requires discrete data for input
     * @param discretizeUnit
     *              filter that discretizes continuous data
     * @param selectedClassifier
     *              classifier name
     * @return
     *              string with an activity type
     */
    public static String recognizeActivity(Instances data, boolean needDiscretizeData, Discretize discretizeUnit, AbstractClassifier selectedClassifier) {

        StringBuilder activityFullName = new StringBuilder();

        if (needDiscretizeData && discretizeUnit != null) {
            try {
                Instances discretData = Filter.useFilter(data, discretizeUnit);
                activityFullName = activityFullName.append(recognizeActivity(selectedClassifier, discretData));
            } catch (Exception e) {
                Log.i(MainActivity.TAG, e.toString());
            }
        } else if (!needDiscretizeData) {
            activityFullName = activityFullName.append(recognizeActivity(selectedClassifier, data));
        } else {
            activityFullName = activityFullName.append("BEING COMPUTED");
        }

        return activityFullName.toString();
    }

    /**
     * Collect data and save it in a string
     *
     * @param context
     *              application data
     * @param currentTimeMs
     *              time identifier
     * @param selectedClassifier
     *              name of the used classifier
     * @return
     *              string with data
     */
    public static String getStringForActivityRecognitionFile(Context context, long currentTimeMs, AbstractClassifier selectedClassifier) {
        StringBuilder recognitionProcessInfo = new StringBuilder();
        recognitionProcessInfo.append(currentTimeMs).append(",");
        recognitionProcessInfo.append(selectedClassifier.getClass()).append(",");
        recognitionProcessInfo.append(BatteryInstruments.getBatteryLevel(context));
        return recognitionProcessInfo.toString();
    }

    /**
     * Store activity data to a file
     *
     * @param activityUpdateEvent
     *              time of the event
     * @param posInstance
     *              number of instance in the array with data instances
     * @param printWriter
     *              manages writing information about activities to a file
     * @param numberOfPerformingActivity
     *              activity number
     * @param measurements
     *              data instances array
     */
    public static void writeNewActivityDataToFile(long activityUpdateEvent, int posInstance,
                                            PrintWriter printWriter, int numberOfPerformingActivity, StorageData measurements) {

        // pause data collection after activity change for a short period and wait for computed values
        long currentTimeMs = System.currentTimeMillis();
        if (currentTimeMs - activityUpdateEvent >= 5000) {
            String newInfo = StorageDataActions.arraysToString(posInstance, numberOfPerformingActivity, measurements).toString();
            printWriter.println(newInfo);
        }
    }

    /**
     * Identify an activity from an instance
     *
     * @param classifier
     *              name of the used classifier
     * @param data
     *              structure with the instance used for the activity recognition
     * @return
     *              activity type
     */
    private static String recognizeActivity(AbstractClassifier classifier, Instances data) {
        double value = Double.NaN;
        try {
            Instance instance = data.get(0);
            value = classifier.classifyInstance(instance);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String activityFullName = ActivityType.values()[(int) value].toString();

        return activityFullName;
    }

    /**
     * Make a header for a data file
     *
     * @return
     *              string with a header for a file with data from sensors
     */
    public static String createTitle() {
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
}

