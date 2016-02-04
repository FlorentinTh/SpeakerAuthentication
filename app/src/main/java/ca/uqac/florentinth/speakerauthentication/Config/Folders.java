package ca.uqac.florentinth.speakerauthentication.Config;

/**
 * Created by FlorentinTh on 11/12/2015.
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
