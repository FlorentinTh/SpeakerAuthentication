package ca.uqac.florentinth.speakerauthentication.Learning.Train;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.FileReader;

import ca.uqac.florentinth.speakerauthentication.Learning.Classifier;
import ca.uqac.florentinth.speakerauthentication.Learning.Learning;
import ca.uqac.florentinth.speakerauthentication.R;
import ca.uqac.florentinth.speakerauthentication.Utils.AndroidUtils;

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
public class TrainClassifierAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final Classifier classifier = Classifier.KNN;

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
