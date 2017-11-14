package com.moriarty.user.runningman.Interface;

import android.app.Activity;

import com.moriarty.user.runningman.Object.PersonInfo;

import java.util.ArrayList;

/**
 * Created by user on 17-8-10.
 */
public interface AddContactsView {
    void showToast(String s);
    void destroy();
    int getFlag();
    ArrayList<String> getHistory();
    PersonInfo getPersonInfo();
    void iniHistory(PersonInfo personInfo);
    Activity getActivity();
}
