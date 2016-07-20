package com.project.frugalmachinelearning.classifiers;

import android.util.Log;

import java.util.ArrayList;
import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by Mikhail on 02.05.2016.
 */
public class ActivityWindow {

    public static String getActivityName(AbstractClassifier classifier, Instances data) {
        int[] activityInWindow = new int[ActivityType.values().length];
        for (Instance instance : data) {
            try {
                double value = classifier.classifyInstance(instance);
                String aType = data.classAttribute().value((int) value);
                activityInWindow[Integer.parseInt(aType)]++;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        int posOfMax = 0;
        int valueOfMax = 0;
        for (int i = 0; i < activityInWindow.length; i++) {
            if (valueOfMax < activityInWindow[i]) {
                posOfMax = i;
                valueOfMax = activityInWindow[i];
            }
        }
        String activityFullName = ActivityType.values()[posOfMax].toString() + " " + Math.round(valueOfMax * 1.0 / data.numInstances() * 100);

        return activityFullName;
    }

    public static Instances constructInstances(ArrayList<Attribute> attributes, DenseInstance[] instances) {

        Instances data = new Instances("Sensors measurements", attributes, 0);
        data.setClassIndex(data.numAttributes() - 1);

        int posNextInstance = 0;
        while(posNextInstance < instances.length && instances[posNextInstance] != null ) {
            data.add(instances[posNextInstance]);
            posNextInstance++;
        }

        return data;
    }

}
