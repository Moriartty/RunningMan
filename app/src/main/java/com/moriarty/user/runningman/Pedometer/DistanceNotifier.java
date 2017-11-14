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


import android.util.Log;

import com.moriarty.user.runningman.Activity.MainActivity;

/**
 * Calculates and displays the distance walked.  
 * @author Levente Bagi
 */
public class DistanceNotifier implements StepListener{

    public interface Listener {
        public void valueChanged(float value ,int flag);
        public void passValue(float value,int flag);
    }
    private Listener mListener;
    
    float mWalkDistance = 0;
    float mRunDistance = 0;
    float mRideDistance = 0;
    
    PedometerSettings mSettings;
    
    boolean mIsMetric;
    float mWalkStepLength;
    float mRunStepLength;
    float mRidePedalLength;

    public DistanceNotifier(Listener listener, PedometerSettings settings) {
        mListener = listener;
        mSettings = settings;
        reloadSettings();
    }
    public void setWalkDistance(float distance) {
        mWalkDistance = distance;
        notifyListener(StepDetector.WALK);
    }
    public void setRunDistance(float distance){
        mRunDistance = distance;
        notifyListener(StepDetector.RUN);
    }
    public void setRideDistance(float distance){
        mRideDistance = distance;
        notifyListener(StepDetector.RIDE);
    }
    
    public void reloadSettings() {
        mIsMetric = mSettings.isMetric();
        mWalkStepLength = mSettings.getWalkStepLength();
        mRunStepLength = mSettings.getRunStepLength();
        mRidePedalLength = mSettings.getRidePadelLength();
        notifyListener(StepDetector.WALK);
        notifyListener(StepDetector.RUN);
        notifyListener(StepDetector.RIDE);
    }
    
    public void onStep(final int flag) {
        switch (flag){
            case StepDetector.WALK:
                //米为单位
                mWalkDistance += (float)(// meter
                        mWalkStepLength // centimeters
                                / 100.0); // centimeters/meter
               // Log.d(MainActivity.TAG,mWalkDistance+"");
                break;
            case StepDetector.RUN:
                mRunDistance += (float)(// meter
                        mRunStepLength // centimeters
                                / 100.0); // centimeters/meter
                break;
            case StepDetector.RIDE:
                mRideDistance += (float)(// meter
                        mRidePedalLength // centimeters
                                / 100.0); // centimeters/meter
                break;
        }
        notifyListener(flag);
    }
    
    private void notifyListener(final int flag) {
        switch (flag){
            case StepDetector.WALK:
                mListener.valueChanged(mWalkDistance,flag);
                break;
            case StepDetector.RUN:
                mListener.valueChanged(mRunDistance,flag);
                break;
            case StepDetector.RIDE:
                mListener.valueChanged(mRideDistance,flag);
                break;
        }

    }
    
    public void passValue() {
        // Callback of StepListener - Not implemented
    }
    

}

