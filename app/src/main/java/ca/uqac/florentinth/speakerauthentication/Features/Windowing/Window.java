package ca.uqac.florentinth.speakerauthentication.Features.Windowing;

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
public abstract class Window {

    private static final String TAG = Window.class.getSimpleName();

    private int size;
    private double[] factors;

    public Window(int size) {
        this.size = size;
        this.factors = getPrecomputedFactors(size);
    }

    protected abstract double[] getPrecomputedFactors(int size);

    public void applyWindow(double[] window) {
        if(window.length == this.size) {
            for(int i = 0; i < window.length; i++) {
                window[i] *= factors[i];
            }
        } else {
            Log.e(TAG, "[ERROR] Incompatible window size for this WindowFunction " + "instance" +
                    " : " + "expected " + size + ", received " + window.length);
        }
    }
}
