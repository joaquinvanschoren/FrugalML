package com.project.frugalmachinelearning.classifiers;

/**
 * Created by Mikhail on 21.11.2016.
 */
public enum Classifiers {

    ADA_BOOST_M1("AdaBoostM1"),

    DAGGING("Dagging"),

    A1DE("A1DE");

    private String algName;

    private Classifiers(String algName){
        this.algName = algName;
    }

    public String getAlgName() {
        return algName;
    }
}
