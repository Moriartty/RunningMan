package com.moriarty.user.runningman.Adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Service.GroupSettingService;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by user on 16-8-19.
 */
public class Category_TopListAdapter extends RecyclerView.Adapter<Category_TopListAdapter.MyViewHolder>  {
    private Context mcontext;
    private LayoutInflater myLayoutInflater;
    private String[] mTitles;
    GroupSettingService groupSettingService;
    RecyclerView groupDeleteView,groupUniteView;  //暂时保留
    private Category_DialogListAdapter myAdapter;
    public static ArrayList<String> allGroupName=new ArrayList<>();
    public static ArrayList<String> choose=new ArrayList<>();    //记录选择删除或选择合并的分组集合
    int resource[] = new int[]{R.drawable.groupsetting_addgroup, R.drawable.groupsetting_deletegroup, R.drawable.groupsetting_unitegroup};

    public Category_TopListAdapter(Context context) {
        mcontext = context;
        mTitles = context.getResources().getStringArray(R.array.groupsetting);
        myLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(myLayoutInflater.inflate(R.layout.item_groupsetting, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.ib.setBackground(mcontext.getResources().getDrawable(resource[position]));
        holder.tv.setText(mTitles[position].toString());
    }

    @Override
    public int getItemCount() {
        return mTitles == null ? 0 : mTitles.length;
    }

    private ServiceConnection connection_add=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Boolean returnValue;
            groupSettingService=((GroupSettingService.GroupSettingBinder)service).getService();
            returnValue=groupSettingService.addGroupIntoSQLite();
            mcontext.unbindService(connection_add);
            if(returnValue)
                Toast.makeText(mcontext,mcontext.getString(R.string.addgroup_success),Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private ServiceConnection connection_delete=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Boolean returnValue;
            groupSettingService=((GroupSettingService.GroupSettingBinder)service).getService();
            returnValue=groupSettingService.deleteGroupFromSQLite();
            mcontext.unbindService(connection_delete);
            choose.clear();   //因为choose和allgroupName改为静态变量，所以每次执行完都要清空
            allGroupName.clear();
            if(returnValue)
                Toast.makeText(mcontext,mcontext.getString(R.string.deletegroup_success),Toast.LENGTH_SHORT).show();   //Toast的显示要show()呀！！！
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            choose.clear();
            allGroupName.clear();
        }
    };
    private ServiceConnection connection_unite=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Boolean returnValue;
            groupSettingService=((GroupSettingService.GroupSettingBinder)service).getService();
            returnValue=groupSettingService.uniteGroupFromSQLite();
            mcontext.unbindService(connection_unite);
            choose.clear();
            allGroupName.clear();
            if(returnValue)
                Toast.makeText(mcontext,mcontext.getString(R.string.unitegroup_success),Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            choose.clear();
            allGroupName.clear();
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.groupsetting_button)
        ImageButton ib;
        @Bind(R.id.groupsetting_text)
        TextView tv;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.groupsetting_button)
        public void OnClick() {
            switch(getPosition()){
                case 0:
                    View v0 = myLayoutInflater.inflate(R.layout.dialog_addgroup, null);
                    final EditText editText=(EditText)v0.findViewById(R.id.addgroup_edittext);
                    new AlertDialog.Builder(mcontext).setIcon(R.drawable.groupsetting_addgroup).setTitle(mcontext.getString(R.string.new_group)).setView(v0)
                            .setPositiveButton(mcontext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(!editText.getText().toString().equals("")){
                                        Intent addGroupIntent=new Intent();
                                        addGroupIntent.putExtra("groupsetting_flag",0);
                                        addGroupIntent.putExtra("newGroupName",editText.getText().toString());
                                        addGroupIntent.setAction("com.moriarty.service.GroupSettingService");
                                        addGroupIntent.setPackage(mcontext.getPackageName());
                                        mcontext.bindService(addGroupIntent, connection_add,mcontext.BIND_AUTO_CREATE);
                                    }
                                }
                            })
                            .setNegativeButton(mcontext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create().show();break;
                case 1:
                    View v1 = myLayoutInflater.inflate(R.layout.groupdelete_recyclerview, null);
                    groupDeleteView = (RecyclerView) v1.findViewById(R.id.recyclerview_deletegroup);
                    groupDeleteView.setLayoutManager(new GridLayoutManager(v1.getContext(),2));
                    groupDeleteView.setAdapter(myAdapter = new Category_DialogListAdapter(mcontext));
                    new AlertDialog.Builder(mcontext).setIcon(R.drawable.groupsetting_deletegroup).setTitle(mcontext.getString(R.string.delete_group)).setView(v1)
                            .setPositiveButton(mcontext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent deleteGroupIntent=new Intent();
                                    deleteGroupIntent.putExtra("groupsetting_flag",1);
                                    deleteGroupIntent.putStringArrayListExtra("deleteIntent",choose);
                                    deleteGroupIntent.setAction("com.moriarty.service.GroupSettingService");
                                    deleteGroupIntent.setPackage(mcontext.getPackageName());
                                    mcontext.bindService(deleteGroupIntent,connection_delete,mcontext.BIND_AUTO_CREATE);
                                }
                            })
                            .setNegativeButton(mcontext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    choose.clear();
                                    allGroupName.clear();
                                }
                            })
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    choose.clear();
                                    allGroupName.clear();
                                }
                            }).create().show();break;
                case 2:
                    View v2 = myLayoutInflater.inflate(R.layout.groupunite_recyclerview, null);
                    final EditText unite_edit=(EditText)v2.findViewById(R.id.unite_edit);
                    groupUniteView = (RecyclerView) v2.findViewById(R.id.recyclerview_unitegroup);
                    groupUniteView.setLayoutManager(new GridLayoutManager(v2.getContext(),2));
                    groupUniteView.setAdapter(myAdapter = new Category_DialogListAdapter(mcontext));
                    new AlertDialog.Builder(mcontext).setIcon(R.drawable.groupsetting_unitegroup).setTitle(mcontext.getString(R.string.unite_group)).setView(v2)
                            .setPositiveButton(mcontext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(choose.size()!=0){    //当有选择要合并的分组时才进行合并
                                        String newGroupNameTemp;
                                        if(unite_edit.getText().toString().equals(""))
                                            newGroupNameTemp=mcontext.getString(R.string.default_group);
                                        else
                                            newGroupNameTemp=unite_edit.getText().toString();
                                        Intent uniteGroupIntent=new Intent();
                                        uniteGroupIntent.putExtra("groupsetting_flag",2);
                                        uniteGroupIntent.putStringArrayListExtra("uniteIntent",choose);
                                        uniteGroupIntent.putExtra("uniteIntent_Edit",newGroupNameTemp);
                                        uniteGroupIntent.setAction("com.moriarty.service.GroupSettingService");
                                        uniteGroupIntent.setPackage(mcontext.getPackageName());
                                        mcontext.bindService(uniteGroupIntent,connection_unite,mcontext.BIND_AUTO_CREATE);
                                    }
                                    else
                                        allGroupName.clear();
                                }
                            })
                            .setNegativeButton(mcontext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    choose.clear();
                                    allGroupName.clear();
                                }
                            })
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    choose.clear();
                                    allGroupName.clear();
                                }
                            }).create().show();break;
            }
        }
    }
}
