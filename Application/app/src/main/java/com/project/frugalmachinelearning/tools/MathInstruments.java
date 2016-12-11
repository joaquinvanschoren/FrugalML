package com.project.frugalmachinelearning.tools;

/**
 * Created by Mikhail on 07.06.2016.
 */
public class MathInstruments {

    public MathInstruments()
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

    public double getStdDev(double[] data) {
        return Math.sqrt(getVariance(data));
    }

    public double getMin(double[] data) {
        double minValue = 1000000.0;
        for (int i = 0; i < data.length; i++) {
            double a = data[i];
            if (a < minValue) {
                minValue = a;
            }
        }
        return minValue;
    }

    public double getMax(double[] data) {
        double maxValue = -1000000.0;
        for (int i = 0; i < data.length; i++) {
            double a = data[i];
            if (a > maxValue) {
                maxValue = a;
            }
        }
        return maxValue;
    }
}
