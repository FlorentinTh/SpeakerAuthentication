package ca.uqac.florentinth.speakerauthentication.Database.Models;

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
public class Location implements BaseSQL {

    public static final String TABLE = "Location";
    public static final String COL_ID = "id", COL_LABEL = "label", COL_ADDRESS = "address",
            COL_LATITUDE = "latitude", COL_LONGITUDE = "longitude";

    private int id;
    private String label, address;
    private double latitude, longitude;

    public Location() {}

    public Location(String label, String address, double latitude, double longitude) {
        this.label = label;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String createTable() {
        return "CREATE TABLE " + TABLE + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_LABEL + " TEXT, " +
                COL_ADDRESS + " TEXT, " +
                COL_LATITUDE + " REAL, " +
                COL_LONGITUDE + " REAL);";
    }

    @Override
    public String deleteTable() {
        return "DROP TABLE IF EXISTS " + TABLE;
    }
}
