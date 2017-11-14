package com.moriarty.user.runningman.Interface;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.moriarty.user.runningman.Object.PersonInfo;
import com.moriarty.user.runningman.Pedometer.PedometerSettings;

import java.util.ArrayList;

/**
 * Created by user on 17-2-23.
 */
public interface IMainPresenter {
    void inspectPermission(Handler handler);
    void checkMySelfInfo(Handler handler);
    void enduePermission(String[] permissions);
    void skip2AddContactsView(PersonInfo personInfo);
    void skip2PersonInfo(PersonInfo myself);
    void initPedometerSetting();
    PedometerSettings getPedometerSettings();
    boolean getIsRunning();
    void setQuitting(boolean mQuitting);
    void preStartService();
    void startStepService();
    void bindStepService();
    void unbindStepService();
    void stopStepService();
    void mainActivityPaused();
    void resetValues(boolean updateDisplay);

}
