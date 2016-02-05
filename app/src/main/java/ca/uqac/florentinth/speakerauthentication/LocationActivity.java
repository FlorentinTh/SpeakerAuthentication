package ca.uqac.florentinth.speakerauthentication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.uqac.florentinth.speakerauthentication.Database.Controllers.LocationController;
import ca.uqac.florentinth.speakerauthentication.Database.Controllers.UserLocationController;
import ca.uqac.florentinth.speakerauthentication.Database.Models.Location;
import ca.uqac.florentinth.speakerauthentication.Database.Models.UserLocation;
import ca.uqac.florentinth.speakerauthentication.GUI.Adapters.LocationRecyclerViewAdapter;
import ca.uqac.florentinth.speakerauthentication.Utils.StringUtils;

public class LocationActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LocationActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 2306;

    private View view;
    private RecyclerView recyclerView;
    private FloatingActionButton locateBtn;

    private SharedPreferences sharedPreferences;

    private GoogleApiClient googleApiClient;
    private android.location.Location location;

    private List<Location> locations = new ArrayList<>();
    private LocationRecyclerViewAdapter adapter = new LocationRecyclerViewAdapter(locations, this);
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(checkPlayServices()) {
            buildGoogleApiClient();
        }

        getAllLocations();
        initGUI();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
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
            FrameLayout emptyLayout = (FrameLayout) findViewById(R.id.layout_empty);
            emptyLayout.setVisibility(View.GONE);

            for(Integer id : idLocations) {
                locations.add(locationController.getLocationById(id));
            }
        } else {
            findViewById(R.id.layout_empty).setVisibility(View.VISIBLE);
        }
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
        view = findViewById(R.id.location_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        locateBtn = (FloatingActionButton) findViewById(R.id.btn_locate);

        locateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCoordinates();
                if(!isLocationExists(latitude, longitude, locations)) {
                    showAddLocationPrompt(getAddressFromCoords(latitude, longitude));
                } else {
                    Snackbar.make(view, getString(R.string.existing_location), Snackbar
                            .LENGTH_LONG).show();
                }
            }
        });
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

    private void showAddLocationPrompt(final String locationAddress) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View prompt = layoutInflater.inflate(R.layout.prompt, null);
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setView(prompt);

        final EditText labelInput = (EditText) prompt.findViewById(R.id.location_label_input);
        final TextView locationAddressView = (TextView) prompt.findViewById(R.id
                .location_address_prompt);

        locationAddressView.setText(locationAddress);

        alertBuilder.setCancelable(true).setPositiveButton(getString(R.string.add_label), new
                DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserLocationController userLocationController = new UserLocationController
                        (getApplicationContext());
                LocationController locationController = new LocationController
                        (getApplicationContext());

                if(validateInputs(labelInput.getText().toString().trim())) {
                    int userID = sharedPreferences.getInt("user_id", -1);
                    if(userID != -1) {
                        Location location = new Location(labelInput.getText().toString().trim(),
                                locationAddress, latitude, longitude);
                        int insertCode = userLocationController.insert(new UserLocation(userID,
                                locationController.insert(location)));
                        if(insertCode > 0) {
                            Snackbar.make(view, getString(R.string.location_added), Snackbar
                                    .LENGTH_LONG).show();
                            locations.add(location);
                            adapter.update(locations);
                        } else {
                            Log.e(TAG, "Problem occurs while trying to add new location.");
                        }
                    }
                }
            }
        }).create().show();
    }

    private boolean isLocationExists(double latitude, double longitude, List<Location> locations) {
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

    private void getCoordinates() {
        if(location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } else {
            Log.e(TAG, "Couldn't get the location. Make sure location is enabled on the device.");
        }
    }

    private String getAddressFromCoords(double latitude, double longitude) {
        String fullAddress = "";

        try {
            Address address = new Geocoder(this, Locale.getDefault()).getFromLocation(latitude,
                    longitude, 1).get(0);

            if(address.getAddressLine(0) != null) {
                fullAddress = address.getAddressLine(0);
            }

            if(address.getSubLocality() != null) {
                fullAddress += "\n" + address.getSubLocality();
            }

            if(address.getCountryName() != null) {
                fullAddress += ", " + address.getCountryName();
            }

            if(address.getPostalCode() != null) {
                fullAddress += ", " + address.getPostalCode();
            }

        } catch(IOException e) {
            Log.e(TAG, "[ERROR] error occurs while trying to recover address from coordinates: "
                    + e.getMessage());
        }

        return fullAddress;
    }

    private boolean validateInputs(String inputValue) {

        if(inputValue.equals("")) {
            Snackbar.make(view, getString(R.string.location_label_empty), Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }

        if(!StringUtils.isAlNum(inputValue)) {
            Snackbar.make(view, getString(R.string.location_label_format), Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }

        return true;
    }
}
