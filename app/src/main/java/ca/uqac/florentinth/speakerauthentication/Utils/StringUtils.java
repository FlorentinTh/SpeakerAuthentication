package ca.uqac.florentinth.speakerauthentication.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public abstract class StringUtils {

    public static boolean isAlNum(String s) {
        return (Pattern.compile("^[a-zA-Z0-9]*$").matcher(s).find()) ? true : false;
    }

    public static boolean isNumeric(String s) {
        return (Pattern.compile("^[0-9]*$").matcher(s).find()) ? true : false;
    }

    public static String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddHH");
        return simpleDateFormat.format(calendar.getTime());
    }
}
