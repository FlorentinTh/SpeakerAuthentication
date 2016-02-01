package ca.uqac.florentinth.speakerauthentication.Audio.PreProcessing;

import android.util.Log;

/**
 * Created by FlorentinTh on 11/12/2015.
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
