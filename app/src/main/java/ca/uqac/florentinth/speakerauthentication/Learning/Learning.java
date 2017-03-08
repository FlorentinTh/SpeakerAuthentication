package ca.uqac.florentinth.speakerauthentication.Learning;

import java.io.BufferedReader;
import java.io.File;
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
import weka.core.converters.CSVLoader;

/**
 * Copyright 2016 Florentin Thullier.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    public void trainClassifier(Classifier classifier, File dataset, FileOutputStream
            model) throws Exception {
        trainClassifier(classifier, dataset, model, 0);
    }

    public void trainClassifier(Classifier classifier, File trainingDataset,
                                FileOutputStream trainingModel, Integer
                                        crossValidationFoldNumber) throws Exception {

        CSVLoader csvLoader = new CSVLoader();
        csvLoader.setSource(trainingDataset);

        Instances instances = csvLoader.getDataSet();

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
