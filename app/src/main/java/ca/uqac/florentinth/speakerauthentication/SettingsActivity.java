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
import android.widget.SeekBar;
import android.widget.TextView;

import ca.uqac.florentinth.speakerauthentication.Config.Audio;
import ca.uqac.florentinth.speakerauthentication.Config.Location;
import ca.uqac.florentinth.speakerauthentication.Utils.ConvertUtils;

public class SettingsActivity extends AppCompatActivity {

    private View view;
    private EditText recordingTimeInput, chunkLengthInput;
    private SeekBar seekBarDistance;
    private TextView distanceValue;
    private FloatingActionButton btnEdit;

    private SharedPreferences sharedPreferences;

    private int recordingTime, chunkLength, distanceFromCenter;

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

        initGUI();
    }

    private void initGUI() {
        view = findViewById(R.id.settings_layout);
        recordingTimeInput = (EditText) findViewById(R.id.recording_time_input);
        chunkLengthInput = (EditText) findViewById(R.id.chunk_length_input);
        seekBarDistance = (SeekBar) findViewById(R.id.seekbar_distance);
        distanceValue = (TextView) findViewById(R.id.distance_from_center_value);
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
            distanceValue.setText(String.valueOf(distanceFromCenter));

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
        int distanceFromCenterValue = Integer.valueOf(distanceValue.getText().toString());

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
