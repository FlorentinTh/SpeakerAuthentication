package ca.uqac.florentinth.speakerauthentication.Dataset.Builder;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import ca.uqac.florentinth.speakerauthentication.Config.Audio;
import ca.uqac.florentinth.speakerauthentication.Config.Folders;
import ca.uqac.florentinth.speakerauthentication.Core.SpeakerRecognition;
import ca.uqac.florentinth.speakerauthentication.Utils.FileUtils;

/**
 * Created by FlorentinTh on 1/27/2016.
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
    }


    @Override
    protected Void doInBackground(String... params) {
        File[] inputFiles = inputFolder.listFiles();

        SpeakerRecognition<String> featureExtraction = new SpeakerRecognition<>(Audio.getInstance
                ().getSampleRate());

        try {
            for(int i = 0; i < inputFiles.length; i++) {

                String WAVFile = inputFiles[i].getName();
                String sampleName = FileUtils.removeFileExtension(inputFiles[i].getName());

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
