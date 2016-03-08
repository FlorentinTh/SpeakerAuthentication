package ca.uqac.florentinth.speakerauthentication.Utils;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ca.uqac.florentinth.speakerauthentication.Config.Features;
import ca.uqac.florentinth.speakerauthentication.Models.Story;
import ca.uqac.florentinth.speakerauthentication.Models.VoiceSample;

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
public abstract class JSONUtils {

    public static String parseJSONFile(Context ctx, String file) {
        String json = null;

        try {
            InputStream inputStream = ctx.getAssets().open(file, AssetManager.ACCESS_STREAMING);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch(IOException e) {
            e.printStackTrace();
        }

        return json;
    }

    public static List JSONToTextsObject(String json) {
        ArrayList list = new ArrayList();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("texts");

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                int no = obj.getInt("no");
                String text = obj.getString("text");
                Story read = new Story(no, text);

                list.add(read);
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List JSONToVoiceSampleObject(String json) {
        ArrayList list = new ArrayList();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                String className = obj.getString("class");
                double[] voiceFeatures = new double[Features.getInstance().getNumberFeatures()];
                JSONArray features = obj.getJSONArray("voice_features");

                for(int j = 0; j < features.length(); j++) {
                    voiceFeatures[j] = features.getDouble(j);
                }

                VoiceSample sample = new VoiceSample(className, voiceFeatures);

                list.add(sample);
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return list;
    }
}
