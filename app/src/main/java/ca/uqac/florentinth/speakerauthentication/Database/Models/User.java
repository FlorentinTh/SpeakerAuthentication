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
public class User implements BaseSQL {

    public static final String TABLE = "User";
    public static final String COL_ID = "id", COL_USERNAME = "username", COL_GENDER = "gender",
            COL_PASSWORD = "password";

    private int id, gender;
    private String username, password;

    public User() {}

    public User(String username, int gender, String password) {
        this.username = username;
        this.gender = gender;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String createTable() {
        return "CREATE TABLE " + TABLE + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                COL_GENDER + " TEXT, " +
                COL_PASSWORD + " TEXT);";
    }

    @Override
    public String deleteTable() {
        return "DROP TABLE IF EXISTS " + TABLE;
    }
}
