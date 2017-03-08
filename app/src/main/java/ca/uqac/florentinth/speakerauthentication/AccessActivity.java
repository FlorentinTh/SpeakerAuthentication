package ca.uqac.florentinth.speakerauthentication;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.uqac.florentinth.speakerauthentication.Accesses.Access;
import ca.uqac.florentinth.speakerauthentication.Audio.HeadsetStateReceiver;
import ca.uqac.florentinth.speakerauthentication.Audio.Recording.VoiceRecorder;
import ca.uqac.florentinth.speakerauthentication.Config.Folders;
import ca.uqac.florentinth.speakerauthentication.Database.Controllers.LocationController;
import ca.uqac.florentinth.speakerauthentication.Database.Controllers.UserController;
import ca.uqac.florentinth.speakerauthentication.Database.Controllers.UserLocationController;
import ca.uqac.florentinth.speakerauthentication.Database.Models.Location;
import ca.uqac.florentinth.speakerauthentication.Database.Models.User;
import ca.uqac.florentinth.speakerauthentication.Dataset.Builders.TestingDatasetBuilderAsyncTask;
import ca.uqac.florentinth.speakerauthentication.Learning.Test.PredictAsyncTask;
import ca.uqac.florentinth.speakerauthentication.Logger.Logger;
import ca.uqac.florentinth.speakerauthentication.Utils.ConvertUtils;

/**
 * Copyright 2016 Florentin Thullier.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class AccessActivity extends Activity {

    private static final String TAG = AccessActivity.class.getSimpleName();
    private static final int RECORDING_TIME = ConvertUtils.secToMillis(2);
    private static final int TIMER_INTERVAL = ConvertUtils.secToMillis(1);


    private View view;
    private FrameLayout accessLayout;
    private ProgressBar voiceProgress, locationProgress;
    private ImageView voiceResultTrue, voiceResultFalse, locationResultTrue, locationResultFalse;
    private TextView voiceText, locationText, accessText;
    private CountDownTimer countDownTimer;

    private SharedPreferences sharedPreferences;
    private HeadsetStateReceiver receiver = null;
    private VoiceRecorder voiceRecorder;

    private ArrayList locations = new ArrayList();
    private User user;
    private double latitude, longitude;

    private boolean isTrustedLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        startHeadsetStateListener();

        int idUser = sharedPreferences.getInt("user_id", -1);
        UserController userController = new UserController(this);

        if(idUser != -1) {
            user = userController.getUserById(idUser);
        }

        initGUI();
        getAllLocations();

        Time.start = System.currentTimeMillis();

        voiceRecognition();
    }

    @Override
    protected void onResume() {
        startHeadsetStateListener();
        checkCurrentLocation();
        super.onResume();
    }

    @Override
    protected void onPause() {
        stopHeadsetStateListener();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stopHeadsetStateListener();
        super.onDestroy();
    }

    private void voiceRecognition() {
        final String username = user.getUsername();
        final String dB = sharedPreferences.getString("dB", null);
        final boolean headsetState = sharedPreferences.getBoolean("headset_state", false);
        final Integer environmentID = sharedPreferences.getInt("id_environment", -1);
        final String headsetValue;
        final String environmentValue;

        voiceRecorder = new VoiceRecorder();

        if(headsetState == true) {
            headsetValue = "headset";
        } else {
            headsetValue = "no-headset";
        }

        if(environmentID == 0) {
            environmentValue = "quiet";
        } else {
            environmentValue = "noisy";
        }


        voiceRecorder.startRecording(username + "-" + headsetValue + "-" + environmentValue);

        writeLog(username, user.getGender(), environmentID, dB, headsetState, "Recognition");

        countDownTimer = new CountDownTimer(RECORDING_TIME, TIMER_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                voiceRecorder.stopRecording(username + "-" + headsetValue + "-" + environmentValue);
                this.cancel();

                Time.stop = System.currentTimeMillis();
                Log.e("TIME", "RECORDING#2 took: " + String.valueOf(Time.stop - Time.start) + "ms");

                new TestingDatasetBuilderAsyncTask(new File(Environment
                        .getExternalStorageDirectory().getPath(), Folders.getInstance()
                        .getRawAudioFiles()), new File(Environment.getExternalStorageDirectory()
                        .getPath(), Folders.getInstance().getTestingGeneratedDataset()), new
                        TestingDatasetBuilderAsyncTask.AsyncResponse() {

                    @Override
                    public void finishCallback() {
                        try {

                            Time.stop = System.currentTimeMillis();
                            Log.e("TIME", "Feature Extraction#2 took: " + String.valueOf(Time.stop - Time.start) + "ms");

                            new PredictAsyncTask(username, new FileInputStream(new File(Environment
                                    .getExternalStorageDirectory().getPath(), Folders.getInstance
                                    ().getTrainedModel() + "/" + Folders.getInstance()
                                    .getModelName())), new FileReader(new File(Environment
                                    .getExternalStorageDirectory().getPath(), Folders.getInstance
                                    ().getTestingGeneratedDataset() + "/" + Folders.getInstance()
                                    .getDatasetFileNameARFF())), new PredictAsyncTask
                                    .AsyncResponse() {
                                @Override
                                public void finishCallback(boolean predictResult) {

                                    Time.stop = System.currentTimeMillis();
                                    Log.e("TIME", "Testing#2 took: " + String.valueOf(Time.stop - Time.start) + "ms");

                                    boolean headsetState = sharedPreferences.getBoolean
                                            ("headset_state", false);

                                    if(predictResult) {
                                        setVoiceStateTrue();
                                        if(headsetState) {
                                            if(isTrustedLocation) {
                                                showPrivilegeAccess(Access.PRIVATE);
                                            }
                                        } else {
                                            showPrivilegeAccess(Access.PROTECTED);
                                        }
                                    } else {
                                        setVoiceStateFalse();
                                        showPrivilegeAccess(Access.PUBLIC);
                                    }
                                }
                            }).execute();
                        } catch(FileNotFoundException e) {
                            Log.e(TAG, "[ERROR] necessary files to be able to make predictions " +
                                    "are not found: " + e.getMessage());
                        }
                    }
                }).execute(username);
            }
        }.start();
    }

    private void startHeadsetStateListener() {
        if(receiver == null) {
            IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
            receiver = new HeadsetStateReceiver(true);
            registerReceiver(receiver, receiverFilter);
        }
    }

    private void stopHeadsetStateListener() {
        if(receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private void initGUI() {
        view = findViewById(R.id.access_layout);
        accessLayout = (FrameLayout) findViewById(R.id.layout_access_result);
        accessText = (TextView) findViewById(R.id.access_result);
        voiceProgress = (ProgressBar) findViewById(R.id.progress_voice);
        voiceResultTrue = (ImageView) findViewById(R.id.voice_result_true);
        voiceResultFalse = (ImageView) findViewById(R.id.voice_result_false);
        voiceText = (TextView) findViewById(R.id.voice_text);
        locationProgress = (ProgressBar) findViewById(R.id.location_progress);
        locationResultTrue = (ImageView) findViewById(R.id.location_result_true);
        locationResultFalse = (ImageView) findViewById(R.id.location_result_false);
        locationText = (TextView) findViewById(R.id.location_text);
    }

    private void getAllLocations() {
        UserLocationController userLocationController = new UserLocationController
                (getApplicationContext());
        LocationController locationController = new LocationController(getApplicationContext());

        List<Integer> idLocations = new ArrayList<>();
        int userID = sharedPreferences.getInt("user_id", -1);

        if(userID != -1) {
            idLocations = userLocationController.getLocationsByUserId(userID);
        } else {
            Log.e(TAG, "default userID value cannot be used");
        }

        if(idLocations.size() > 0) {
            for(Integer id : idLocations) {
                locations.add(locationController.getLocationById(id));
            }
        }
    }

    private void checkCurrentLocation() {
        String storedLatitude = sharedPreferences.getString("latitude", null);
        String storedLongitude = sharedPreferences.getString("longitude", null);

        if((storedLatitude != null) && (storedLongitude != null)) {
            latitude = Double.valueOf(storedLatitude);
            longitude = Double.valueOf(storedLongitude);

            if(latitude != 0 && longitude != 0) {
                if(isTrustedLocation(latitude, longitude, locations)) {
                    setLocationStateTrue();
                    isTrustedLocation = true;
                } else {
                    setLocationStateFalse();
                }
            }
        } else {
            Log.e(TAG, "[ERROR] Impossible to get location coordinates.");
        }
    }

    private void showPrivilegeAccess(Access access) {
        accessLayout.setVisibility(View.VISIBLE);
        accessText.setText(String.valueOf(access) + " access granted");
        switch(access) {
            case PUBLIC:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    accessText.setTextColor(getColor(R.color.md_red_500));
                } else {
                    accessText.setTextColor(this.getResources().getColor(R.color.md_red_500));
                }
                break;
            case PROTECTED:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    accessText.setTextColor(getColor(R.color.md_orange_500));
                } else {
                    accessText.setTextColor(this.getResources().getColor(R.color.md_orange_500));
                }
                break;
            case PRIVATE:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    accessText.setTextColor(getColor(R.color.md_green_500));
                } else {
                    accessText.setTextColor(this.getResources().getColor(R.color.md_green_500));
                }
                break;
        }
    }

    private void setVoiceStateTrue() {
        voiceProgress.setVisibility(View.GONE);
        voiceResultFalse.setVisibility(View.GONE);
        voiceResultTrue.setVisibility(View.VISIBLE);
        voiceText.setText("Voice recognized");
        voiceText.setTextColor(getResources().getColor(R.color.md_green_500));
    }

    private void setVoiceStateFalse() {
        voiceProgress.setVisibility(View.GONE);
        voiceResultTrue.setVisibility(View.GONE);
        voiceResultFalse.setVisibility(View.VISIBLE);
        voiceText.setText("Voice not recognized");
        voiceText.setTextColor(getResources().getColor(R.color.md_red_500));
    }

    private void setLocationStateTrue() {
        locationProgress.setVisibility(View.GONE);
        locationResultFalse.setVisibility(View.GONE);
        locationResultTrue.setVisibility(View.VISIBLE);
        locationText.setText("Trusted location");
        locationText.setTextColor(getResources().getColor(R.color.md_green_500));
    }

    private void setLocationStateFalse() {
        locationProgress.setVisibility(View.GONE);
        locationResultTrue.setVisibility(View.GONE);
        locationResultFalse.setVisibility(View.VISIBLE);
        locationText.setText("Not a trusted location");
        locationText.setTextColor(getResources().getColor(R.color.md_red_500));
    }

    private boolean isTrustedLocation(double latitude, double longitude, List<Location> locations) {
        if(locations.size() > 0) {
            for(Location location : locations) {
                float[] distance = new float[2];
                android.location.Location.distanceBetween(latitude, longitude, location
                        .getLatitude(), location.getLongitude(), distance);

                int distanceFromCenter = sharedPreferences.getInt("distanceFromCenter", -1);
                if(distanceFromCenter != -1) {
                    if(distance[0] < distanceFromCenter) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void writeLog(String username, int genderID, int environmentID, String dB, boolean
            headsetState, String
            stage) {
        String gender = null, environment = null, headsetValue;

        switch(genderID) {
            case 0:
                gender = "Female";
                break;
            case 1:
                gender = "Male";
                break;
        }

        switch(environmentID) {
            case 0:
                environment = "Quiet";
                break;
            case 1:
                environment = "Noisy";
                break;
        }

        if(headsetState == true) {
            headsetValue = "Headset-Plugged";
        } else {
            headsetValue = "Headset-Unplugged";
        }

        List<String> values = new ArrayList<>(Arrays.asList(username, gender, environment, dB +
                "dB", headsetValue, stage));
        Logger.write(values);
    }
}
