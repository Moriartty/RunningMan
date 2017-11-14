package com.moriarty.user.runningman.User_Defind.Fuck;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.Object.MovementData;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Service.MDataManageService;
import com.moriarty.user.runningman.User_Defind.MLineView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by user on 17-8-28.
 */
public class Fragment2 extends BaseFragment {
    View view;
    ExtendedLineView mLineView;
    Context context;
    MDataManageService MDService;
    ArrayList<MovementData> movementDatas;
    int dateType;
    int dataType;
    @Override
    public View initView(LayoutInflater inflater) {
        if(view==null){
            view=inflater.inflate(R.layout.fragment2,null);
        }
        context = getActivity();
        dateType=getArguments().getInt(getString(R.string.datetype),0);
        dataType=getArguments().getInt(getString(R.string.datatype),0);
        mLineView=(ExtendedLineView) view.findViewById(R.id.fragment2_mLineView);
        bindMDataService();
        return view;
    }
    public static Fragment2 newInstance(Bundle args) {
        Fragment2 f = new Fragment2();
        f.setArguments(args);
        return f;
    }
    @Override
    public void onPause(){
        Log.d(MainActivity.TAG,"Fragment2 is paused");
        super.onPause();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(MainActivity.TAG,"Fragment2 is destroyed");
    }
    public void bindMDataService(){
        context.bindService(new Intent(context, MDataManageService.class),
                queryConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }
    public void unbindMDataService(){
        context.unbindService(queryConnection);
    }
    private ServiceConnection queryConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(MainActivity.TAG,"fragment2 bind MDataManagerService");
            MDService = ((MDataManageService.MDataManageBinder)service).getService();
            movementDatas=MDService.getMovementDataObject(dateType,dataType);
            if(movementDatas!=null){
                Collections.reverse(movementDatas);
                mLineView.setData(movementDatas,dateType,dataType);
            }
            unbindMDataService();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
