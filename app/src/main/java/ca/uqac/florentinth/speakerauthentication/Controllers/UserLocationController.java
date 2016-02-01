package ca.uqac.florentinth.speakerauthentication.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.florentinth.speakerauthentication.Database.DatabaseHelper;
import ca.uqac.florentinth.speakerauthentication.Models.UserLocation;

/**
 * Created by FlorentinTh on 1/23/2016.
 */
public class UserLocationController {

    private DatabaseHelper dbHelper;

    public UserLocationController(Context ctx) {
        this.dbHelper = new DatabaseHelper(ctx);
    }

    public int insert(UserLocation userLocation) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserLocation.COL_ID_USER, userLocation.getIdUser());
        values.put(UserLocation.COL_ID_LOCATION, userLocation.getIdLocation());
        long id = db.insert(UserLocation.TABLE, null, values);
        db.close();
        return (int) id;
    }

    public List<Integer> getLocationsByUserId(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT " +
                UserLocation.COL_ID_LOCATION +
                " FROM " + UserLocation.TABLE +
                " WHERE " + UserLocation.COL_ID_USER + "=?";
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(id)});
        ArrayList<Integer> locations = new ArrayList<>();

        if(cursor.moveToFirst()) {
            do {
                locations.add(cursor.getInt(cursor.getColumnIndex(UserLocation.COL_ID_LOCATION)));
            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return locations;
    }

    public int delete(UserLocation userLocation) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long returnValue = db.delete(UserLocation.TABLE, UserLocation.COL_ID_USER + "=? AND " +
                UserLocation.COL_ID_LOCATION + "=?", new String[] {String.valueOf(userLocation
                .getIdUser()), String.valueOf(userLocation.getIdLocation())});
        db.close();
        return (int) returnValue;
    }
}
