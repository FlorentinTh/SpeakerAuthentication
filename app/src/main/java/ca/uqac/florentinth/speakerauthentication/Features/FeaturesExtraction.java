package ca.uqac.florentinth.speakerauthentication.Features;

import ca.uqac.florentinth.speakerauthentication.Features.Windowing.HammingWindow;
import ca.uqac.florentinth.speakerauthentication.Features.Windowing.Window;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public class FeaturesExtraction extends ExtractFeaturesFromWindow {

    private int poles;
    private Window window;
    private LinearPredictiveCoding LPC;

    public FeaturesExtraction(float sampleRate, int poles) {
        super(sampleRate);
        this.poles = poles;
        this.window = new HammingWindow(windowSize);
        this.LPC = new LinearPredictiveCoding(windowSize, poles);
    }

    public double[] extractVoiceFeatures(double[] voiceSample) {
        double[] voiceFeatures = new double[poles];
        double[] audioWindow = new double[windowSize];
        int counter = 0;

        for(int i = 0; (i + windowSize) <= voiceSample.length; i += (windowSize / 2)) {
            System.arraycopy(voiceSample, i, audioWindow, 0, windowSize);
            window.applyWindow(audioWindow);
            double[] LPCCoefficients = LPC.processLPC(audioWindow)[0];

            for(int j = 0; j < poles; j++) {
                voiceFeatures[j] += LPCCoefficients[j];
            }

            counter++;
        }

        if(counter > 1) {
            for(int i = 0; i < poles; i++) {
                voiceFeatures[i] /= counter;
            }
        }

        return voiceFeatures;
    }
}
