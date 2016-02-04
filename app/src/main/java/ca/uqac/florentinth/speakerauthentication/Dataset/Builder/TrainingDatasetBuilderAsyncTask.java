package ca.uqac.florentinth.speakerauthentication.Dataset.Builder;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;

import ca.uqac.florentinth.speakerauthentication.Config.Audio;
import ca.uqac.florentinth.speakerauthentication.Config.Folders;
import ca.uqac.florentinth.speakerauthentication.Core.SpeakerRecognition;
import ca.uqac.florentinth.speakerauthentication.Learning.Train.TrainClassifierAsyncTask;
import ca.uqac.florentinth.speakerauthentication.R;
import ca.uqac.florentinth.speakerauthentication.Utils.FileUtils;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public class TrainingDatasetBuilderAsyncTask extends AsyncTask<String, String, Void> {

    private static final String TAG = TrainingDatasetBuilderAsyncTask.class.getSimpleName();

    private Context context;
    private File inputFolder, outputFolder;
    private ProgressDialog loadingDialog;
    private String dB;

    public TrainingDatasetBuilderAsyncTask(Context context, File inputFolder, File outputFolder) {
        this.context = context;
        this.inputFolder = inputFolder;
        this.outputFolder = outputFolder;
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

                if(FileUtils.removeChunkNumber(sampleName).equals(params[0]) && !inputFiles[i]
                        .isHidden() && inputFiles[i].isFile()) {

                    publishProgress(context.getString(R.string.loading_dialog_init_message) +
                            WAVFile);

                    featureExtraction.createVoicePrintFromFile(sampleName, new File(inputFolder +
                            "/" + WAVFile), true);
                }
            }

            publishProgress(context.getString(R.string.loading_dialog_conversion));

            FileUtils.CSVToARFF(new File(outputFolder + "/" + Folders.getInstance()
                    .getDatasetFileNameCSV()), new File(outputFolder + "/" + Folders.getInstance
                    ().getDatasetFileNameARFF()));

            publishProgress(context.getString(R.string.loading_dialog_files));

            FileUtils.moveTrash(inputFolder, new File(Environment.getExternalStorageDirectory()
                    .getPath(), Folders.getInstance().getTrashAudioChunk()));

        } catch(Exception e) {
            Log.e(TAG, "[ERROR] error occurs while trying to build training dataset: " + e
                    .getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        loadingDialog = new ProgressDialog(context);
        loadingDialog.setTitle(R.string.loading_dialog_title);
        loadingDialog.setMessage(context.getString(R.string.loading_dialog_init_message));
        loadingDialog.setCancelable(false);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        loadingDialog.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        loadingDialog.setMessage(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        loadingDialog.dismiss();
        try {
            new TrainClassifierAsyncTask(context, new FileReader(new File(Environment
                    .getExternalStorageDirectory().getPath(), Folders.getInstance()
                    .getTrainingGeneratedDataset() + "/" +
                    Folders.getInstance().getDatasetFileNameARFF())), new FileOutputStream(new
                    File(Environment.getExternalStorageDirectory().getPath(), Folders.getInstance
                    ().getTrainedModel() + "/" +
                    Folders.getInstance().getModelName()))).execute();
        } catch(FileNotFoundException e) {
            Log.e(TAG, "[ERROR] necessary files to train the classifier are not found: " + e
                    .getMessage());
        }
    }
}
