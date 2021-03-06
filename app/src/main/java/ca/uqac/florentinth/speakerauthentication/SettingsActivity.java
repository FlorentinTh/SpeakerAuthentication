package ca.uqac.florentinth.speakerauthentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import ca.uqac.florentinth.speakerauthentication.Config.Audio;
import ca.uqac.florentinth.speakerauthentication.Config.Location;
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
public class SettingsActivity extends AppCompatActivity {

    private View view;
    private EditText recordingTimeInput, chunkLengthInput;
    private SeekBar seekBarDistance;
    private TextView distanceValue;
    private RadioButton quietRadio, noisyRadio;
    private FloatingActionButton btnEdit;

    private SharedPreferences sharedPreferences;

    private int recordingTime, chunkLength, distanceFromCenter, environment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_settings);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        recordingTime = sharedPreferences.getInt("recordingTime", -1);
        chunkLength = sharedPreferences.getInt("chunkLength", -1);
        distanceFromCenter = sharedPreferences.getInt("distanceFromCenter", -1);
        environment = sharedPreferences.getInt("id_environment", -1);

        initGUI();
    }

    private void initGUI() {
        view = findViewById(R.id.settings_layout);
        recordingTimeInput = (EditText) findViewById(R.id.recording_time_input);
        chunkLengthInput = (EditText) findViewById(R.id.chunk_length_input);
        seekBarDistance = (SeekBar) findViewById(R.id.seekbar_distance);
        distanceValue = (TextView) findViewById(R.id.distance_from_center_value);
        quietRadio = (RadioButton) findViewById(R.id.radio_quiet);
        noisyRadio = (RadioButton) findViewById(R.id.radio_noisy);
        btnEdit = (FloatingActionButton) findViewById(R.id.btn_edit);

        seekBarDistance.setMax(Location.getInstance().getMaxDistanceFromCenter());

        if(recordingTime != -1) {
            recordingTimeInput.setText(String.valueOf(ConvertUtils.millisecToSec(recordingTime)));
        }

        if(chunkLength != -1) {
            chunkLengthInput.setText(String.valueOf(ConvertUtils.millisecToSec(chunkLength)));
        }

        if(distanceFromCenter != -1) {
            seekBarDistance.setProgress(distanceFromCenter);
            distanceValue.setText(String.valueOf(distanceFromCenter) + "m");

        }

        seekBarDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress < Location.getInstance().getMinDistanceFromCenter()) {
                    seekBar.setProgress(Location.getInstance().getMinDistanceFromCenter());
                } else {
                    distanceValue.setText(String.valueOf(progress) + "m");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        switch(environment) {
            case 0:
                quietRadio.setChecked(true);
                break;
            case 1:
                noisyRadio.setChecked(true);
                break;
            default:
                quietRadio.setChecked(true);
        }

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });
    }

    private void updateSettings() {
        int recordingTimeValue = Integer.valueOf(recordingTimeInput.getText().toString());
        int chunkLengthValue = Integer.valueOf(chunkLengthInput.getText().toString());
        int distanceFromCenterValue = Integer.valueOf(distanceValue.getText().subSequence(0, 3)
                .toString());

        if(recordingTimeValue < Audio.getInstance().getMinRecordingTime()) {
            Snackbar.make(view, getString(R.string.recording_time_error) + " " + String.valueOf
                    (Audio.getInstance().getMinRecordingTime()), Snackbar.LENGTH_LONG).show();
        } else if(chunkLengthValue < Audio.getInstance().getMinChunkLength()) {
            Snackbar.make(view, getString(R.string.chunk_length_error) + " " + String.valueOf
                    (Audio.getInstance().getMinChunkLength()), Snackbar.LENGTH_LONG).show();
        } else if(distanceFromCenterValue < Location.getInstance().getMinDistanceFromCenter()) {
            Snackbar.make(view, getString(R.string.distance_from_center_error) + " " + String
                    .valueOf(Location.getInstance().getMinDistanceFromCenter()), Snackbar
                    .LENGTH_LONG).show();
        } else {
            sharedPreferences.edit().putInt("recordingTime", ConvertUtils.secToMillis
                    (recordingTimeValue)).apply();
            sharedPreferences.edit().putInt("chunkLength", ConvertUtils.secToMillis
                    (chunkLengthValue)).apply();
            sharedPreferences.edit().putInt("distanceFromCenter", distanceFromCenterValue).apply();

            if(quietRadio.isChecked()) {
                sharedPreferences.edit().putInt("id_environment", 0).apply();
            } else {
                sharedPreferences.edit().putInt("id_environment", 1).apply();
            }

            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if(NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
