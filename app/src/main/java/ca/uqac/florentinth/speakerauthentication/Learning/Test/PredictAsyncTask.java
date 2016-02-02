package ca.uqac.florentinth.speakerauthentication.Learning.Test;

import android.os.AsyncTask;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Map;

import ca.uqac.florentinth.speakerauthentication.Learning.Learning;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public class PredictAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = PredictAsyncTask.class.getSimpleName();
    public AsyncResponse delegate = null;

    private String username;
    private FileInputStream model;
    private FileReader dataset;

    public PredictAsyncTask(String username, FileInputStream model, FileReader dataset,
                            AsyncResponse delegate) {
        this.username = username;
        this.model = model;
        this.dataset = dataset;
        this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Learning learning = new Learning();
        boolean result = false;

        try {
            for(Map.Entry<String, String> entry : learning.makePrediction(model, dataset)
                    .entrySet()) {

                String key = entry.getKey();
                String value = entry.getValue();
                if(key.equals(username)) {
                    if(key.equals(value)) {
                        result = true;
                    }
                }
            }
        } catch(Exception e) {
            Log.e(TAG, "ERROR while making prediction: " + e.getMessage());
        }

        return result;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        delegate.finishCallback(result);
        super.onPostExecute(result);
    }

    public interface AsyncResponse {
        void finishCallback(boolean predictResult);
    }
}
