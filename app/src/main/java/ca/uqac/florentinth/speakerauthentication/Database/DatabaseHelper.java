package ca.uqac.florentinth.speakerauthentication.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ca.uqac.florentinth.speakerauthentication.Models.Location;
import ca.uqac.florentinth.speakerauthentication.Models.User;
import ca.uqac.florentinth.speakerauthentication.Models.UserLocation;

/**
 * Created by FlorentinTh on 1/21/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "SpeakerAuthentication.db";

    private User user = new User();
    private Location location = new Location();
    private UserLocation userLocation = new UserLocation();

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(user.createTable());
        db.execSQL(location.createTable());
        db.execSQL(userLocation.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(userLocation.deleteTable());
        db.execSQL(location.deleteTable());
        db.execSQL(user.deleteTable());
        onCreate(db);
    }
}
