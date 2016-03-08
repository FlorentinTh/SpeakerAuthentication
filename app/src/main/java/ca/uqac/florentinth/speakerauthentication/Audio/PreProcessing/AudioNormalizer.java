package ca.uqac.florentinth.speakerauthentication.Audio.PreProcessing;

import android.util.Log;

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
public class AudioNormalizer {

    private static final String TAG = AudioNormalizer.class.getSimpleName();

    public double normalizeAudio(double[] audioSample) {
        double maxValue = Double.MIN_VALUE;

        for(int i = 0; i < audioSample.length; i++) {
            double absoluteValue = Math.abs(audioSample[i]);

            if(absoluteValue > maxValue) {
                maxValue = absoluteValue;
            }
        }

        if(maxValue > 1.0d) {
            Log.e(TAG, "[ERROR] Expected values for audio must be between -1.0 and 1.0");
        }

        if(maxValue < 5 * Math.ulp(0.0d)) {
            return 1.0d;
        }

        for(int i = 0; i < audioSample.length; i++) {
            audioSample[i] /= maxValue;
        }

        return 1.0d / maxValue;
    }
}
