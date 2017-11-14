package com.moriarty.user.runningman.User_Defind.Fuck;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.R;

import java.util.List;


/**
 * Created by user on 17-8-28.
 */
public class Selector_Adapter extends FragmentStatePagerAdapter{
    String[] address;
    List<Fragment> fragmentList;
    public Selector_Adapter(FragmentManager fm,String[] address ,List<Fragment> fragmentList)
    {
        super(fm);
        this.address=address;
        this.fragmentList=fragmentList;
    }
    @Override
    public Fragment getItem(int position)
    {
        return fragmentList.get(position);
    }
    @Override
    public int getCount(){
        return MainActivity.addresses.length;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return address[position];
    }

}
