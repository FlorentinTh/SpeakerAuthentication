package ca.uqac.florentinth.speakerauthentication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import ca.uqac.florentinth.speakerauthentication.Database.Controllers.UserController;
import ca.uqac.florentinth.speakerauthentication.Database.Models.User;
import ca.uqac.florentinth.speakerauthentication.Utils.AndroidUtils;
import ca.uqac.florentinth.speakerauthentication.Utils.CryptUtils;
import ca.uqac.florentinth.speakerauthentication.Utils.StringUtils;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int EXTERNAL_STORAGE_REQUEST_CODE = 1;
    private static final int RECORD_AUDIO_REQUEST_CODE = 2;
    private static final int LOCATION_REQUEST_CODE = 3;

    private View view;
    private EditText usernameInput, passwordInput;
    private Button loginBtn, newAccountBtn;
    private ImageView visibiltyImage;

    private SharedPreferences sharedPreferences;

    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isUserLogged();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        initGUI();
    }

    @Override
    protected void onResume() {
        isUserLogged();
        super.onResume();
    }

    private void isUserLogged() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(sharedPreferences.contains("user_id")) {
            startActivity(new Intent(getApplicationContext(), MenuActivity.class));
            finish();
        }
    }

    private void initGUI() {
        view = findViewById(R.id.main_layout);
        usernameInput = (EditText) findViewById(R.id.username_input);
        passwordInput = (EditText) findViewById(R.id.password_input);
        visibiltyImage = (ImageView) findViewById(R.id.visibility);
        loginBtn = (Button) findViewById(R.id.btn_login);
        newAccountBtn = (Button) findViewById(R.id.btn_new_account);

        visibiltyImage.setOnTouchListener(new View.OnTouchListener() {
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

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkExternalStoragePermissions();
            }
        });

        newAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CreateActivity.class));
            }
        });
    }

    private void submit() {
        if(validateInputs()) {
            UserController userController = new UserController(this);
            User user = userController.getUserByUsername(username);

            if((username.equals(user.getUsername()) && CryptUtils.SHA256(password).equals(user
                    .getPassword()))) {
                sharedPreferences.edit().putInt("user_id", user.getId()).apply();
                startActivity(new Intent(this, MenuActivity.class));
                finish();
            } else {
                Snackbar.make(view, getString(R.string.login_failed), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void checkExternalStoragePermissions() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestExternalStoragePermissions();
        } else {
            checkLocationPermissions();
        }
    }

    private void checkLocationPermissions() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions();
        } else {
            checkRecordAudioPermission();
        }
    }

    private void checkRecordAudioPermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            requestRecordAudioPermissions();
        } else {
            submit();
        }
    }

    private void requestExternalStoragePermissions() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                .WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(view, getString(R.string.external_storage_permission_rationale),
                    Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.ok_label), new View
                    .OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest
                            .permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQUEST_CODE);
                }
            }).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission
                    .WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    private void requestLocationPermissions() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                .ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale
                (this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Snackbar.make(view, getString(R.string.location_permission_rationale), Snackbar
                    .LENGTH_INDEFINITE).setAction(getString(R.string.ok_label), new View
                    .OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest
                            .permission.ACCESS_COARSE_LOCATION, Manifest.permission
                            .ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                }
            }).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission
                    .ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
    }

    private void requestRecordAudioPermissions() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                .RECORD_AUDIO)) {
            Snackbar.make(view, getString(R.string.record_audio_permission_rationale), Snackbar
                    .LENGTH_INDEFINITE).setAction(getString(R.string.ok_label), new View
                    .OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest
                            .permission.RECORD_AUDIO}, RECORD_AUDIO_REQUEST_CODE);
                }
            }).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission
                    .RECORD_AUDIO}, RECORD_AUDIO_REQUEST_CODE);
        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == EXTERNAL_STORAGE_REQUEST_CODE) {
            if(AndroidUtils.verifyPermissions(grantResults)) {
                Snackbar.make(view, R.string.external_storage_permission_granted, Snackbar
                        .LENGTH_SHORT).show();
                checkLocationPermissions();
            } else {
                Snackbar.make(view, R.string.external_storage_permission_denied, Snackbar
                        .LENGTH_SHORT).show();
            }
        } else if(requestCode == LOCATION_REQUEST_CODE) {
            if(AndroidUtils.verifyPermissions(grantResults)) {
                Snackbar.make(view, R.string.location_permission_granted, Snackbar.LENGTH_SHORT)
                        .show();
                checkRecordAudioPermission();
            } else {
                Snackbar.make(view, R.string.location_permission_denied, Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else if(requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if(AndroidUtils.verifyPermissions(grantResults)) {
                Snackbar.make(view, R.string.record_audio_permission_granted, Snackbar
                        .LENGTH_SHORT).show();
                submit();
            } else {
                Snackbar.make(view, R.string.record_audio_permission_denied, Snackbar
                        .LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
