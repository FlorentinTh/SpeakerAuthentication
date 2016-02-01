package ca.uqac.florentinth.speakerauthentication.Core;

import android.util.Log;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public class VoicePrint {

    private static final String TAG = VoicePrint.class.getSimpleName();

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private double[] voiceFeatures;
    private int meanCount;

    public VoicePrint(double[] voiceFeatures) {
        this.voiceFeatures = voiceFeatures;
        this.meanCount = 1;
    }

    public VoicePrint(VoicePrint voicePrint) {
        this(Arrays.copyOf(voicePrint.voiceFeatures, voicePrint.voiceFeatures.length));
    }

    public void mergeVoicePrintFeatures(double[] voiceFeatures) {
        if(this.voiceFeatures.length != voiceFeatures.length) {
            Log.e(TAG, "[ERROR] VoicePrints features lengths are different : [" + voiceFeatures
                    .length + "] expected [" + this.voiceFeatures.length + "]");
        }

        writeLock.lock();

        try {
            for(int i = 0; i < this.voiceFeatures.length; i++) {
                this.voiceFeatures[i] = (this.voiceFeatures[i] * meanCount + voiceFeatures[i]) /
                        (meanCount + 1);
            }

            meanCount++;
        } finally {
            writeLock.unlock();
        }
    }
}
