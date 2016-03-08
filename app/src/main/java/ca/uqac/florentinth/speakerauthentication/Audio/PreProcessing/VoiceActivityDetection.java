package ca.uqac.florentinth.speakerauthentication.Audio.PreProcessing;

import java.util.Arrays;

import ca.uqac.florentinth.speakerauthentication.Utils.ConvertUtils;

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
public class VoiceActivityDetection {

    private static final double SILENCE_THRESHOLD = 0.0001d;
    private static final int MIN_VOICE_TIME = 200, ACTIVITY_DETECTION_MIN_SILENCE_TIME = 4,
            TIME_ACTIVITY_DETECTION_WINDOW = 1, ACTIVITY_DETECTION_FADING_TIME = 2;

    private double[] fadeInFactors, fadeOutFactors;

    private int getMinVoiceActivityLength(float sampleRate) {
        return MIN_VOICE_TIME * ConvertUtils.kiloHeztToHertz(sampleRate);
    }

    private double meanCorrelationValue(double[] voiceSample, double[] buffer) {
        Arrays.fill(buffer, 0);

        for(int i = 0; i < voiceSample.length; i++) {
            for(int j = 0; j < voiceSample.length; j++) {
                buffer[i] += voiceSample[j] * voiceSample[(voiceSample.length + j - i) %
                        voiceSample.length];
            }
        }

        double mean = 0.0d;

        for(int i = 0; i < voiceSample.length; i++) {
            mean += buffer[i];
        }

        return mean / buffer.length;
    }

    private void mergeSilenceAreas(boolean[] result, int minSilenceLength) {
        boolean isAreaActive;
        int increment;

        for(int i = 0; i < result.length; i += increment) {
            isAreaActive = result[i];
            increment = 1;

            while((i + increment < result.length) && result[i + increment] == isAreaActive) {
                increment++;
            }

            if(!isAreaActive && increment < minSilenceLength) {
                Arrays.fill(result, i, i + increment, !isAreaActive);
            }
        }
    }

    private int mergeActiveAreas(boolean[] result, int minActivityLength) {
        boolean isAreaActive;
        int increment;
        int silenceCounter = 0;

        for(int i = 0; i < result.length; i += increment) {
            isAreaActive = result[i];
            increment = 1;

            while((i + increment < result.length) && result[i + increment] == isAreaActive) {
                increment++;
            }

            if(isAreaActive && increment < minActivityLength) {
                Arrays.fill(result, i, i + increment, !isAreaActive);
                silenceCounter += increment;
            }

            if(!isAreaActive) {
                silenceCounter += increment;
            }
        }

        return silenceCounter;
    }

    private void initFadingFactors(int fadingLength) {
        fadeInFactors = new double[fadingLength];

        for(int i = 0; i < fadingLength; i++) {
            fadeInFactors[i] = (1.0d / fadingLength) * i;
        }

        fadeOutFactors = new double[fadingLength];

        for(int i = 0; i < fadingLength; i++) {
            fadeOutFactors[i] = 1.0d - fadeInFactors[i];
        }
    }

    private void applyFadeInFadeOutFactors(double[] voiceSample, int fadeLength, int startIndex,
                                           int endIndex) {
        int fadeOutStart = endIndex - startIndex;

        for(int i = 0; i < fadeLength; i++) {
            voiceSample[startIndex + i] *= fadeInFactors[i];
            voiceSample[fadeOutStart + i] *= fadeOutFactors[i];
        }
    }

    public double[] removeSilences(double[] voiceSample, float sampleRate) {
        int millisSample = ConvertUtils.kiloHeztToHertz(sampleRate);
        int minSilenceLength = ACTIVITY_DETECTION_MIN_SILENCE_TIME * millisSample;
        int minActivityLength = getMinVoiceActivityLength(sampleRate);
        boolean[] result = new boolean[voiceSample.length];

        if(voiceSample.length < minActivityLength) {
            return voiceSample;
        }

        int windowSize = TIME_ACTIVITY_DETECTION_WINDOW * millisSample;
        double[] correlation = new double[windowSize];
        double[] window = new double[windowSize];

        for(int i = 0; i + windowSize < voiceSample.length; i += windowSize) {
            System.arraycopy(voiceSample, i, window, 0, windowSize);
            double meanValue = meanCorrelationValue(window, correlation);
            Arrays.fill(result, i, i + windowSize, meanValue > SILENCE_THRESHOLD);
        }

        mergeSilenceAreas(result, minSilenceLength);

        int silenceCounter = mergeActiveAreas(result, minActivityLength);

        if(silenceCounter > 0) {
            int fadingLength = ACTIVITY_DETECTION_FADING_TIME * millisSample;
            initFadingFactors(fadingLength);
            double[] shortenedVoiceSample = new double[voiceSample.length - silenceCounter];
            int copyCounter = 0;

            for(int i = 0; i < result.length; i++) {
                if(result[i]) {
                    int startIndex = i;
                    int counter = 0;

                    while(i < result.length && result[i++]) {
                        counter++;
                    }

                    int endIndex = startIndex + counter;
                    applyFadeInFadeOutFactors(voiceSample, fadingLength, startIndex, endIndex);
                    System.arraycopy(voiceSample, startIndex, shortenedVoiceSample, copyCounter,
                            counter);
                    copyCounter += counter;
                }
            }

            return shortenedVoiceSample;

        } else {
            return voiceSample;
        }
    }
}
