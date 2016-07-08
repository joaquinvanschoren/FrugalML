package com.project.frugalmachinelearning.classifiers;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.bayes.AveragedNDependenceEstimators.A1DE;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Dagging;
import weka.classifiers.misc.HyperPipes;
import weka.classifiers.meta.RandomSubSpace;
import weka.classifiers.trees.RandomForest;

/**
 * Created by Mikhail on 02.05.2016.
 */
public class FactoryClassifiers {

    private static final String TAG = "Creating a model";

    public String getModelFile(String modelName) {
        if(modelName.equals("HyperPipes")) {
            return "hpnumeric";
        } else if (modelName.equals("NaiveBayes")) {
            return "nbnumeric";
        } else if (modelName.equals("RandomForest")) {
            return "rfnumeric";
        } else if (modelName.equals("A1DE")) {
            return "a1denumeric";
        } else if (modelName.equals("Dagging")) {
            return "daggingnumeric";
        } else if (modelName.equals("AdaBoostM1")) {
            return "adaboostm1numeric";
        }

        else
            return null;
    }

    public AbstractClassifier getModel(String modelName, InputStream ins) {
        try {
            AbstractClassifier model = null;
            if (modelName.equals("HyperPipes")) {
                model = (HyperPipes) (new ObjectInputStream(ins)).readObject();
            } else if (modelName.equals("NaiveBayes")) {
                model = (NaiveBayes) new ObjectInputStream(ins).readObject();
            } else if (modelName.equals("RandomForest")) {
                model = (RandomForest) new ObjectInputStream(ins).readObject();
            } else if (modelName.equals("A1DE")) {
                model = (A1DE) new ObjectInputStream(ins).readObject();
            } else if (modelName.equals("Dagging")) {
                model = (Dagging) new ObjectInputStream(ins).readObject();
            } else if (modelName.equals("AdaBoostM1")) {
                model = (AdaBoostM1) new ObjectInputStream(ins).readObject();
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
