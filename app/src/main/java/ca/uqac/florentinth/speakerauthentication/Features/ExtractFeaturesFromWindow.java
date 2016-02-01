package ca.uqac.florentinth.speakerauthentication.Features;

import android.util.Log;

import ca.uqac.florentinth.speakerauthentication.Config.Audio;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public class ExtractFeaturesFromWindow {

    private static final String TAG = ExtractFeaturesFromWindow.class.getSimpleName();
    private static final int DEFAULT_FEATURES_WINDOW_SIZE = 24;

    protected int windowSize;
    protected float sampleRate;

    public ExtractFeaturesFromWindow(float sampleRate) {
        float minSampleRate = Audio.getInstance().getMinSampleRate();

        if(sampleRate < minSampleRate) {
            Log.e(TAG, "[ERROR] Sample rate should be at least " + minSampleRate + " " +
                    "Received : " + sampleRate + "Hz");
        }

        this.sampleRate = sampleRate;
        this.windowSize = getWindowSize(sampleRate, DEFAULT_FEATURES_WINDOW_SIZE);
    }

    private int getWindowSize(float sampleRate, int windowSize) {
        boolean done = false;
        int bytes = 8;
        float previousMillis = 0.0F;

        while(!done) {
            float millis = 1000 / sampleRate * bytes;

            if(millis < windowSize) {
                previousMillis = millis;
                bytes *= 2;
            } else {
                if(Math.abs(windowSize - millis) > windowSize - previousMillis) {
                    bytes /= 2;
                }

                done = true;
            }
        }

        return bytes;
    }
}
