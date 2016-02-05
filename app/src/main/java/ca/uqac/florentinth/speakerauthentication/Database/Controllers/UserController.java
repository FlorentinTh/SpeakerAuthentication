package ca.uqac.florentinth.speakerauthentication.Database.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ca.uqac.florentinth.speakerauthentication.Database.DatabaseHelper;
import ca.uqac.florentinth.speakerauthentication.Database.Models.User;

/**
 * Created by FlorentinTh on 1/21/2016.
 */
public class UserController {

    private DatabaseHelper dbHelper;

    public UserController(Context ctx) {
        this.dbHelper = new DatabaseHelper(ctx);
    }

    public int insert(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(User.COL_USERNAME, user.getUsername());
        values.put(User.COL_GENDER, user.getGender());
        values.put(User.COL_PASSWORD, user.getPassword());
        long id = db.insert(User.TABLE, null, values);
        db.close();
        return (int) id;
    }

    public User getUserById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String select = "SELECT " + User.COL_USERNAME + ", " + User.COL_GENDER + ", " + User
                .COL_PASSWORD + " FROM " + User.TABLE + " WHERE " + User.COL_ID + "=?";

        User user = new User();
        Cursor cursor = db.rawQuery(select, new String[] {String.valueOf(id)});

        if(cursor.moveToFirst()) {
            do {
                user.setUsername(cursor.getString(cursor.getColumnIndex(User.COL_USERNAME)));
                user.setGender(cursor.getInt(cursor.getColumnIndex(User.COL_GENDER)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(User.COL_PASSWORD)));
            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return user;
    }

    public User getUserByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String select = "SELECT " + User.COL_ID + ", " + User.COL_USERNAME + ", " + User
                .COL_GENDER + ", " + User.COL_PASSWORD + " FROM " + User.TABLE + " WHERE " + User
                .COL_USERNAME + "=?";

        User user = new User();
        Cursor cursor = db.rawQuery(select, new String[] {String.valueOf(username)});

        if(cursor.moveToFirst()) {
            do {
                user.setId(cursor.getInt(cursor.getColumnIndex(User.COL_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndex(User.COL_USERNAME)));
                user.setGender(cursor.getInt(cursor.getColumnIndex(User.COL_GENDER)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(User.COL_PASSWORD)));
            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return user;
    }
}
