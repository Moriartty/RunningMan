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
 * Calculates and displays pace (steps / minute), handles input of desired pace,
 * notifies user if he/she has to go faster or slower.  
 * @author Levente Bagi
 */
public class PaceNotifier implements StepListener{

    public interface Listener {
        public void paceChanged(int value);
        public void passValue();
    }
    private ArrayList<Listener> mListeners = new ArrayList<Listener>();

    private long mLastStepTime = 0;
    private long[] mLastStepDeltas = {-1, -1, -1, -1};
    private int mLastStepDeltasIndex = 0;
    private long mWalkPace = 0;
    
    PedometerSettings mSettings;

    /** Desired pace, adjusted by the user */
    int mDesiredPace;

    /** When did the TTS speak last time */
    private long mSpokenAt = 0;

    public PaceNotifier(PedometerSettings settings) {
        mSettings = settings;
        mDesiredPace = mSettings.getDesiredPace();
        reloadSettings();
    }
    public void setPace(int pace) {
        mWalkPace = pace;
        int avg = (int)(60*1000.0 / mWalkPace);
        for (int i = 0; i < mLastStepDeltas.length; i++) {
            mLastStepDeltas[i] = avg;
        }
        notifyListener();
    }
    public void reloadSettings() {
        notifyListener();
    }
    
    public void addListener(Listener l) {
        mListeners.add(l);
    }

    public void setDesiredPace(int desiredPace) {
        mDesiredPace = desiredPace;
    }

    public void onStep(final int flag) {
        long thisStepTime = System.currentTimeMillis();
        
        // Calculate pace based on last x steps
        if (mLastStepTime > 0) {
            long delta = thisStepTime - mLastStepTime;
            
            mLastStepDeltas[mLastStepDeltasIndex] = delta;
            mLastStepDeltasIndex = (mLastStepDeltasIndex + 1) % mLastStepDeltas.length;
            
            long sum = 0;
            boolean isMeaningfull = true;
            for (int i = 0; i < mLastStepDeltas.length; i++) {
                if (mLastStepDeltas[i] < 0) {
                    isMeaningfull = false;
                    break;
                }
                sum += mLastStepDeltas[i];
            }
            if (isMeaningfull && sum > 0) {
                long avg = sum / mLastStepDeltas.length;
                if(avg!=0)
                    mWalkPace = 60*1000 / avg;
            }
            else {
                mWalkPace = -1;
            }
        }
        mLastStepTime = thisStepTime;
        notifyListener();
    }
    
    private void notifyListener() {
        for (Listener listener : mListeners) {
            listener.paceChanged((int)mWalkPace);
        }
    }
    
    public void passValue() {
        // Not used
    }

    //-----------------------------------------------------
    // Speaking


}
