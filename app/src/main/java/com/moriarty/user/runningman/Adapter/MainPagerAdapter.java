package com.moriarty.user.runningman.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.moriarty.user.runningman.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by user on 17-8-16.
 */
public class MainPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int colors[];
    Context context;
    public MainPagerAdapter(Context context){
        this.context=context;
        colors=new int[]{context.getResources().getColor(R.color.walk_color),
                context.getResources().getColor(R.color.run_color),
                context.getResources().getColor(R.color.ride_color),
                context.getResources().getColor(R.color.aq_color)
        };
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mainpager,parent,false);
        final RecyclerView.ViewHolder holder=new MyViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder){
            ((MyViewHolder) holder).cardView.setCardBackgroundColor(colors[position]);
            ((MyViewHolder) holder).mptv1.setText("hello bitch");
            ((MyViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"aaa",Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.mptv1)
        TextView mptv1;

        @Bind(R.id.cardview)
        CardView cardView;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
