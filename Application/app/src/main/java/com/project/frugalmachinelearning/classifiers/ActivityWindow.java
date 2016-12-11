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
