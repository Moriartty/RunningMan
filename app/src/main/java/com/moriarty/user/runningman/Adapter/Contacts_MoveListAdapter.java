package com.moriarty.user.runningman.Adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Service.GroupSettingService;
import com.moriarty.user.runningman.Service.QueryContactsService;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by user on 16-9-1.
 */
public class Contacts_MoveListAdapter extends RecyclerView.Adapter<Contacts_MoveListAdapter.MyViewHolder> {
    private Context mcontext;
    private LayoutInflater myLayoutInflater;
    private String[] mTitles=new String[]{};
    ArrayList<String> allgroupName=new ArrayList<String>();
    private GroupSettingService groupSettingService;
    ArrayList<String> contactPackage=new ArrayList<>();
    private String contactName;


    public Contacts_MoveListAdapter(Context context, String contactName){
        mcontext=context;
        mTitles = context.getResources().getStringArray(R.array.titles);
        allgroupName.addAll(QueryContactsService.getAllGroup());
        myLayoutInflater=LayoutInflater.from(context);
        this.contactName=contactName;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(myLayoutInflater.inflate(R.layout.item_category_selectgroup,parent,false));
        return holder;
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.show.setText(allgroupName.get(position).toString());
    }
    @Override
    public int getItemCount() {
        return allgroupName == null ? 0 :allgroupName .size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.show)
        TextView show;
        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
        @OnClick(R.id.show)
        public void OnClick(){
            contactPackage.add(contactName);
            contactPackage.add(show.getText().toString());
            Intent moveContactIntent=new Intent();
            moveContactIntent.putStringArrayListExtra("choose",contactPackage);
            moveContactIntent.putExtra("groupsetting_flag",3);
            moveContactIntent.setAction("com.moriarty.service.GroupSettingService");
            moveContactIntent.setPackage(mcontext.getPackageName());
            mcontext.bindService(moveContactIntent,connection_Move,mcontext.BIND_AUTO_CREATE);
        }
    }

    private ServiceConnection connection_Move=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            groupSettingService=((GroupSettingService.GroupSettingBinder)service).getService();
            groupSettingService.updateContactAddress();
            mcontext.unbindService(connection_Move);
            contactPackage.clear();
            allgroupName.clear();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            contactPackage.clear();
            allgroupName.clear();
        }
    };
}
