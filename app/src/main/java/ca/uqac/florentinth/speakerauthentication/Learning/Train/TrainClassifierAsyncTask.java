package ca.uqac.florentinth.speakerauthentication.Learning.Train;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.FileReader;

import ca.uqac.florentinth.speakerauthentication.Learning.Classifiers;
import ca.uqac.florentinth.speakerauthentication.Learning.Learning;
import ca.uqac.florentinth.speakerauthentication.R;
import ca.uqac.florentinth.speakerauthentication.Utils.AndroidUtils;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public class TrainClassifierAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final Classifiers classifier = Classifiers.KNN;

    private Context context;
    private ProgressDialog loadingDialog;

    private FileReader dataset;
    private FileOutputStream model;
    private int foldNumber;

    public TrainClassifierAsyncTask(Context context, FileReader dataset, FileOutputStream model) {
        this.context = context;
        this.dataset = dataset;
        this.model = model;
    }

    public TrainClassifierAsyncTask(Context context, FileReader dataset, FileOutputStream model,
                                    int foldNumber) {
        this.context = context;
        this.dataset = dataset;
        this.model = model;
        this.foldNumber = foldNumber;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Learning learning = new Learning();

        try {
            learning.trainClassifier(classifier, dataset, model);
        } catch(Exception e) {
            Log.e("Training", "ERROR while training dataset: " + e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        loadingDialog = new ProgressDialog(context);
        loadingDialog.setTitle(R.string.loading_dialog_title);
        loadingDialog.setMessage(context.getString(R.string.loading_dialog_message));
        loadingDialog.setCancelable(false);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        loadingDialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        loadingDialog.dismiss();
        AndroidUtils.showAlertDialogFinish(context, context.getString(R.string
                .alert_title_thanks), context.getString(R.string.finish_learning));
    }
}
