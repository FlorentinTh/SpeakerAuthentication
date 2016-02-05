package ca.uqac.florentinth.speakerauthentication.GUI.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ca.uqac.florentinth.speakerauthentication.Core.App;
import ca.uqac.florentinth.speakerauthentication.Database.Controllers.LocationController;
import ca.uqac.florentinth.speakerauthentication.Database.Controllers.UserLocationController;
import ca.uqac.florentinth.speakerauthentication.Database.Models.Location;
import ca.uqac.florentinth.speakerauthentication.Database.Models.UserLocation;
import ca.uqac.florentinth.speakerauthentication.R;

/**
 * Created by FlorentinTh on 1/15/2016.
 */
public class LocationRecyclerViewAdapter extends RecyclerView.Adapter<LocationRecyclerViewAdapter
        .LocationViewHolder> {


    private static final String TAG = LocationRecyclerViewAdapter.class.getSimpleName();

    private Context context;
    private SharedPreferences sharedPreferences;
    private List<Location> locations;

    public LocationRecyclerViewAdapter(List<Location> locations, Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext());
        this.locations = locations;
        this.context = context;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_card_row,
                parent, false);

        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LocationViewHolder holder, final int position) {
        final String label = locations.get(position).getLabel(), address = locations.get
                (position).getAddress();
        holder.locationLabel.setText(label);
        holder.locationAdress.setText(address);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserLocationController userLocationController = new UserLocationController(App
                        .getAppContext());
                LocationController locationController = new LocationController(App.getAppContext());

                int userID = sharedPreferences.getInt("user_id", -1);

                if(userID != -1) {
                    locationController.delete(userLocationController.delete(new UserLocation
                            (userID, locations.get(position).getId())));
                    locations.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, locations.size());
                    notifyDataSetChanged();
                    Snackbar.make(v, label + " " + App.getAppContext().getString(R.string
                            .deleted_msg), Snackbar.LENGTH_LONG).show();
                } else {
                    Log.e(TAG, "ERROR occurs while trying to remove the location. Caused by " +
                            "userID.");
                }

                if(locations.size() <= 0) {
                    ((Activity) context).findViewById(R.id.layout_empty).setVisibility(View
                            .VISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public void update(List<Location> locations) {
        this.locations = locations;
        notifyDataSetChanged();

        if(locations.size() > 0) {
            ((Activity) context).findViewById(R.id.layout_empty).setVisibility(View.GONE);
        }
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder {
        public TextView locationLabel, locationAdress;
        public Button deleteButton;

        public LocationViewHolder(final View view) {
            super(view);
            locationLabel = (TextView) view.findViewById(R.id.location_label);
            locationAdress = (TextView) view.findViewById(R.id.location_adress);
            deleteButton = (Button) view.findViewById(R.id.btn_delete);
        }
    }
}
