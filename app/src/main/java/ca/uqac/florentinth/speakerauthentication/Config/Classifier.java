package ca.uqac.florentinth.speakerauthentication.Config;

import ca.uqac.florentinth.speakerauthentication.Learning.Classifiers;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public class Classifier {

    private static Classifier ourInstance = new Classifier();

    private final Classifiers classifier = Classifiers.KNN;

    public static Classifier getInstance() {
        return ourInstance;
    }

    public Classifiers getClassifier() { return classifier; }
}
