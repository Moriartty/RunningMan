package com.moriarty.user.runningman.Interface;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by user on 17-8-11.
 */
public interface IAddContactsPresenter {
    void addGroupAction(View v);
    void selectGroupAction(Button selectGroupType, ArrayList<String> allGroupName);
    void confirmOthers();
    void confirmMyself();
    void modelChooser(Intent intent);
    void selectSexyAction(Button selectSexyType);
    void selectDateAction();
}
