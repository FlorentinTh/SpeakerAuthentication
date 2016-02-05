package ca.uqac.florentinth.speakerauthentication.Audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ca.uqac.florentinth.speakerauthentication.Audio.WAV.WAVFile;
import ca.uqac.florentinth.speakerauthentication.Audio.WAV.WAVFileException;
import ca.uqac.florentinth.speakerauthentication.Config.Audio;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public abstract class AudioHelper {

    private static final int BUFFER_SIZE = 8192;

    private static short byteArrayToShort(byte[] bytes, int offset, boolean bigEndian) {
        int low, high;

        if(bigEndian) {
            low = bytes[offset + 1];
            high = bytes[offset + 0];
        } else {
            low = bytes[offset + 0];
            high = bytes[offset + 1];
        }
        return (short) ((high << 8) | (0xFF & low));
    }

    private static double[] readAudioInputStream(File file) throws IOException, WAVFileException {

        WAVFile wavFile = WAVFile.openWAVFile(file);
        InputStream inputStream = new FileInputStream(file);

        double[] audioSample = new double[(int) wavFile.getFrameNumber()];
        byte[] audioData = new byte[BUFFER_SIZE];
        int bytesToRead;
        int offset = 0;
        while((bytesToRead = inputStream.read(audioData)) > -1) {
            int counter = (bytesToRead / 2) + (bytesToRead % 2);

            for(int i = 0; i < counter; i++) {
                double tmp = (double) byteArrayToShort(audioData, 2 * i, false) / 32768;
                if(offset + i < ((int) wavFile.getFrameNumber())) {
                    audioSample[offset + i] = tmp;
                }
            }
            offset += counter;
        }

        inputStream.close();
        wavFile.close();
        return audioSample;
    }

    public static double[] convertFileToDoubleArray(File voiceSample, float voiceSampleRate)
            throws IOException, WAVFileException {

        WAVFile file = WAVFile.openWAVFile(voiceSample);

        float aDelta = Math.abs(file.getSampleRate() - voiceSampleRate);

        if(aDelta > 5 * Math.ulp(0.0f)) {
            throw new WAVFileException("[ERROR] Expected sample rate for audio file is: " + Audio
                    .getInstance().getSampleRate() + " instead of: " + file.getSampleRate());
        }

        file.close();

        return readAudioInputStream(voiceSample);
    }
}
