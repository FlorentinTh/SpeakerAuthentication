package ca.uqac.florentinth.speakerauthentication.Utils;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public abstract class CryptUtils {

    private static final String HASHING_ALGORITHM = "SHA-256";

    public static String SHA256(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASHING_ALGORITHM);
            byte[] hashedString = messageDigest.digest(str.getBytes("UTF-8"));
            StringBuffer stringBuffer = new StringBuffer();

            for(int i = 0; i < hashedString.length; i++) {
                String tmp = Integer.toHexString(0xff & hashedString[i]);

                if(tmp.length() == 1) { stringBuffer.append('0'); }

                stringBuffer.append(tmp);
            }

            return stringBuffer.toString();

        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
