package ca.uqac.florentinth.speakerauthentication.Audio;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ca.uqac.florentinth.speakerauthentication.Core.App;
import ca.uqac.florentinth.speakerauthentication.R;

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
public class HeadsetStateReceiver extends BroadcastReceiver {

    private static final String TAG = HeadsetStateReceiver.class.getName();

    private ProgressBar headsetProgess;
    private ImageView headsetResultTrue, headsetResultFalse;
    private TextView headsetText;

    private SharedPreferences sharedPreferences;

    private boolean view;

    public HeadsetStateReceiver(boolean view) {
        this.view = view;
    }

    private void switchState(Context context, boolean state) {
        headsetProgess = (ProgressBar) ((Activity) context).findViewById(R.id.progress_headset);
        headsetResultTrue = (ImageView) ((Activity) context).findViewById(R.id.headset_result_true);
        headsetResultFalse = (ImageView) ((Activity) context).findViewById(R.id
                .headset_result_false);
        headsetText = (TextView) ((Activity) context).findViewById(R.id.headset_text);
        headsetProgess.setVisibility(View.GONE);

        if(state) {
            headsetResultFalse.setVisibility(View.GONE);
            headsetResultTrue.setVisibility(View.VISIBLE);
            headsetText.setText("Headset plugged");
            headsetText.setTextColor(context.getResources().getColor(R.color.md_green_500));
        } else {
            headsetResultTrue.setVisibility(View.GONE);
            headsetResultFalse.setVisibility(View.VISIBLE);
            headsetText.setText("No headset plugged");
            headsetText.setTextColor(context.getResources().getColor(R.color.md_red_500));
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext());

        if(intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            int micro = intent.getIntExtra("microphone", -1);

            if((state != -1) && (micro != -1)) {
                if(state == 1 && micro == 1) {
                    if(view) {
                        switchState(context, true);
                    }
                    sharedPreferences.edit().putBoolean("headset_state", true).apply();
                } else {
                    if(view) {
                        switchState(context, false);
                    }
                    sharedPreferences.edit().putBoolean("headset_state", false).apply();
                }
            } else {
                Log.e(TAG, "ERROR occurs in headphone state listener");
            }
        }
    }
}
