package ca.uqac.florentinth.speakerauthentication.Learning;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public class Learning {

    private weka.classifiers.Classifier classifier;
    private double kappa;
    private double fMeasure;
    private String confusionMatrix;

    public double getKappa() {
        return kappa;
    }

    public double getFMeasure() {
        return fMeasure;
    }

    public String getConfusionMatrix() {
        return confusionMatrix;
    }

    public void trainClassifier(Classifier classifier, FileReader dataset, FileOutputStream
            model) throws Exception {
        trainClassifier(classifier, dataset, model, 0);
    }

    public void trainClassifier(Classifier classifier, FileReader trainingDataset,
                                FileOutputStream trainingModel, Integer
                                        crossValidationFoldNumber) throws Exception {
        Instances instances = new Instances(new BufferedReader(trainingDataset));

        switch(classifier) {
            case KNN:
                int K = (int) Math.ceil(Math.sqrt(instances.numInstances()));
                this.classifier = new IBk(K);
                break;
            case NB:
                this.classifier = new NaiveBayes();
        }

        if(instances.classIndex() == -1) {
            instances.setClassIndex(instances.numAttributes() - 1);
        }

        this.classifier.buildClassifier(instances);

        if(crossValidationFoldNumber > 0) {
            Evaluation evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(this.classifier, instances, crossValidationFoldNumber,
                    new Random(1));
            kappa = evaluation.kappa();
            fMeasure = evaluation.weightedFMeasure();
            confusionMatrix = evaluation.toMatrixString("Confusion matrix: ");
        }

        ObjectOutputStream outputStream = new ObjectOutputStream(trainingModel);
        outputStream.writeObject(this.classifier);
        outputStream.flush();
        outputStream.close();
    }

    public Map<String, String> makePrediction(String username, FileInputStream trainingModel,
                                              FileReader
            testingDataset) throws Exception {
        Map<String, String> predictions = new HashMap<>();

        ObjectInputStream inputStream = new ObjectInputStream(trainingModel);
        weka.classifiers.Classifier classifier = (weka.classifiers.Classifier) inputStream
                .readObject();
        inputStream.close();

        Instances instances = new Instances(new BufferedReader(testingDataset));

        if(instances.classIndex() == -1) {
            instances.setClassIndex(instances.numAttributes() - 1);
        }

        int last = instances.numInstances() - 1;

        if(instances.instance(last).stringValue(instances.classIndex()).equals(username)) {
            double label = classifier.classifyInstance(instances.instance(last));
            instances.instance(last).setClassValue(label);
            predictions.put(username, instances.instance(last).stringValue(instances.classIndex()));
        }

        return predictions;
    }
}
