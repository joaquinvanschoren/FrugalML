package com.project.frugalmachinelearning.external;

import java.util.Arrays;

/**
 * Created by Mikhail on 07.06.2016.
 */
public class MathStuff {

    public MathStuff()
    {

    }

    public double getMean(double[] data)
    {
        int size = data.length;
        double sum = 0.0;
        for(double a : data)
            sum += a;
        return sum/size;
    }

    public double getVariance(double[] data)
    {
        int size = data.length;
        double mean = getMean(data);
        double temp = 0;
        for(double a :data)
            temp += (mean-a)*(mean-a);
        return temp/size;
    }

    public double getStdDev(double[] data)
    {
        return Math.sqrt(getVariance(data));
    }

}
