package com.moriarty.user.runningman.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.moriarty.user.runningman.Fragment.Contacts_PersonInfo_Ride;
import com.moriarty.user.runningman.Fragment.Contacts_PersonInfo_Run;
import com.moriarty.user.runningman.Fragment.Contacts_PersonInfo_Walk;

/**
 * Created by user on 17-8-14.
 */
public class PagerAdapter_SM extends FragmentStatePagerAdapter {
    public PagerAdapter_SM(FragmentManager fm, String tel)
    {
        super(fm);
    }
    @Override
    public Fragment getItem(int position)
    {
        switch(position){
            case 0:return new Contacts_PersonInfo_Walk();
            case 1:return new Contacts_PersonInfo_Run();
            default:return new Contacts_PersonInfo_Ride();
        }
    }
    @Override
    public int getCount()
    {
        return 3;
    }
}
