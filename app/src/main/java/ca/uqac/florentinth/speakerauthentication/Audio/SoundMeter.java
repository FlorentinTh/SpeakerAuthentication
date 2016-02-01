package ca.uqac.florentinth.speakerauthentication.Audio;

import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.util.Timer;
import java.util.TimerTask;

import ca.uqac.florentinth.speakerauthentication.Config.Audio;

/**
 * Created by FlorentinTh on 11/16/2015.
 */
public class SoundMeter {
    private AudioRecord recorder;
    private int bufferSize;
    private short[] buffer;
    private int currentDecibels = 0;
    private int maxDecibels = 0;
    private Timer timer;

    public SoundMeter() {
        bufferSize = AudioRecord.getMinBufferSize(Audio.getInstance().getSampleRate(), Audio
                .getInstance().getChannelType(), Audio.getInstance().getEncodingFormat());

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, Audio.getInstance()
                .getSampleRate(), Audio.getInstance().getChannelType(), Audio.getInstance()
                .getEncodingFormat(), bufferSize);

        buffer = new short[bufferSize];
    }

    public int getCurrentDecibels() {
        return currentDecibels;
    }

    public int getMaxDecibels() {
        return maxDecibels;
    }

    private void updateBuffers() {
        int bufferReadResult;

        if(recorder != null) {
            bufferReadResult = recorder.read(buffer, 0, bufferSize);
            double sumLevel = 0;

            for(int i = 0; i < bufferReadResult; i++) {
                sumLevel += buffer[i];
            }

            currentDecibels = (int) Math.abs((sumLevel / bufferReadResult));

            if(currentDecibels > maxDecibels) {
                maxDecibels = currentDecibels;
            }
        }
    }

    public void start() {
        recorder.startRecording();
        timer = new Timer("SoundTimer", true);

        TimerTask timerTask = new TimerTask() {
            public void run() {
                updateBuffers();
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 250);
    }

    public void stop() {
        recorder.stop();
        timer.cancel();
    }
}
