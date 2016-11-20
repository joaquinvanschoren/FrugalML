package com.project.frugalmachinelearning.tools;

import android.content.res.Resources;
import android.util.Log;

import com.project.frugalmachinelearning.MainActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

/**
 * Created by Mikhail on 20.11.2016.
 */
public class ReadDiscretizedValues {

    public static Discretize initFilter(MainActivity activity) {

        Resources resources = activity.getResources();

        Discretize discretizeItems = new Discretize();

        String[] options = new String[6];
        options[0] = "-B";
        options[1] = "3";
        options[2] = "-M";
        options[3] = "-1.0";
        options[4] = "-R";
        options[5] = "first-last";

        try {

            InputStream ins = resources.openRawResource(resources.getIdentifier("margins", "raw", activity.getPackageName()));

            BufferedReader br = new BufferedReader(new InputStreamReader(ins, "UTF-8"));

            Instances data = new Instances(br);
            data.setClassIndex(data.numAttributes() - 1);

            discretizeItems.setOptions(options);
            discretizeItems.setInputFormat(data);

            br.close();

            ins = resources.openRawResource(resources.getIdentifier("features", "raw", activity.getPackageName()));

            br = new BufferedReader(new InputStreamReader(ins, "UTF-8"));

            Instances dataInFile = new Instances(br);
            dataInFile.setClassIndex(dataInFile.numAttributes() - 1);

            Instances tData  = new Instances(dataInFile, 0, 32);

            // a new instance of filter should be used once before real use
            tData = Filter.useFilter(tData, discretizeItems);

            br.close();

        } catch (Exception e) {
            Log.i(MainActivity.TAG, e.toString());
        }

        return discretizeItems;
    }
}
