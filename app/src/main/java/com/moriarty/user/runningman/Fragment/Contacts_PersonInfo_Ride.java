package com.moriarty.user.runningman.Fragment;

import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moriarty.user.runningman.R;

/**
 * Created by user on 17-8-14.
 */
public class Contacts_PersonInfo_Ride extends Fragment  {
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ride, null);
        return v;
    }

}
