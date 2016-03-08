package ca.uqac.florentinth.speakerauthentication.Audio;

import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.util.Timer;
import java.util.TimerTask;

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
