package ca.uqac.florentinth.speakerauthentication.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ca.uqac.florentinth.speakerauthentication.Database.DatabaseHelper;
import ca.uqac.florentinth.speakerauthentication.Models.Location;

/**
 * Created by FlorentinTh on 1/23/2016.
 */
public class LocationController {

    private DatabaseHelper dbHelper;

    public LocationController(Context ctx) {
        this.dbHelper = new DatabaseHelper(ctx);
    }

    public int insert(Location location) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Location.COL_LABEL, location.getLabel());
        values.put(Location.COL_ADDRESS, location.getAddress());
        values.put(Location.COL_LATITUDE, location.getLatitude());
        values.put(Location.COL_LONGITUDE, location.getLongitude());
        long id = db.insert(Location.TABLE, null, values);
        db.close();
        return (int) id;
    }

    public Location getLocationById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT " + Location.COL_ID + ", " +
                Location.COL_LABEL + ", " +
                Location.COL_ADDRESS + ", " +
                Location.COL_LATITUDE + ", " +
                Location.COL_LONGITUDE +
                " FROM " + Location.TABLE +
                " WHERE " + Location.COL_ID + "=?";
        Location location = new Location();
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(id)});

        if(cursor.moveToFirst()) {
            do {
                location.setId(cursor.getInt(cursor.getColumnIndex(Location.COL_ID)));
                location.setLabel(cursor.getString(cursor.getColumnIndex(Location.COL_LABEL)));
                location.setAddress(cursor.getString(cursor.getColumnIndex(Location.COL_ADDRESS)));
                location.setLatitude(cursor.getDouble(cursor.getColumnIndex(Location
                        .COL_LATITUDE)));
                location.setLongitude(cursor.getDouble(cursor.getColumnIndex(Location
                        .COL_LONGITUDE)));
            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return location;
    }

    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long returnValue = db.delete(Location.TABLE, Location.COL_ID + "=?", new String[] {String
                .valueOf(id)});
        db.close();
        return (int) returnValue;
    }
}
