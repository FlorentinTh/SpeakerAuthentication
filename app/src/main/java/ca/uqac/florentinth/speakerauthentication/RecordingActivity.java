package ca.uqac.florentinth.speakerauthentication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ca.uqac.florentinth.speakerauthentication.Audio.Recording.VoiceRecorder;
import ca.uqac.florentinth.speakerauthentication.Audio.Splitting.WAVSplitterAsyncTask;
import ca.uqac.florentinth.speakerauthentication.Config.Folders;
import ca.uqac.florentinth.speakerauthentication.Database.Controllers.UserController;
import ca.uqac.florentinth.speakerauthentication.Database.Models.User;
import ca.uqac.florentinth.speakerauthentication.GUI.JustifiedTextView.JustifiedTextView;
import ca.uqac.florentinth.speakerauthentication.GUI.VisualizerView.CalculateVolumeListener;
import ca.uqac.florentinth.speakerauthentication.GUI.VisualizerView.VisualizerView;
import ca.uqac.florentinth.speakerauthentication.Logger.Logger;
import ca.uqac.florentinth.speakerauthentication.Models.Story;
import ca.uqac.florentinth.speakerauthentication.Utils.ConvertUtils;
import ca.uqac.florentinth.speakerauthentication.Utils.FileUtils;
import ca.uqac.florentinth.speakerauthentication.Utils.JSONUtils;

public class RecordingActivity extends Activity implements CalculateVolumeListener {

    private static final String TAG = RecordingActivity.class.getSimpleName();
    private static final int COUNT_DOWN_TIMER_INTERVAL = ConvertUtils.secToMillis(1);
    private static final String TEXT_TO_READ_SOURCE = "stories.json";

    private VisualizerView visualizer;
    private TextView countDown;
    private JustifiedTextView textToRead;
    private FloatingActionButton btnStart;

    private CountDownTimer countDownTimer;
    private VoiceRecorder voiceRecorder;
    private Vibrator vibrator;
    private TextToSpeech textToSpeech;

    private SharedPreferences sharedPreferences;

    private String sampleName = null;
    private String dB = null;
    private int gender;

    private int recordingTime;
    private int chunkLength;

    private List<String> logValues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int idUser = sharedPreferences.getInt("user_id", -1);
        UserController userController = new UserController(this);

        if(idUser != -1) {
            User user = userController.getUserById(idUser);
            sampleName = user.getUsername();
            gender = user.getGender();
        }

        dB = sharedPreferences.getString("dB", null);
        recordingTime = sharedPreferences.getInt("recordingTime", -1);
        chunkLength = sharedPreferences.getInt("chunkLength", -1);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if(gender == 0) {
            logValues.add("Female");
        } else if(gender == 1) {
            logValues.add("Male");
        }

        initGUI();
        initTTS();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTTS();
        initTimer();
        btnStart.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(textToSpeech != null) {
            textToSpeech.stop();
        }

        voiceRecorder.cancelRecording(sampleName);
        countDownTimer.cancel();
        FileUtils.deleteTMPFile(Folders.getInstance().getTmpFolder() + "/" + sampleName + ".raw");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(textToSpeech != null) {
            textToSpeech.shutdown();
        }
    }

    private void initGUI() {
        visualizer = (VisualizerView) findViewById(R.id.visualizer);

        initVisualizer();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        countDown = (TextView) findViewById(R.id.count_down);
        initTimer();

        textToRead = (JustifiedTextView) findViewById(R.id.read);
        String json = JSONUtils.parseJSONFile(getApplicationContext(), TEXT_TO_READ_SOURCE);
        List<Story> texts = JSONUtils.JSONToTextsObject(json);
        int random = (int) (Math.random() * texts.size());
        textToRead.setText(texts.get(random).getText());

        btnStart = (FloatingActionButton) findViewById(R.id.btn_start);
        btnStart.setVisibility(View.VISIBLE);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeLog(sampleName, gender, dB, "Training");
                btnStart.setVisibility(View.GONE);
                countDownTimer.start();
                voiceRecorder.startRecording(sampleName);
            }
        });
    }

    private void writeLog(String username, int genderID, String dB, String stage) {
        String gender = null;

        if(genderID == 0) {
            gender = "Female";
        } else if(genderID == 1) {
            gender = "Male";
        }

        List<String> values = new ArrayList<>(Arrays.asList(username, gender, dB + "dB", stage));
        Logger.write(values);
    }

    private void initTimer() {
        countDownTimer = new CountDownTimer(recordingTime, COUNT_DOWN_TIMER_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                countDown.setText(String.valueOf(millisUntilFinished / 1000) + " " + getString(R
                        .string.countdown_run_label));
            }

            @Override
            public void onFinish() {
                voiceRecorder.stopRecording(sampleName);

                new WAVSplitterAsyncTask(RecordingActivity.this, new File(Environment
                        .getExternalStorageDirectory().getPath(), Folders.getInstance()
                        .getRawAudioFiles()), new File(Environment.getExternalStorageDirectory()
                        .getPath(), Folders.getInstance().getAudioChunks()), recordingTime,
                        chunkLength).execute(sampleName);


                vibrator.vibrate(500);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak(getString(R.string.tts_label), TextToSpeech.QUEUE_FLUSH,
                            null, null);
                } else {
                    textToSpeech.speak(getString(R.string.tts_label), TextToSpeech.QUEUE_FLUSH,
                            null);
                }

                btnStart.setVisibility(View.VISIBLE);
                this.cancel();
                countDown.setText(getString(R.string.countdown_init_label));
            }
        };

        countDown.setText(getString(R.string.countdown_init_label));
    }

    private void initTTS() {
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) { textToSpeech.setLanguage(Locale.getDefault()); }
            }
        });
    }

    private void initVisualizer() {
        ViewTreeObserver observer = visualizer.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                visualizer.setBaseY(visualizer.getHeight() / 5);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    visualizer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    visualizer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        voiceRecorder = new VoiceRecorder();
        voiceRecorder.link(visualizer);
        voiceRecorder.setVolumeListener(this);
    }

    @Override
    public void onCalculateVolume(int volume) {}
}
