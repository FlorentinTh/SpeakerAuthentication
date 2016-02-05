package ca.uqac.florentinth.speakerauthentication.Database.Models;

/**
 * Created by FlorentinTh on 1/21/2016.
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
