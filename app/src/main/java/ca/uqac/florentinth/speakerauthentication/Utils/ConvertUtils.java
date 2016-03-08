package ca.uqac.florentinth.speakerauthentication.Utils;

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
