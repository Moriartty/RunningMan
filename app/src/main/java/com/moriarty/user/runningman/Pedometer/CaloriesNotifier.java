/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.moriarty.user.runningman.Pedometer;


/**
 * Calculates and displays the approximate calories.  
 * @author Levente Bagi
 */
public class CaloriesNotifier implements StepListener{

    public interface Listener {
        public void valueChanged(float value);
        public void passValue();
    }
    private Listener mListener;
    
    public final static double METRIC_RUNNING_FACTOR = 1.02784823;
    public final static double IMPERIAL_RUNNING_FACTOR = 0.75031498;

    public final static double METRIC_WALKING_FACTOR = 0.708;
    public final static double IMPERIAL_WALKING_FACTOR = 0.517;

    public final static double METRIC_RIDING_FACTOR = 0.65;

    private double mCalories = 0;
    
    PedometerSettings mSettings;
    
    boolean mIsMetric;
    boolean mIsRunning;
    float mWalkStepLength;
    float mRunStepLength;
    float mRidePedalLength;
    float mBodyWeight;

    public CaloriesNotifier(Listener listener, PedometerSettings settings) {
        mListener = listener;
        mSettings = settings;
        reloadSettings();
    }
    public void setCalories(float calories) {
        mCalories = calories;
        notifyListener();
    }
    public void reloadSettings() {
        mIsMetric = mSettings.isMetric();
        mIsRunning = mSettings.isRunning();
        mWalkStepLength = mSettings.getWalkStepLength();
        mRunStepLength = mSettings.getRunStepLength();
        mRidePedalLength = mSettings.getRidePadelLength();
        mBodyWeight = mSettings.getBodyWeight();
        notifyListener();
    }
    public void resetValues() {
        mCalories = 0;
    }
    
    public void isMetric(boolean isMetric) {
        mIsMetric = isMetric;
    }
    public void setStepLength(float stepLength) {
        mWalkStepLength = stepLength;
    }
    
    public void onStep(final int flag) {
        switch (flag){
            case StepDetector.WALK:
                mCalories +=
                        (mBodyWeight * METRIC_WALKING_FACTOR)
                                // Distance:
                                * mWalkStepLength // centimeters
                                / 100000.0; // centimeters/kilometer
                break;
            case StepDetector.RUN:
                mCalories +=
                        (mBodyWeight * METRIC_RUNNING_FACTOR)
                                // Distance:
                                * mRunStepLength // centimeters
                                / 100000.0; // centimeters/kilometer
                break;
            case StepDetector.RIDE:
                //骑自行车的卡路里计算方式需要重新设定算法
                mCalories +=
                        (mBodyWeight * METRIC_RIDING_FACTOR)
                                // Distance:
                                * mRidePedalLength // centimeters
                                / 100000.0; // centimeters/kilometer
                break;
        }
        notifyListener();
    }
    
    private void notifyListener() {
        mListener.valueChanged((float)mCalories);
    }
    
    public void passValue() {
        
    }
    public double getmCalories(){
        return (float)mCalories;
    }
    

}

