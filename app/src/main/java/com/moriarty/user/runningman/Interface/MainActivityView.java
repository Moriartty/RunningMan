package com.moriarty.user.runningman.Interface;

import android.app.Activity;

/**
 * Created by user on 17-8-9.
 */
public interface MainActivityView {
    void setCurrentItem(int flag);
    Activity getActivity();
    void init();
    void initToolbarAndNav();
}
