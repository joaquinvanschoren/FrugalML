package com.project.frugalmachinelearning.classifiers;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

// import weka.classifiers.AbstractClassifier;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.bayes.AveragedNDependenceEstimators.A1DE;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Dagging;
import weka.classifiers.trees.RandomForest;

/**
 * Created by Mikhail on 02.05.2016.
 */
public class FactoryClassifiers {

    private static final String TAG = "Creating a model";

    public String getModelFile(String modelName) {
        switch (modelName) {
            case "A1DE":
                return "a1denumeric";
            case "Dagging":
                return "daggingnumeric";
            case "AdaBoostM1":
                return "adaboostm1numeric";
            default:
                return null;
        }
    }

    public AbstractClassifier getModel(String modelName, InputStream ins) {
        try {
            AbstractClassifier model;
            switch (modelName) {
                case "A1DE":
                    model = (A1DE) new ObjectInputStream(ins).readObject();
                    break;
                case "Dagging":
                    model = (Dagging) new ObjectInputStream(ins).readObject();
                    break;
                case "AdaBoostM1":
                    model = (AdaBoostM1) new ObjectInputStream(ins).readObject();
                    break;
                default:
                    model = null;
            }

            return model;
        }
        catch (IOException e) {
            Log.e(TAG, "File reading error" );
        }
        catch (ClassNotFoundException e) {
            Log.e(TAG, "Class not found");
        }

        return null;
    }
}
