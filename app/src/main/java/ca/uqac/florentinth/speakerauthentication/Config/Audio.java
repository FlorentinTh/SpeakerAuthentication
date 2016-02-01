package ca.uqac.florentinth.speakerauthentication.Config;

import android.media.AudioFormat;

/**
 * Created by FlorentinTh on 11/12/2015.
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
