package ca.uqac.florentinth.speakerauthentication.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

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
