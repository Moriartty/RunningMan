package com.moriarty.user.runningman.Adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import com.moriarty.user.runningman.Activity.ChooseContacts;
import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.Activity.Person_InfoCard;
import com.moriarty.user.runningman.Fragment.Normal_List_Contacts;
import com.moriarty.user.runningman.Others.HandleContact;
import com.moriarty.user.runningman.Others.PopupMenuManager;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.SearchContact.SortModel;
import com.moriarty.user.runningman.Thread.AsyncImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by user on 16-8-19.
 */
public class Entirety_ContactsListAdapter extends RecyclerView.Adapter<Entirety_ContactsListAdapter.MyViewHolder> {
    private static final String currentTag="Entirety_ContactsListAdapter:";
    private Context mcontext;
    private LayoutInflater myLayoutInflater;
    private List<SortModel> list = null;
    int[] Colors=new int[]{R.color.gold, R.color.crimson, R.color.greenyellow, R.color.indigo, R.color.black
            ,R.color.sienna, R.color.darkviolet,R.color.cyan};
    private AsyncImageLoader asyncImageLoader;
    private RecyclerView recyclerView;
    String imageUri;   //记录联系人头像图片的路径
    HandleContact handleContact;
    private String currentType;

    public Entirety_ContactsListAdapter(Context context, List<SortModel> list, RecyclerView recyclerView,String currentType){
        Log.d(MainActivity.TAG,currentTag+"EntiretyAdapter is prepared");
        mcontext=context;
        this.list = list;
        Log.d(MainActivity.TAG,currentTag+" list's size is "+list.size());
        myLayoutInflater=LayoutInflater.from(context);
        asyncImageLoader=new AsyncImageLoader(context);
        this.recyclerView=recyclerView;
        handleContact=new HandleContact(mcontext);
        this.currentType=currentType;
    }
    public void updateListView(List<SortModel> list){
        this.list = list;
        notifyDataSetChanged();    //用list中的数据更新recyclerView
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(myLayoutInflater.inflate(R.layout.item_entiretycontacts,parent,false));
        return holder;
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        //异步加载的关键步骤！！！
        imageUri= Normal_List_Contacts.headPortraits.get(this.list.get(position).getName());
        holder.img.setTag(imageUri);
        Bitmap cachedImage=getBitmapByTag(imageUri);
        if(cachedImage==null){
            holder.img.setImageResource(R.drawable.contact_default);
        }
        else{
            holder.img.setImageBitmap(cachedImage);
        }

        int section = getSectionForPosition(position);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(position == getPositionForSection(section)){
            holder.catalog.setVisibility(View.VISIBLE);
            holder.catalog.setText(list.get(position).getSortLetters());
            holder.catalog.setTextColor(mcontext.getResources().getColor(Colors[position%Colors.length]));
        }else{
            holder.catalog.setVisibility(View.GONE);
        }
        holder.tv.setText(this.list.get(position).getName());
        //这里通过判断intent的来源来控制组建的可见性
        if(currentType.equals(mcontext.getString(R.string.source_choosecontacts)))
            holder.checkBox.setVisibility(View.VISIBLE);
        else if(currentType.equals(mcontext.getString(R.string.source_contactslistfragment)))
            holder.delete_contact.setVisibility(View.VISIBLE);
    }

    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    public Bitmap getBitmapByTag(String imageUri){
        return asyncImageLoader.loadDrawable(imageUri,"small",new AsyncImageLoader.ImageCallback() {
            @Override
            public void imageLoader(Bitmap imageDrawable, String imageUri) {
                ImageView imageViewByTag=(ImageView)recyclerView.findViewWithTag(imageUri);
                if(imageViewByTag!=null&&imageDrawable!=null){
                    imageViewByTag.setImageBitmap(imageDrawable);
                }
            }
        });
    }


    // 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
       return list == null ? 0 :list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.contacts_name_in_checkbox)
        TextView tv;
        @Bind(R.id.catalog_in_chooseContacts)
        TextView catalog;
        @Bind(R.id.delete_contact)
        Button delete_contact;
        @Bind(R.id.contacts_img_in_checkbox)
        ImageView img;
        @Bind(R.id.contacts_checkbox)
        CheckBox checkBox;
        public MyViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }
        @OnClick(R.id.contacts_name_in_checkbox)
        public void Click(){
            if(currentType.equals(mcontext.getString(R.string.source_contactslistfragment))){
                Intent intent=new Intent(mcontext, Person_InfoCard.class);
                intent.putExtra("info",tv.getText().toString());
                intent.putExtra("flag",2);
                mcontext.startActivity(intent);
            }
        }
        @OnClick(R.id.contacts_checkbox)
        public void OnClick() {
            if (checkBox.isChecked()) {
                ChooseContacts.selectedFromEntity.add(tv.getText().toString());
            } else if (!checkBox.isChecked()) {
                ChooseContacts.selectedFromEntity.remove(tv.getText().toString());
            }
        }

        @OnClick(R.id.delete_contact)
        public void OnClickbutton(){
            //传递当前需要操作的人名
            PopupMenuManager popupMenuManager=new PopupMenuManager(mcontext);
            popupMenuManager.showpopupMenu(delete_contact,tv.getText().toString(),img.getTag()==null?null:img.getTag().toString(),myLayoutInflater,handleContact);
        }
       /* @OnClick(R.id.quick_shareqrcode)
        public void OnClickImg(){
            Bitmap logo=getBitmapByTag(img.getTag().toString());
            if(logo==null)
                Log.d(MainActivity.TAG,currentTag+"logo is null");
            TwoDimentionCode twoDimentionCode=new TwoDimentionCode();
            Bitmap bitmap=twoDimentionCode.generateTDC("",logo);
            twoDimentionCode.shareImage(bitmap,mcontext);  //传输携带着联系人头像的bitmap
        }*/
    }
}
