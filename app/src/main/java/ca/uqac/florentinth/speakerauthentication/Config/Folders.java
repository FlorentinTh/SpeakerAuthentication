package ca.uqac.florentinth.speakerauthentication.Config;

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
public class Folders {

    private static Folders ourInstance = new Folders();
    private final String baseDir = "SpeakerAuthentication";
    private final String baseFilesLocation = baseDir + "/files";
    private final String baseLogs = baseDir + "/logs";
    private final String baseAudioFiles = baseFilesLocation + "/audio";
    private final String audioChunks = baseAudioFiles + "/chunks";
    private final String rawAudioFiles = baseAudioFiles + "/raw";
    private final String baseDataset = baseFilesLocation + "/dataset";
    private final String generatedDatasets = baseDataset + "/generated";
    private final String testingGeneratedDataset = generatedDatasets + "/testing";
    private final String trainingGeneratedDataset = generatedDatasets + "/training";
    private final String trainedModel = baseDataset + "/model";
    private final String tmpFolder = baseFilesLocation + "/tmp";
    private final String trashFolder = baseFilesLocation + "/trash";
    private final String trashTraining = trashFolder + "/training";
    private final String trashTesting = trashFolder + "/testing";
    private final String trashAudioRaw = trashTraining + "/raw";
    private final String trashAudioChunk = trashTraining + "/chunks";
    private final String datasetFileNameCSV = "dataset.csv";
    private final String datasetFileNameARFF = "dataset.arff";
    private final String modelName = "training.model";

    public static Folders getInstance() {
        return ourInstance;
    }

    public String getBaseLogs() { return baseLogs; }

    public String getAudioChunks() { return audioChunks; }

    public String getRawAudioFiles() { return rawAudioFiles; }

    public String getTestingGeneratedDataset() {
        return testingGeneratedDataset;
    }

    public String getTrainingGeneratedDataset() { return trainingGeneratedDataset; }

    public String getTrainedModel() { return trainedModel; }

    public String getTmpFolder() { return tmpFolder; }

    public String getTrashTesting() {
        return trashTesting;
    }

    public String getTrashAudioRaw() {
        return trashAudioRaw;
    }

    public String getTrashAudioChunk() { return trashAudioChunk; }

    public String getDatasetFileNameCSV() { return datasetFileNameCSV; }

    public String getDatasetFileNameARFF() { return datasetFileNameARFF; }

    public String getModelName() { return modelName; }
}
