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

import java.util.ArrayList;

/**
 * Counts steps provided by StepDetector and passes the current
 * step count to the activity.
 */
public class StepDisplayer implements StepListener {

    private int mWalkCount = 0;
    private int mRunCount = 0;
    private int mRideCount = 0;
    PedometerSettings mSettings;

    public StepDisplayer(PedometerSettings settings) {
        mSettings = settings;
        notifyListener(StepDetector.WALK);
        notifyListener(StepDetector.RUN);
        notifyListener(StepDetector.RIDE);
    }

    public void setWalkSteps(int steps) {
        mWalkCount = steps;
        notifyListener(StepDetector.WALK);
    }
    public void setRunSteps(int steps){
        mRunCount = steps;
        notifyListener(StepDetector.RUN);
    }
    public void setRidePedals(int steps){
        mRideCount = steps;
        notifyListener(StepDetector.RIDE);
    }
    public void onStep(final int flag) {
        switch (flag){
            case StepDetector.WALK:
                mWalkCount ++;
                break;
            case StepDetector.RUN:
                mRunCount++;
                break;
            case StepDetector.RIDE:
                mRideCount++;
                break;
        }
        notifyListener(flag);
    }
    public void reloadSettings() {
        notifyListener(StepDetector.WALK);
        notifyListener(StepDetector.RUN);
        notifyListener(StepDetector.RIDE);
    }
    public void passValue() {
    }


    //-----------------------------------------------------
    // Listener
    
    public interface Listener {
        public void stepsChanged(int value,int flag);
        public void passValue(int value,int flag);
    }
    private ArrayList<Listener> mListeners = new ArrayList<Listener>();

    public void addListener(Listener l) {
        mListeners.add(l);
    }
    public void notifyListener(final int flag) {
        switch (flag){
            case StepDetector.WALK:
                for (Listener listener : mListeners) {
                    listener.stepsChanged((int)mWalkCount,flag);
                }
                break;
            case StepDetector.RUN:
                for(Listener listener : mListeners)
                    listener.stepsChanged((int)mRunCount,flag);
                break;
            case StepDetector.RIDE:
                for(Listener listener : mListeners)
                    listener.stepsChanged((int)mRideCount,flag);
                break;
        }
    }
    
    //-----------------------------------------------------
    // Speaking
    public int getMWalkCount(){
        return mWalkCount;
    }
    public int getMRunCount(){
        return mRunCount;
    }
    public int getmRideCount(){return mRideCount;}
    
}
