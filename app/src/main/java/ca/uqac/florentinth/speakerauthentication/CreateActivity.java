package ca.uqac.florentinth.speakerauthentication;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import ca.uqac.florentinth.speakerauthentication.Database.Controllers.UserController;
import ca.uqac.florentinth.speakerauthentication.Database.Models.User;
import ca.uqac.florentinth.speakerauthentication.Utils.CryptUtils;
import ca.uqac.florentinth.speakerauthentication.Utils.StringUtils;

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
public class CreateActivity extends AppCompatActivity {

    private static final String TAG = CreateActivity.class.getSimpleName();

    private View view;
    private EditText usernameInput, passwordInput;
    private RadioButton femaleRadio, maleRadio;
    private ImageView visibilityImage;
    private Button createBtn;

    private String username, password;
    private int gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        initGUI();
    }

    private void initGUI() {
        view = findViewById(R.id.create_layout);
        usernameInput = (EditText) findViewById(R.id.username_input);
        passwordInput = (EditText) findViewById(R.id.password_input);
        visibilityImage = (ImageView) findViewById(R.id.visibility);
        femaleRadio = (RadioButton) findViewById(R.id.radio_female);
        maleRadio = (RadioButton) findViewById(R.id.radio_male);
        createBtn = (Button) findViewById(R.id.btn_create);

        usernameInput.setText("user" + StringUtils.getCurrentDateTime());

        visibilityImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType
                                .TYPE_TEXT_VARIATION_PASSWORD);
                    }
                }
                return true;
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void submit() {
        if(validateInputs()) {
            UserController userController = new UserController(this);
            userController.insert(new User(username, getGender(), CryptUtils.SHA256(password)));
            finish();
        }
    }

    public int getGender() {
        if(femaleRadio.isChecked()) {
            return 0;
        }
        return 1;
    }

    private boolean validateInputs() {
        username = usernameInput.getText().toString().trim().toLowerCase();
        password = passwordInput.getText().toString().trim();

        if(username.equals("")) {
            Snackbar.make(view, getString(R.string.username_empty_error), Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }

        if(!StringUtils.isAlNum(username)) {
            Snackbar.make(view, getString(R.string.username_special_chars_error), Snackbar
                    .LENGTH_LONG).show();
            return false;
        }

        if(username.length() <= 2) {
            Snackbar.make(view, getString(R.string.username_length_error), Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }

        if(password.equals("")) {
            Snackbar.make(view, getString(R.string.password_empty_error), Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }

        if(password.length() <= 5) {
            Snackbar.make(view, getString(R.string.password_length_error), Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }

        return true;
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
