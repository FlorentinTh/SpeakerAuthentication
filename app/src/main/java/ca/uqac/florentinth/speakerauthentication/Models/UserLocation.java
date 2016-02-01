package ca.uqac.florentinth.speakerauthentication.Models;

/**
 * Created by FlorentinTh on 1/21/2016.
 */
public class UserLocation implements BaseSQL {

    public static final String TABLE = "UserLocation";
    public static final String COL_ID_USER = "id_user", COL_ID_LOCATION = "id_location";

    private int idUser, idLocation;

    public UserLocation() {}

    public UserLocation(int idUser, int idLocation) {
        this.idUser = idUser;
        this.idLocation = idLocation;
    }

    public int getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(int idLocation) {
        this.idLocation = idLocation;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    @Override
    public String createTable() {
        return "CREATE TABLE " + TABLE + "(" +
                COL_ID_USER + " INTEGER, " +
                COL_ID_LOCATION + " INTEGER, " +
                "PRIMARY KEY (" + COL_ID_USER + ", " + COL_ID_LOCATION + "), " +
                "FOREIGN KEY (" + COL_ID_USER + ") REFERENCES User(" + User.COL_ID + "), " +
                "FOREIGN KEY (" + COL_ID_LOCATION + ") REFERENCES Location(" + Location.COL_ID +
                "));";
    }

    @Override
    public String deleteTable() {
        return "DROP TABLE IF EXISTS " + TABLE;
    }
}
