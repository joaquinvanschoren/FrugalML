package com.project.frugalmachinelearning.tools;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Mikhail on 02.05.2016.
 */
public abstract class FileOperations {

    private static final String TAG = "File operations";

    public static boolean deleteFile(String fileName) {
        File fileToDelete = new File(fileName);

        if (fileToDelete.exists() && fileToDelete.delete()) {
                Log.i(TAG, "File " + fileName + " was deleted from a file system");

                return true;
        } else
                return false;

    }

    public static String getSensorStorageDir(String folderName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(folderName).toString());
        if (!file.mkdirs()) {
            Log.i(TAG, "Directory not created");
        }
        return file.toString();
    }

}
