package ca.uqac.florentinth.speakerauthentication.Config;

/**
 * Created by FlorentinTh on 1/25/2016.
 */
public class Location {
    private static Location ourInstance = new Location();
    private final int minDistanceFromCenter = 100; //meters
    private final int maxDistanceFromCenter = 500; //meters

    public static Location getInstance() {
        return ourInstance;
    }

    public int getMinDistanceFromCenter() {
        return minDistanceFromCenter;
    }

    public int getMaxDistanceFromCenter() {
        return maxDistanceFromCenter;
    }
}
