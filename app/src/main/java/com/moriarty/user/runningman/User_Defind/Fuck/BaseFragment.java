package com.moriarty.user.runningman.User_Defind.Fuck;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by user on 17-8-28.
 */
public abstract class BaseFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // mActivity = (MainActivity) getActivity();
        View view = initView(inflater);
        return view;
    }

    public abstract View initView(LayoutInflater inflater);

}
