package ca.uqac.florentinth.speakerauthentication.Models;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public class VoiceSample {

    private String className;
    private double[] voiceFeatures;

    public VoiceSample(String className, double[] voiceFeatures) {
        this.className = className;
        this.voiceFeatures = voiceFeatures;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public double[] getVoiceFeatures() {
        return voiceFeatures;
    }

    public void setVoiceFeatures(double[] voiceFeatures) {
        this.voiceFeatures = voiceFeatures;
    }
}
