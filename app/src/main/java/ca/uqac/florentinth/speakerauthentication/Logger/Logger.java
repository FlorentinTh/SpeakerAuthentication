package ca.uqac.florentinth.speakerauthentication.Logger;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import ca.uqac.florentinth.speakerauthentication.Config.Folders;

/**
 * Created by FlorentinTh on 1/28/2016.
 */
public abstract class Logger {

    private static final String TAG = Logger.class.getSimpleName();

    public static void write(List<String> values) {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), Folders
                .getInstance().getBaseLogs() + "/log.log");

        BufferedWriter bufferedWriter = null;

        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file, true));

            for(int i = 0; i < values.size(); i++) {
                if(i == values.size() - 1) {
                    bufferedWriter.write(values.get(i));
                } else {
                    bufferedWriter.write(values.get(i) + ";");
                }
            }

            bufferedWriter.write("\n");

        } catch(IOException e) {
            Log.e(TAG, "Impossible to open log file : " + e.getMessage());
        } finally {
            try {
                bufferedWriter.close();
            } catch(IOException e) {
                Log.e(TAG, "Impossible to close log file : " + e.getMessage());
            }
        }
    }
}
