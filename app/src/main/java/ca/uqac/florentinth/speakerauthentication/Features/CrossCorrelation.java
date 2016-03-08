package ca.uqac.florentinth.speakerauthentication.Features;

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
public class CrossCorrelation {

    private static final String TAG = CrossCorrelation.class.getSimpleName();

    public double processCrossCorrelation(double[] buffer, int lag) {
        if(lag > -1 && lag < buffer.length) {
            double result = 0.0d;

            for(int i = lag; i < buffer.length; i++) {
                result += buffer[i] * buffer[i - lag];
            }

            return result;
        } else {
            Log.e(TAG, "[ERROR] Lag parameter must be between -1 and buffer " + "size. " +
                    "Received [" + lag + "] while buffer size is [" + buffer.length + "]");
            return 0;
        }
    }
}
