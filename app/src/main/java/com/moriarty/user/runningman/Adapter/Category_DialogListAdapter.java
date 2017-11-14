package com.moriarty.user.runningman.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;


import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Service.QueryContactsService;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by user on 16-8-19.
 */
public class Category_DialogListAdapter extends RecyclerView.Adapter<Category_DialogListAdapter.MyViewHolder> {
    private Context mcontext;
    private LayoutInflater myLayoutInflater;
    private static final String currentTag="Category_DialogListAdapter:";

    public Category_DialogListAdapter(Context context) {
        mcontext = context;
        myLayoutInflater = LayoutInflater.from(context);
        Category_TopListAdapter.allGroupName.addAll(QueryContactsService.getAllGroup());
        Category_TopListAdapter.allGroupName.remove(mcontext.getResources().getString(R.string.default_group));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(myLayoutInflater.inflate(R.layout.item_deletegroup, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if(Category_TopListAdapter.choose.contains(Category_TopListAdapter.allGroupName.get(position).toString())){
            holder.checkBox.setChecked(true);
        }
        else{
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setText(Category_TopListAdapter.allGroupName.get(position).toString());
        Log.d(MainActivity.TAG,currentTag+holder.checkBox.getText().toString());

    }

    @Override
    public int getItemCount() {
        return Category_TopListAdapter.allGroupName == null ? 0 : Category_TopListAdapter.allGroupName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.checkbox)
        CheckBox checkBox;
        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
        @OnClick(R.id.checkbox)
        public void OnClick() {
            if (checkBox.isChecked()) {
                Log.d(MainActivity.TAG,currentTag+"deletegroup"+checkBox.getText().toString()+"is checked");
                Category_TopListAdapter.choose.add(checkBox.getText().toString());
            } else if (!checkBox.isChecked()) {
                Log.d(MainActivity.TAG,currentTag+"deletegroup"+checkBox.getText().toString()+"is unchecked");
                Category_TopListAdapter.choose.remove(checkBox.getText().toString());
            }
        }
    }
}
