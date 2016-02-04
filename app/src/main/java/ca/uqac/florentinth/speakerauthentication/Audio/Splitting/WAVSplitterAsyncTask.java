package ca.uqac.florentinth.speakerauthentication.Audio.Splitting;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import ca.uqac.florentinth.speakerauthentication.Config.Folders;
import ca.uqac.florentinth.speakerauthentication.Dataset.Builder.TrainingDatasetBuilderAsyncTask;
import ca.uqac.florentinth.speakerauthentication.Models.WAVFile;
import ca.uqac.florentinth.speakerauthentication.R;
import ca.uqac.florentinth.speakerauthentication.Utils.FileUtils;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public class WAVSplitterAsyncTask extends AsyncTask<String, Integer, String> {

    private static final String TAG = WAVSplitterAsyncTask.class.getSimpleName();

    private Context context;
    private File inputFolder, outputFolder;
    private ProgressDialog loadingDialog;
    private int recordingTime, chunkLength, chunkNumber;

    public WAVSplitterAsyncTask(Context context, File inputFolder, File outputFolder, int
            recordingTime, int chunkLength) {
        this.context = context;
        this.inputFolder = inputFolder;
        this.outputFolder = outputFolder;
        this.recordingTime = recordingTime;
        this.chunkLength = chunkLength;
        this.chunkNumber = (int) Math.ceil(this.recordingTime / this.chunkLength);
    }

    @Override
    protected String doInBackground(String... params) {

        File[] inputFiles = inputFolder.listFiles();

        for(int i = 0; i < inputFiles.length; i++) {

            String WAVFileName = FileUtils.removeFileExtension(inputFiles[i].getName());

            if(WAVFileName.equals(params[0]) && !inputFiles[i].isHidden() && inputFiles[i].isFile
                    ()) {
                try {
                    WAVFile wavFile = WAVFile.openWAVFile(inputFiles[i]);

                    int channelNumber = wavFile.getChannelNumber();
                    int maxFrameNumberPerFile = (int) wavFile.getSampleRate() * chunkLength / 1000;
                    double[] buffer = new double[maxFrameNumberPerFile * channelNumber];
                    int chunkCreated = 0;
                    int framesRead;

                    do {
                        if(chunkCreated < chunkNumber) {
                            framesRead = wavFile.readFrames(buffer, maxFrameNumberPerFile);
                            WAVFile outputWAVFile = WAVFile.createWAVFile(new File(outputFolder +
                                    "/" + WAVFileName + "-" + chunkCreated +
                                    ".wav"), wavFile.getChannelNumber(), framesRead, wavFile
                                    .getValidBits(), wavFile.getSampleRate());


                            outputWAVFile.writeFrames(buffer, framesRead);
                            outputWAVFile.close();

                            chunkCreated++;
                            publishProgress(chunkCreated);
                        } else {
                            break;
                        }
                    } while((framesRead != 0));

                    wavFile.close();

                    FileUtils.moveTrash(inputFolder, new File(Environment
                            .getExternalStorageDirectory().getPath(), Folders.getInstance()
                            .getTrashAudioRaw()));

                } catch(Exception e) {
                    Log.e(TAG, "[ERROR] error occurs while trying to split WAV files: " + e
                            .getMessage());
                }
            }
        }

        return params[0];
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loadingDialog = new ProgressDialog(context);
        loadingDialog.setTitle(context.getString(R.string.loading_dialog_title));
        loadingDialog.setMessage(context.getString(R.string.loading_dialog_message));
        loadingDialog.setIndeterminate(false);
        loadingDialog.setCancelable(false);
        loadingDialog.setMax(chunkNumber);
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        loadingDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        loadingDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        loadingDialog.dismiss();
        new TrainingDatasetBuilderAsyncTask(context, new File(Environment
                .getExternalStorageDirectory().getPath(), Folders.getInstance().getAudioChunks())
                , new File(Environment.getExternalStorageDirectory().getPath(), Folders
                .getInstance().getTrainingGeneratedDataset())).execute(string);
    }
}
