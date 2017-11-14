package com.moriarty.user.runningman.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.Fragment.ContactsListFragment;
import com.moriarty.user.runningman.Fragment.MainPageFragment;
import com.moriarty.user.runningman.Fragment.SportsPartyFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 17-9-19.
 */
public class FragmentAdapter extends FragmentStatePagerAdapter {
    String[] address;
    List<Fragment> fragmentList;
    FragmentManager fm;
    public FragmentAdapter(FragmentManager fm,String[] address ,List<Fragment> fragmentList)
    {
        super(fm);
        this.address=address;
        this.fm=fm;
        this.fragmentList=fragmentList;
    }

    public void setter(){
        fm.beginTransaction().remove(fragmentList.get(1)).commitAllowingStateLoss();
        fragmentList.set(1,new ContactsListFragment());
        notifyDataSetChanged();
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
