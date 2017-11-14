package com.moriarty.user.runningman.Others;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.moriarty.user.runningman.Activity.AddContacts;
import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Service.QueryContactsService;

import java.util.ArrayList;

/**
 * Created by user on 16-11-28.
 */
public class PopupMenuManager {
    private static final String currentTag="PopupMenuManager:";
    public Context mcontext;
    public PopupMenu popupMenu;
    public Menu menu;
    public PopupMenuManager(Context context){
        this.mcontext=context;
    }
    public void showpopupMenu(final View view, final String name, final String imageUrl,
                              final LayoutInflater myLayoutInflater,final HandleContact handleContact){
        popupMenu=new PopupMenu(mcontext,view);
        menu=popupMenu.getMenu();
        final boolean isCollected;
        int length;
        //这里无法为item设置icon,内部框架已经设定其visibility为false，需要使用反射机制。
        final String[] menu_details=mcontext.getResources().getStringArray(R.array.contactssetting);
        length=menu_details.length;
        Log.d(MainActivity.TAG,currentTag+"length:"+length);
        for(int i=0;i<length-1;i++){
            menu.add(Menu.NONE,i,i,menu_details[i]);
        }
        if(isCollected=QueryContactsService.isCollected(name))
            menu_details[length-1]=mcontext.getResources().getString(R.string.delete_collect);
        else
            menu_details[length-1]=mcontext.getResources().getString(R.string.collect);
        menu.add(Menu.NONE,length-1,length-1,menu_details[length-1]);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Log.d(MainActivity.TAG,currentTag+item.getTitle());
                // Toast.makeText(mcontext,name+item.getTitle(),Toast.LENGTH_SHORT).show();
                if(item.getTitle().equals(menu_details[0])){   //删除该联系人
                    handleContact.deleteContact(name);
                }
                else if(item.getTitle().equals(menu_details[1])){  //对联系人执行移动／加入分组
                    handleContact.moveContact(myLayoutInflater,name);

                }else if(item.getTitle().equals(menu_details[2])){    //分享该联系人二维码名片
                    //handleContact.showContact(name,imageUrl);
                    handleContact.collectOrNot(name,isCollected);
                }else if(item.getTitle().equals(menu_details[3])){
                    handleContact.collectOrNot(name,isCollected);
                }
                return true;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                //暂时不做操作
            }
        });
        popupMenu.show();
    }

    public void showGroupMenu(View view, final ArrayList<String> allGroupName){
        popupMenu=new PopupMenu(mcontext,view);
        menu=popupMenu.getMenu();
        allGroupName.addAll(QueryContactsService.getAllGroup());    //查询所有组的方法在QueryContactsService中.......
        for(int i=0;i<allGroupName.size();i++){
            menu.add(Menu.NONE,Menu.FIRST+i,i,allGroupName.get(i));
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AddContacts.groupType.setText(item.getTitle().toString());
                return false;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                allGroupName.clear();
            }
        });
        popupMenu.show();
    }
    public void showSexyMenu(View view){
        popupMenu=new PopupMenu(mcontext,view);
        menu=popupMenu.getMenu();
        final String[] arr=mcontext.getResources().getStringArray(R.array.sexy);
        for(int i=0;i<arr.length;i++){
            menu.add(Menu.NONE,Menu.FIRST+i,i,arr[i]);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AddContacts.sexy.setText(item.getTitle().toString());
                return false;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                //arr.length=0;
            }
        });
        popupMenu.show();
    }
}
