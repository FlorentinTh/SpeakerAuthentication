package ca.uqac.florentinth.speakerauthentication.Learning.Test;

import android.os.AsyncTask;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Map;

import ca.uqac.florentinth.speakerauthentication.Learning.Learning;
import ca.uqac.florentinth.speakerauthentication.Time;

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

        Time.start = System.currentTimeMillis();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Learning learning = new Learning();

        boolean result = false;

        try {

            Map<String, String> map = learning.makePrediction(username, model, dataset);

            for(Map.Entry<String, String> entry : map.entrySet()) {

                String value = entry.getValue();

                if(value.equals(username)) {
                    result = true;
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
