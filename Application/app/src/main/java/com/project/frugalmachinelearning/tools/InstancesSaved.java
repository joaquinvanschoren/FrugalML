package com.project.frugalmachinelearning.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

/**
 * Created by Mikhail on 03.05.2016.
 */
public class InstancesSaved {

    public static int getLabelFromSavedFirstInstances(InputStream ins, AbstractClassifier classifier) {

        BufferedReader br = new BufferedReader(new InputStreamReader(ins));

        int value = -1;

        try {
            Instances instancesLabeled = new Instances(br);
            br.close();

            instancesLabeled.setClassIndex(instancesLabeled.numAttributes() - 1);

            int s1 = 0;
            value = (int) (classifier.classifyInstance(instancesLabeled.instance(s1)));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

}
