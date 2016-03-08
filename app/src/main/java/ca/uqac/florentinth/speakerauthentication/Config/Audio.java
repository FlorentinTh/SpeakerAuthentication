package ca.uqac.florentinth.speakerauthentication.Config;

import android.media.AudioFormat;

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
public class Audio {

    private static Audio ourInstance = new Audio();
    private final int sampleRate = 44100;
    private final int bytePerSample = 16;
    private final int channelType = AudioFormat.CHANNEL_IN_STEREO;
    private final int encodingFormat = AudioFormat.ENCODING_PCM_16BIT;
    private final float minSampleRate = 8000.0f;
    private final int minRecordingTime = 10; //sec
    private final int minChunkLength = 1; //sec

    public static Audio getInstance() {
        return ourInstance;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getBytePerSample() { return bytePerSample; }

    public int getChannelType() { return channelType; }

    public int getEncodingFormat() {
        return encodingFormat;
    }

    public float getMinSampleRate() { return minSampleRate; }

    public int getMinRecordingTime() { return minRecordingTime; }

    public int getMinChunkLength() { return minChunkLength; }
}
