package ca.uqac.florentinth.speakerauthentication.Dataset.Builders;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import ca.uqac.florentinth.speakerauthentication.Config.Audio;
import ca.uqac.florentinth.speakerauthentication.Config.Folders;
import ca.uqac.florentinth.speakerauthentication.Core.SpeakerRecognition;
import ca.uqac.florentinth.speakerauthentication.Time;
import ca.uqac.florentinth.speakerauthentication.Utils.FileUtils;

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
public class TestingDatasetBuilderAsyncTask extends AsyncTask<String, String, Void> {

    private static final String TAG = TestingDatasetBuilderAsyncTask.class.getSimpleName();
    public AsyncResponse delegate = null;
    private File inputFolder, outputFolder;

    public TestingDatasetBuilderAsyncTask(File inputFolder, File outputFolder, AsyncResponse
            delegate) {
        this.inputFolder = inputFolder;
        this.outputFolder = outputFolder;
        this.delegate = delegate;

        Time.start = System.currentTimeMillis();
    }


    @Override
    protected Void doInBackground(String... params) {
        File[] inputFiles = inputFolder.listFiles();

        SpeakerRecognition<String> featureExtraction = new SpeakerRecognition<>(Audio.getInstance
                ().getSampleRate());

        try {
            for(int i = 0; i < inputFiles.length; i++) {

                String WAVFile = inputFiles[i].getName();
                String sampleName = FileUtils.removeFileExtension(FileUtils.removeHeadsetState
                        (inputFiles[i].getName()));

                if(sampleName.equals(params[0]) && !inputFiles[i].isHidden() && inputFiles[i]
                        .isFile()) {

                    featureExtraction.createVoicePrintFromFile(sampleName, new File(inputFolder +
                            "/" + WAVFile), false);
                }

                FileUtils.moveTrash(inputFolder, new File(Environment.getExternalStorageDirectory
                        ().getPath(), Folders.getInstance().getTrashTesting()));
            }

            FileUtils.CSVToARFF(new File(outputFolder + "/" + Folders.getInstance()
                    .getDatasetFileNameCSV()), new File(outputFolder + "/" + Folders.getInstance
                    ().getDatasetFileNameARFF()));

        } catch(Exception e) {
            Log.e(TAG, "[ERROR] error occurs while trying to build testing dataset: " + e
                    .getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        delegate.finishCallback();
    }

    public interface AsyncResponse {
        void finishCallback();
    }
}
