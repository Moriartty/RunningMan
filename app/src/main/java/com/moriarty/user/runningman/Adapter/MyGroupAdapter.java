package com.moriarty.user.runningman.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.Activity.Person_InfoCard;
import com.moriarty.user.runningman.Fragment.MyGroupFragment;
import com.moriarty.user.runningman.Others.HandleContact;
import com.moriarty.user.runningman.Others.PopupMenuManager;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Thread.AsyncImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 17-8-11.
 */
public class MyGroupAdapter extends BaseExpandableListAdapter {
    private Context context;
    public ArrayList<String> groupName = new ArrayList<>();
    public ArrayList<ArrayList<String>> groupContent = new ArrayList<ArrayList<String>>();
    public ArrayList<String> collectInfo = new ArrayList<>();
    public static HashMap<String,String> headPortraits=new HashMap<>();
    private AsyncImageLoader asyncImageLoader;
    PopupMenuManager popupMenuManager;
    HandleContact handleContact;
    LayoutInflater layoutInflater;
   // ArrayList<Boolean> group_CB;
    //ArrayList<ArrayList<Boolean>> child_CB;
    private String currentType;

    public MyGroupAdapter(Context context, ArrayList<String> groupName, ArrayList<ArrayList<String>> groupContent
            , ArrayList<String> collectInfo, HashMap<String,String> headPortraits ,String currentType){
        this.context=context;
        this.groupName=groupName;
        this.groupContent=groupContent;
        this.collectInfo=collectInfo;
        this.headPortraits=headPortraits;
       // this.group_CB=group_CB;
       // this.child_CB=child_CB;
        this.currentType=currentType;
        asyncImageLoader=new AsyncImageLoader(context);
        popupMenuManager = new PopupMenuManager(context);
        handleContact=new HandleContact(context);
        layoutInflater = LayoutInflater.from(context);
    }

    private TextView getTextView(int paddingvalue) {
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
        TextView textview = new TextView(context);
        textview.setLayoutParams(lp);
        textview.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        textview.setPadding(paddingvalue, 0, 0, 0);
        textview.setTextSize(15);
        return textview;
    }

    @Override
    public int getGroupCount() {
        return groupName.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groupContent.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupName.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupContent.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(currentType.equals(context.getString(R.string.source_contactslistfragment))){
            if(groupPosition==0){
                LinearLayout linearLayout=new LinearLayout(context);
                TextView textView1=new TextView(context);
                textView1.setText(getGroup(groupPosition).toString());
                textView1.setTextSize(17);
                textView1.setTextColor(context.getResources().getColor(R.color.black));
                textView1.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                linearLayout.addView(textView1);
                convertView=linearLayout;
            }
            else {
                convertView=layoutInflater.inflate(R.layout.item_category_parent,null);
                TextView textView=(TextView)convertView.findViewById(R.id.category_parent_text);
                TextView showNum=(TextView)convertView.findViewById(R.id.category_parent_shownum);
                textView.setText(getGroup(groupPosition).toString());
                showNum.setText(String.valueOf((groupContent.get(groupPosition)).size()));
                showNum.setVisibility(View.VISIBLE);
            }
        }
        else if(currentType.equals(context.getString(R.string.source_choosecontacts))){
            convertView=layoutInflater.inflate(R.layout.item_category_parent,null);
            TextView textView=(TextView)convertView.findViewById(R.id.category_parent_text);
            //TextView showNum=(TextView)convertView.findViewById(R.id.category_parent_shownum);
            final CheckBox group_checkbox=(CheckBox)convertView.findViewById(R.id.group_checkbox);
            textView.setText(getGroup(groupPosition).toString());
            group_checkbox.setVisibility(View.VISIBLE);
            //showNum.setText(String.valueOf((groupContent.get(groupPosition)).size()));
            group_checkbox.setChecked(MyGroupFragment.group_CB.get(groupPosition));

            group_checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean afterStatus=group_checkbox.isChecked();
                    MyGroupFragment.group_CB.set(groupPosition,afterStatus);
                    for(int i=0;i<MyGroupFragment.child_CB.get(groupPosition).size();i++)
                        MyGroupFragment.child_CB.get(groupPosition).set(i,afterStatus);
                    notifyDataSetChanged();
                }
            });
        }

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        convertView = layoutInflater.inflate(R.layout.item_category_child, null);
        final ImageView child_img=(ImageView)convertView.findViewById(R.id.category_child_imageview);
        final TextView textView = (TextView) convertView.findViewById(R.id.category_child_textview);
        final Button itembutton=(Button)convertView.findViewById(R.id.category_chile_itembutton);
        final CheckBox child_checkbox=(CheckBox)convertView.findViewById(R.id.child_checkbox);

        View.OnClickListener myListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.child_checkbox:
                        Boolean isChecked = child_checkbox.isChecked();
                        MyGroupFragment.child_CB.get(groupPosition).set(childPosition,isChecked);
                        boolean isAllTrue=true;
                        for(int i=0;i<MyGroupFragment.child_CB.get(groupPosition).size();i++)
                            isAllTrue=isAllTrue&&MyGroupFragment.child_CB.get(groupPosition).get(i);
                        MyGroupFragment.group_CB.set(groupPosition,isAllTrue);
                        notifyDataSetChanged();
                        break;
                    case R.id.category_chile_itembutton:
                        popupMenuManager.showpopupMenu(itembutton, textView.getText().toString(),
                                child_img.getTag() == null ? null : child_img.getTag().toString(), layoutInflater,handleContact);
                        break;
                    case R.id.category_child_textview:
                        Intent intent=new Intent(context,Person_InfoCard.class);
                        intent.putExtra("info",textView.getText().toString());
                        intent.putExtra("flag",2);
                        context.startActivity(intent);
                        break;
                }
            }
        };

        if(currentType.equals(context.getString(R.string.source_contactslistfragment))){
            itembutton.setVisibility(View.VISIBLE);
        }
        else if(currentType.equals(context.getString(R.string.source_choosecontacts))){
            child_checkbox.setVisibility(View.VISIBLE);
            child_checkbox.setChecked(MyGroupFragment.child_CB.get(groupPosition).get(childPosition));
        }

        //异步加载的关键步骤！！！
        final String imageUri=headPortraits.get(getChild(groupPosition, childPosition).toString());
        child_img.setTag(imageUri);
        final ImageView imageViewByTag=(ImageView)convertView.findViewWithTag(imageUri);
        Bitmap cachedImage=asyncImageLoader.loadDrawable(imageUri,"small",new AsyncImageLoader.ImageCallback() {
            @Override
            public void imageLoader(Bitmap imageDrawable, String imageUri) {
                if(imageViewByTag!=null&&imageDrawable!=null){
                    imageViewByTag.setImageBitmap(imageDrawable);
                }
            }
        });
        if(cachedImage==null){
            child_img.setImageResource(R.drawable.contact_default);
        }
        else{
            child_img.setImageBitmap(cachedImage);
        }

        child_checkbox.setOnClickListener(myListener);

        //Log.d(MainActivity.TAG,currentTag+groupPosition);
        itembutton.setOnClickListener(myListener);

        textView.setText(getChild(groupPosition, childPosition).toString());
        textView.setOnClickListener(myListener);

        return convertView;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
