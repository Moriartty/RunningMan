package com.moriarty.user.runningman.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.moriarty.user.runningman.Activity.Initiate_Activities;
import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by user on 17-9-14.
 */
public class PeopleReviewAdapter extends RecyclerView.Adapter<PeopleReviewAdapter.MyViewHolder> {
    private LayoutInflater mLayoutInflater;
    Context context;
    public PeopleReviewAdapter(Context context){
        this.context=context;
        mLayoutInflater=LayoutInflater.from(context);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder mViewHolder = new MyViewHolder(mLayoutInflater.inflate(R.layout.item_review_people,parent,false));
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.d(MainActivity.TAG,"selected people has:"+Initiate_Activities.selected.get(position));
        holder.show_name.setText(Initiate_Activities.selected.get(position));
    }

    @Override
    public int getItemCount() {
        return Initiate_Activities.selected==null?0:Initiate_Activities.selected.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.show_name)
        TextView show_name;
        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
