package ca.uqac.florentinth.speakerauthentication.Config;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public class Features {

    private static Features ourInstance = new Features();
    private final int numberFeatures = 20;

    public static Features getInstance() {
        return ourInstance;
    }

    public int getNumberFeatures() {
        return numberFeatures;
    }
}
