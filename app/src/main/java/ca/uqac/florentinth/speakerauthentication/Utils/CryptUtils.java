package ca.uqac.florentinth.speakerauthentication.Utils;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
