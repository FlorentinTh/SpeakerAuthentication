package ca.uqac.florentinth.speakerauthentication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;

import ca.uqac.florentinth.speakerauthentication.Audio.SoundMeter;
import ca.uqac.florentinth.speakerauthentication.Config.Folders;
import ca.uqac.florentinth.speakerauthentication.Controllers.UserController;
import ca.uqac.florentinth.speakerauthentication.Models.User;
import ca.uqac.florentinth.speakerauthentication.Utils.ConvertUtils;
import ca.uqac.florentinth.speakerauthentication.Utils.FileUtils;

public class MenuActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = MenuActivity.class.getSimpleName();
    private static final int RECORDING_TIME_DEFAULT = ConvertUtils.secToMillis(50),
            CHUNK_LENGTH_DEFAULT = ConvertUtils.secToMillis(10), DISTANCE_FROM_CENTER_DEFAULT =
            200, PLAY_SERVICES_RESOLUTION_REQUEST = 2306;

    private View view;

    private TextView usernameTextView;
    private Button btnLogout, btnTraining, btnRecognition, btnSettings, btnLocation;

    private Handler handler;
    private Runnable runnable;
    private SoundMeter soundMeter;

    private SharedPreferences sharedPreferences;
    private GoogleApiClient googleApiClient;
    private Location location;

    private User user;
    private boolean isListening = false;
    private double latitude, longitude;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(checkPlayServices()) {
            buildGoogleApiClient();
        }

        int idUser = sharedPreferences.getInt("user_id", -1);
        UserController userController = new UserController(this);

        if(idUser != -1) {
            user = userController.getUserById(idUser);
        }

        Log.i(TAG, String.valueOf(user.getGender()));

        initGUI();
        FileUtils.initFolders();
        initSettings();
    }

    @Override
    protected void onStart() {
        if(googleApiClient != null) {
            googleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        startSoundListening();
        checkPlayServices();
        getCoordinates();
        super.onResume();
    }

    @Override
    protected void onPause() {
        stopSoundListening();
        super.onPause();
    }

    @Override
    protected void onStop() {
        if(googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopSoundListening();
        super.onDestroy();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability instanceAPI = GoogleApiAvailability.getInstance();
        int resultCode = instanceAPI.isGooglePlayServicesAvailable(this);

        if(resultCode != ConnectionResult.SUCCESS) {
            if(instanceAPI.isUserResolvableError(resultCode)) {
                instanceAPI.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            }
            return false;
        }
        return true;
    }

    private synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    private void initGUI() {
        view = findViewById(R.id.menu_layout);
        usernameTextView = (TextView) findViewById(R.id.username);
        btnLogout = (Button) findViewById(R.id.btn_logout);
        btnTraining = (Button) findViewById(R.id.btn_training);
        btnRecognition = (Button) findViewById(R.id.btn_recognition);
        btnSettings = (Button) findViewById(R.id.btn_settings);
        btnLocation = (Button) findViewById(R.id.btn_location);

        usernameTextView.setText(user.getUsername());

        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                updateCurrentDecibel();
            }
        };

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().remove("user_id").apply();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        btnTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSoundListening();
                sharedPreferences.edit().putString("dB", String.valueOf(soundMeter.getMaxDecibels
                        ())).apply();
                startActivity(new Intent(MenuActivity.this, RecordingActivity.class));
            }
        });

        btnRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSoundListening();
                sharedPreferences.edit().putString("dB", String.valueOf(soundMeter.getMaxDecibels
                        ())).apply();
                startRecognitionActivity();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSoundListening();
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSoundListening();
                startActivity(new Intent(getApplicationContext(), LocationActivity.class));
            }
        });
    }

    private void getCoordinates() {
        if(location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            sharedPreferences.edit().putString("latitude", String.valueOf(latitude)).putString
                    ("longitude", String.valueOf(longitude)).apply();
        } else {
            Log.e(TAG, "Couldn't get the location. Make sure location is enabled on the device.");
        }
    }

    private void startRecognitionActivity() {
        if(isVoiceTrained()) {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        } else {
            Snackbar.make(view, getString(R.string.access_denied), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.train_action), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), RecordingActivity.class));
                }
            }).show();
        }
    }

    private boolean isVoiceTrained() {
        File modelFolder = new File(Environment.getExternalStorageDirectory().getPath(), Folders
                .getInstance().getTrainedModel());

        if(modelFolder.isDirectory() && modelFolder.listFiles().length > 0) {
            stopSoundListening();
            return true;
        }

        return false;
    }

    private void initSettings() {
        if(sharedPreferences.getInt("recordingTime", -1) == -1) {
            sharedPreferences.edit().putInt("recordingTime", RECORDING_TIME_DEFAULT).apply();
        }

        if(sharedPreferences.getInt("chunkLength", -1) == -1) {
            sharedPreferences.edit().putInt("chunkLength", CHUNK_LENGTH_DEFAULT).apply();
        }

        if(sharedPreferences.getInt("distanceFromCenter", -1) == -1) {
            sharedPreferences.edit().putInt("distanceFromCenter", DISTANCE_FROM_CENTER_DEFAULT)
                    .apply();
        }
    }

    private void updateCurrentDecibel() {
        int currentDecibels = soundMeter.getCurrentDecibels();

        TextView decibel = (TextView) findViewById(R.id.decibel);
        decibel.setText(String.valueOf(currentDecibels) + " dB");

        handler.post(runnable);
    }

    private void startSoundListening() {
        if(!isListening) {
            soundMeter = new SoundMeter();
            soundMeter.start();
            handler.post(runnable);
            isListening = true;
        }
    }

    private void stopSoundListening() {
        if(isListening) {
            soundMeter.stop();
            handler.removeCallbacks(runnable);
            isListening = false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(location == null) {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: [CODE: " + connectionResult.getErrorCode() + "]");
    }
}
