package ca.uqac.florentinth.speakerauthentication.Features;

import android.util.Log;

/**
 * Created by FlorentinTh on 11/12/2015.
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
