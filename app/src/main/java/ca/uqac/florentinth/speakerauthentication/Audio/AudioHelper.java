package ca.uqac.florentinth.speakerauthentication.Audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ca.uqac.florentinth.speakerauthentication.Audio.WAV.WAVFile;
import ca.uqac.florentinth.speakerauthentication.Audio.WAV.WAVFileException;
import ca.uqac.florentinth.speakerauthentication.Config.Audio;

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
