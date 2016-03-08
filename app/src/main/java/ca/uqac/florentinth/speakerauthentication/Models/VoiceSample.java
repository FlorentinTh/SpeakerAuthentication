package ca.uqac.florentinth.speakerauthentication.Models;

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
public class VoiceSample {

    private String className;
    private double[] voiceFeatures;

    public VoiceSample(String className, double[] voiceFeatures) {
        this.className = className;
        this.voiceFeatures = voiceFeatures;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public double[] getVoiceFeatures() {
        return voiceFeatures;
    }

    public void setVoiceFeatures(double[] voiceFeatures) {
        this.voiceFeatures = voiceFeatures;
    }
}
