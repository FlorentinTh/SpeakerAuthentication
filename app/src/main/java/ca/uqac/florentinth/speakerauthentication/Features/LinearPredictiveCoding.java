package ca.uqac.florentinth.speakerauthentication.Features;

import android.util.Log;

import java.util.Arrays;

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
public class LinearPredictiveCoding {

    private static final String TAG = LinearPredictiveCoding.class.getSimpleName();

    private int windowSize;
    private int poles;
    private double[] output;
    private double[] error;
    private double[] k;
    private double[][] matrix;

    public LinearPredictiveCoding(int windowSize, int poles) {
        this.windowSize = windowSize;
        this.poles = poles;
        this.output = new double[poles];
        this.error = new double[poles];
        this.k = new double[poles];
        this.matrix = new double[poles][poles];
    }

    public double[][] processLPC(double[] window) {
        if(windowSize != window.length) {
            Log.e(TAG, "[ERROR] The windows length provided are not equal to the one " +
                    "provided when " + "the LPC object is created : [" + window.length + "] != ["
                    + windowSize + "]");
        }

        Arrays.fill(k, 0.0d);
        Arrays.fill(output, 0.0d);
        Arrays.fill(error, 0.0d);

        for(double[] aDouble : matrix) {
            Arrays.fill(aDouble, 0.0d);
        }

        CrossCorrelation crossCorrelation = new CrossCorrelation();
        double[] correlation = new double[poles];

        for(int i = 0; i < poles; i++) {
            correlation[i] = crossCorrelation.processCrossCorrelation(window, i);
        }

        error[0] = correlation[0];

        for(int i = 1; i < poles; i++) {
            double tmp = correlation[i];

            for(int j = 1; j < i; j++) {
                tmp -= matrix[i - 1][j] * correlation[i - j];
            }

            k[i] = tmp / error[i - 1];

            for(int j = 0; j < i; j++) {
                matrix[i][j] = matrix[i - 1][j] - k[i] * matrix[i - 1][i - j];
            }

            matrix[i][i] = k[i];
            error[i] = (1 - (k[i] * k[i])) * error[i - 1];
        }

        for(int i = 0; i < poles; i++) {
            if(Double.isNaN(matrix[poles - 1][i])) {
                output[i] = 0.0d;
            } else {
                output[i] = matrix[poles - 1][i];
            }
        }

        return new double[][] {output, error};
    }
}
