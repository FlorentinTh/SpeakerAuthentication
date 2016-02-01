package ca.uqac.florentinth.speakerauthentication.Audio.Recording;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.uqac.florentinth.speakerauthentication.Config.Audio;
import ca.uqac.florentinth.speakerauthentication.GUI.VisualizerView.CalculateVolumeListener;
import ca.uqac.florentinth.speakerauthentication.GUI.VisualizerView.VisualizerView;
import ca.uqac.florentinth.speakerauthentication.Utils.FileUtils;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public class VoiceRecorder {

    private AudioRecord recorder = null;
    private int bufferSize;
    private Thread recThread = null;
    private boolean isRecording = false;

    private CalculateVolumeListener volumeListener;
    private List<VisualizerView> visualizerViews = new ArrayList<>();

    public void link(VisualizerView visualizerView) {
        visualizerViews.add(visualizerView);
    }

    public void setVolumeListener(CalculateVolumeListener vl) {
        volumeListener = vl;
    }

    public void startRecording(final String sampleName) {

        bufferSize = AudioRecord.getMinBufferSize(Audio.getInstance().getSampleRate(), Audio
                .getInstance().getChannelType(), Audio.getInstance().getEncodingFormat());

        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, Audio.getInstance
                ().getSampleRate(), Audio.getInstance().getChannelType(), Audio.getInstance()
                .getEncodingFormat(), bufferSize);

        int state = recorder.getState();

        if(state == AudioRecord.STATE_INITIALIZED) {
            recorder.startRecording();
        }

        isRecording = true;

        recThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    createAudioFile(sampleName);
                } catch(IOException e) {
                    Log.e("VoiceRecorder", e.getMessage());
                }
            }
        }, "VoiceRecorder Thread");

        recThread.start();
    }

    private void createAudioFile(String sampleName) throws IOException {
        final byte[] data = new byte[bufferSize];
        String fileName = FileUtils.getSampleTMPFile(sampleName);
        FileOutputStream outputStream = new FileOutputStream(fileName);

        int read;

        if(outputStream != null) {
            while(isRecording) {
                read = recorder.read(data, 0, bufferSize);

                if(AudioRecord.ERROR_INVALID_OPERATION != read) {
                    outputStream.write(data);
                }

                int decibel = calculateDecibel(data);

                if(visualizerViews != null && !visualizerViews.isEmpty()) {
                    for(int i = 0; i < visualizerViews.size(); i++) {
                        visualizerViews.get(i).receive(decibel);
                    }
                }

                if(volumeListener != null) {
                    volumeListener.onCalculateVolume(decibel);
                }
            }

            outputStream.close();
        }
    }

    private int calculateDecibel(byte[] data) {
        int sum = 0;

        for(int i = 0; i < bufferSize; i++) {
            sum += Math.abs(data[i]);
        }

        return sum / bufferSize;
    }

    public void stopRecording(String sampleName) {
        if(recorder != null) {
            isRecording = false;

            int state = recorder.getState();

            if(state == AudioRecord.STATE_INITIALIZED) {
                recorder.stop();
            }

            recorder.release();
            recorder = null;
            recThread = null;

            if(visualizerViews != null && !visualizerViews.isEmpty()) {
                for(int i = 0; i < visualizerViews.size(); i++) {
                    visualizerViews.get(i).receive(0);
                }
            }
        }

        try {
            convertToWAVFile(FileUtils.getSampleTMPFile(sampleName), FileUtils.getSampleRawFile
                    (sampleName));
        } catch(IOException e) {
            Log.e("VoiceRecorder", e.getMessage());
        }

        FileUtils.deleteTMPFile(sampleName);
    }

    public void cancelRecording(String sampleName) {
        if(recorder != null) {
            isRecording = false;

            int state = recorder.getState();

            if(state == AudioRecord.STATE_INITIALIZED) {
                recorder.stop();
            }

            recorder.release();
            recorder = null;
            recThread = null;

            if(visualizerViews != null && !visualizerViews.isEmpty()) {
                for(int i = 0; i < visualizerViews.size(); i++) {
                    visualizerViews.get(i).receive(0);
                }
            }
        }

        FileUtils.deleteTMPFile(sampleName);
    }

    private void convertToWAVFile(String raw, String output) throws IOException {
        FileInputStream inputStream;
        FileOutputStream outputStream;
        long fileLength;
        long dataLength;
        long byteRate = Audio.getInstance().getBytePerSample() * Audio.getInstance()
                .getSampleRate() * 2 / 8;

        byte[] data = new byte[bufferSize];

        inputStream = new FileInputStream(raw);
        outputStream = new FileOutputStream(output);
        fileLength = inputStream.getChannel().size();
        dataLength = fileLength + 36;

        writeWAVFile(outputStream, fileLength, dataLength, Audio.getInstance().getSampleRate(),
                2, byteRate);

        while(inputStream.read(data) != -1) {
            outputStream.write(data);
        }

        inputStream.close();
        outputStream.close();
    }

    private void writeWAVFile(FileOutputStream output, Long fileLength, Long dataLength, Integer
            sampleRate, int channels, Long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (dataLength & 0xff);
        header[5] = (byte) ((dataLength >> 8) & 0xff);
        header[6] = (byte) ((dataLength >> 16) & 0xff);
        header[7] = (byte) ((dataLength >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8);
        header[33] = 0;
        header[34] = (byte) Audio.getInstance().getBytePerSample();
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (fileLength & 0xff);
        header[41] = (byte) ((fileLength >> 8) & 0xff);
        header[42] = (byte) ((fileLength >> 16) & 0xff);
        header[43] = (byte) ((fileLength >> 24) & 0xff);

        output.write(header, 0, 44);
    }
}
