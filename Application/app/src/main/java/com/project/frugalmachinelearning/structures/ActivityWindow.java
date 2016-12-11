package com.project.frugalmachinelearning.structures;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

/**
 * Created by Mikhail on 02.05.2016.
 */
public class ActivityWindow {

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
