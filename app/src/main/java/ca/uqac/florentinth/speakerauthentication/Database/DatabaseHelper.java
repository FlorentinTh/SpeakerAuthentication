package ca.uqac.florentinth.speakerauthentication.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ca.uqac.florentinth.speakerauthentication.Database.Models.Location;
import ca.uqac.florentinth.speakerauthentication.Database.Models.User;
import ca.uqac.florentinth.speakerauthentication.Database.Models.UserLocation;

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
