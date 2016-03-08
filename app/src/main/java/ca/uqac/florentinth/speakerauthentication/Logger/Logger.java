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
 * Copyright 2016 Florentin Thullier.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
