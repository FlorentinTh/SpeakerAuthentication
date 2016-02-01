package ca.uqac.florentinth.speakerauthentication.Core;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import ca.uqac.florentinth.speakerauthentication.Audio.AudioHelper;
import ca.uqac.florentinth.speakerauthentication.Audio.PreProcessing.AudioNormalizer;
import ca.uqac.florentinth.speakerauthentication.Audio.PreProcessing.VoiceActivityDetection;
import ca.uqac.florentinth.speakerauthentication.Config.Audio;
import ca.uqac.florentinth.speakerauthentication.Config.Features;
import ca.uqac.florentinth.speakerauthentication.Config.Folders;
import ca.uqac.florentinth.speakerauthentication.Dataset.CSVWriter;
import ca.uqac.florentinth.speakerauthentication.Exceptions.WAVFileException;
import ca.uqac.florentinth.speakerauthentication.Features.FeaturesExtraction;
import ca.uqac.florentinth.speakerauthentication.Models.VoiceSample;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public class SpeakerRecognition<T> {

    private static final String TAG = SpeakerRecognition.class.getSimpleName();

    private final ConcurrentHashMap<T, VoicePrint> voicePrints = new ConcurrentHashMap<>();
    private final float sampleRate;
    private final AtomicBoolean universalVoicePrintWasSetByUser = new AtomicBoolean();
    private VoicePrint universalVoicePrint;
    private List<VoiceSample> voiceSamples = new ArrayList<>();

    public SpeakerRecognition(float sampleRate) {
        float minSampleRate = Audio.getInstance().getMinSampleRate();
        if(sampleRate < minSampleRate) {

            Log.e(TAG, "[ERROR] Sample rate should be at least : " + minSampleRate + " Hz. " + " " +
                    "Received : " + sampleRate + "Hz");
        }
        this.sampleRate = sampleRate;
    }

    public VoicePrint createVoicePrintFromFile(T userKey, File voiceSample, boolean training)
            throws IOException, WAVFileException {
        return createVoicePrint(userKey, AudioHelper.convertFileToDoubleArray(voiceSample,
                sampleRate), training);
    }

    public synchronized VoicePrint createVoicePrint(T userKey, double[] voiceSample, boolean
            training) throws IOException {
        if(userKey == null) {
            throw new NullPointerException("The userKey is null");
        }

        if(voicePrints.containsKey(userKey)) {
            throw new IllegalArgumentException("The userKey [" + userKey + "] already exists");
        }

        double[] voiceFeatures = extractVoiceFeatures(voiceSample, sampleRate);
        VoicePrint voicePrint = new VoicePrint(voiceFeatures);

        String formattedSampleName;

        if(training) {
            formattedSampleName = userKey.toString().substring(0, userKey.toString().length() - 2);
        } else {
            formattedSampleName = userKey.toString();
        }

        VoiceSample voiceSampleModel = new VoiceSample(formattedSampleName, voiceFeatures);
        voiceSamples.add(voiceSampleModel);

        CSVWriter csvWriter;

        if(training) {
            csvWriter = new CSVWriter(Folders.getInstance().getTrainingGeneratedDataset(),
                    voiceSamples);
        } else {
            csvWriter = new CSVWriter(Folders.getInstance().getTestingGeneratedDataset(),
                    voiceSamples);
        }

        csvWriter.writeCSVFileHeader();
        csvWriter.writeCSVFileContent();

        synchronized(this) {
            if(!universalVoicePrintWasSetByUser.get()) {
                if(universalVoicePrint == null) {
                    universalVoicePrint = new VoicePrint(voicePrint);
                } else {
                    universalVoicePrint.mergeVoicePrintFeatures(voiceFeatures);
                }
            }
        }

        return voicePrint;
    }

    private double[] extractVoiceFeatures(double[] voiceSample, float sampleRate) {

        VoiceActivityDetection voiceActivityDetection = new VoiceActivityDetection();
        AudioNormalizer audioNormalizer = new AudioNormalizer();
        FeaturesExtraction featuresExtraction = new FeaturesExtraction(sampleRate, Features
                .getInstance().getNumberFeatures());

        voiceActivityDetection.removeSliences(voiceSample, sampleRate);
        audioNormalizer.normalizeAudio(voiceSample);

        double[] voiceFeatures = featuresExtraction.extractVoiceFeatures(voiceSample);

        return voiceFeatures;
    }
}
