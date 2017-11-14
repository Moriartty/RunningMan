package com.moriarty.user.runningman.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moriarty.user.runningman.Fragment.Normal_List_Contacts;
import com.moriarty.user.runningman.Object.ActivityData;
import com.moriarty.user.runningman.Others.ZoomBitmap;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.SearchContact.SortModel;
import com.moriarty.user.runningman.Thread.AsyncImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by user on 17-9-18.
 */
public class ActivitiesGalleryAdapter extends RecyclerView.Adapter<ActivitiesGalleryAdapter.MyViewHolder> {
    LayoutInflater myLayoutInflater;
    ArrayList<ActivityData> list;
    String imageUri;
    AsyncImageLoader asyncImageLoader;
    RecyclerView recyclerView;
    Context context;
    public ActivitiesGalleryAdapter(Context context,RecyclerView recyclerView,ArrayList<ActivityData> list){
        this.context=context;
        this.myLayoutInflater=LayoutInflater.from(context);
        this.recyclerView=recyclerView;
        this.list=list;
        asyncImageLoader=new AsyncImageLoader(context);
    }
    public void updateListView(ArrayList<ActivityData> list){
        this.list = list;
        notifyDataSetChanged();    //用list中的数据更新recyclerView
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(myLayoutInflater.inflate(R.layout.item_activities_list,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ActivityData data=list.get(position);
        holder.topic.setText(data.getTitle());
        holder.time.setText(data.getTime());
        holder.tel.setText(data.getTel());
        holder.address.setText(data.getAddress());
        holder.content.setText(data.getContent());
        if(position<getItemCount()-1)
            holder.divider.setVisibility(View.VISIBLE);

        imageUri= data.getImg_url();
        holder.pic.setTag(imageUri);
        Bitmap cachedImage=getBitmapByTag(imageUri);
        /*if(cachedImage==null){
            holder.pic.setImageResource(R.drawable.contact_default);
        }
        else{
            holder.pic.setImageBitmap(cachedImage);
        }*/
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.activity_topic)
        TextView topic;
        @Bind(R.id.activity_time)
        TextView time;
        @Bind(R.id.activity_tel)
        TextView tel;
        @Bind(R.id.activity_address)
        TextView address;
        @Bind(R.id.activity_pic)
        ImageView pic;
        @Bind(R.id.activity_content)
        TextView content;
        @Bind(R.id.activity_divider)
        View divider;
        public MyViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public Bitmap getBitmapByTag(String imageUri){
        return asyncImageLoader.loadDrawable(imageUri,"large",new AsyncImageLoader.ImageCallback() {
            @Override
            public void imageLoader(Bitmap imageDrawable, String imageUri) {
                ImageView imageViewByTag=(ImageView)recyclerView.findViewWithTag(imageUri);
                if(imageViewByTag!=null&&imageDrawable!=null){
                    //imageViewByTag.setImageBitmap(null);
                    //imageViewByTag.setImageBitmap(imageDrawable);
                    //imageViewByTag.setBackgroundDrawable(new BitmapDrawable(imageDrawable));
                    imageViewByTag.setBackgroundDrawable(new BitmapDrawable(ZoomBitmap.ImageCrop(imageDrawable,2,1,true)));
                }
            }
        });
    }
}
