package ca.uqac.florentinth.speakerauthentication.Features.Windowing;

import android.util.Log;

/**
 * Created by FlorentinTh on 11/12/2015.
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
