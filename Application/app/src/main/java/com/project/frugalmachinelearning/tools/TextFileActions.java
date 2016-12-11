package com.project.frugalmachinelearning.tools;

/**
 * Created by Mikhail on 20.11.2016.
 */
public class TextFileActions {

    public static String createTitle() {
        StringBuilder title = new StringBuilder();
        title.append("Timestamp ,");

        title.append("AccelX, ");
        title.append("AccelY, ");
        title.append("AccelZ, ");

        title.append("GyroX, ");
        title.append("GyroY, ");
        title.append("GyroZ, ");

        title.append("GravityX, ");
        title.append("GravityY, ");
        title.append("GravityZ, ");

        title.append("LinAccelX, ");
        title.append("LinAccelY, ");
        title.append("LinAccelZ, ");

        title.append("RotVecX, ");
        title.append("RotVecY, ");
        title.append("RotVecS, ");

        title.append("AiPreVal, ");

        title.append("MagFielY, ");

        title.append("HeartRateVal, ");

        title.append("Activity");

        String dataTitle = title.toString();

        return dataTitle;
    }

}
