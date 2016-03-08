package ca.uqac.florentinth.speakerauthentication.Config;

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
