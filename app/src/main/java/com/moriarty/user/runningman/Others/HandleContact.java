package com.moriarty.user.runningman.Others;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.Adapter.Contacts_MoveListAdapter;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Service.AddContactsService;
import com.moriarty.user.runningman.Service.DeleteContactsService;


/**
 * Created by user on 16-11-28.
 */
public class HandleContact {
    private static final String currentTag="HandleContact:";
    Context mcontext;
    DeleteContactsService deleteContactsService;

    public static AlertDialog alertDialog;
    public HandleContact(Context context){
        this.mcontext=context;
    }
    //下面的方法是修改联系人的collect状态，
    //但被collect的联系人并不需要从原分组中移出，collect只是为联系人新增加一个属性
    public void collectOrNot(String name,boolean isCollected){
        int flag;
        if(isCollected)
            flag=0;
        else
            flag=1;
        Log.d(MainActivity.TAG,currentTag+":"+name+" "+isCollected);
        AddContactsService.update_CollectedInfo(name,flag,mcontext.getContentResolver(),mcontext);
    }
    public void deleteContact(final String name){     //删除联系人
        Log.d(MainActivity.TAG,currentTag+"delete "+name);
        AlertDialog.Builder builder=new AlertDialog.Builder(mcontext)
                .setIcon(mcontext.getResources().getDrawable(R.drawable.ic_dialog_alert_holo_light))
                //当要删除联系人时，弹出警告框，显示setMessage中的内容
                .setMessage(mcontext.getString(R.string.alert_deletecontactfirst)+name+mcontext.getString(R.string.alert_deletecontactlast))
                .setTitle(mcontext.getString(R.string.alert_deletecontacttitle));
        builder.setPositiveButton(mcontext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle bundle=new Bundle();
                bundle.putString("name",name);
                Intent deleteIntent=new Intent();
                deleteIntent.putExtras(bundle);
                deleteIntent.setAction("com.moriarty.service.DELETECONTACTSSERVICE");
                deleteIntent.setPackage(mcontext.getPackageName());
                mcontext.bindService(deleteIntent,Dconnection, Context.BIND_AUTO_CREATE);
            }
        });
        builder.setNegativeButton(mcontext.getResources().getString(R.string.cancel),null).create().show();
        builder.setOnCancelListener(null);
    }
    public void moveContact(LayoutInflater layoutInflater,String name){  //移动联系人分组
        RecyclerView selectGroupView;
        Contacts_MoveListAdapter myAdapter=new Contacts_MoveListAdapter(mcontext,name);
        View v3 = layoutInflater.inflate(R.layout.movecontact2other, null);
        selectGroupView = (RecyclerView) v3.findViewById(R.id.recyclerview_selectgroup);
        selectGroupView.setLayoutManager(new GridLayoutManager(mcontext,2));
        selectGroupView.setAdapter(myAdapter);
        AlertDialog.Builder builder=new AlertDialog.Builder(mcontext).setIcon(R.drawable.contact_move)
                .setTitle(mcontext.getResources().getString(R.string.moveto)).setView(v3);
        alertDialog=builder.create();
        alertDialog.show();
    }
    public void showContact(String name,String imageUrl){   //显示联系人二维码
        //ShareQRCode.showQRCode(mcontext,name,imageUrl);
    }

    private ServiceConnection Dconnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            deleteContactsService=((DeleteContactsService.DeleteContactsBinder)service).getService();
            Boolean isDelete=deleteContactsService.deleteContactsInSQLite();
            mcontext.unbindService(Dconnection);   //操作完后无论如何都要解除绑定
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
