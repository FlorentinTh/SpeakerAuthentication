package ca.uqac.florentinth.speakerauthentication.Utils;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public abstract class ConvertUtils {

    public static int millisecToSec(int millis) {
        return millis / 1000;
    }

    public static int millisecToSec(String millis) {
        return Integer.parseInt(millis) / 1000;
    }

    public static int secToMillis(int sec) {
        return sec * 1000;
    }

    public static int secToMillis(String sec) {
        return Integer.parseInt(sec) * 1000;
    }

    public static int kiloHeztToHertz(float kHz) {
        return (int) kHz / 1000;
    }
}
