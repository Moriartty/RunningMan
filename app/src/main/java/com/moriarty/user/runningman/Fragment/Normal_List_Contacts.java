package com.moriarty.user.runningman.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.moriarty.user.runningman.Activity.ChooseContacts;
import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.Adapter.Entirety_ContactsListAdapter;
import com.moriarty.user.runningman.Presenter.MainActivityPresenter;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.SearchContact.CharacterParser;
import com.moriarty.user.runningman.SearchContact.ClearEditText;
import com.moriarty.user.runningman.SearchContact.PinyinComparator;
import com.moriarty.user.runningman.SearchContact.SideBar;
import com.moriarty.user.runningman.SearchContact.SortModel;
import com.moriarty.user.runningman.User_Defind.Fuck.BaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 17-9-14.
 */
public class Normal_List_Contacts extends BaseFragment {
    private View view;
    private static final String currentTag="Contacts_EntiretyFragment:";
    private RecyclerView mRecyclerView;
    Entirety_ContactsListAdapter mAdapter;
    private SideBar sideBar;
    private TextView dialog;
    private ClearEditText mClearEditText;
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;
    private PinyinComparator pinyinComparator;
    Context context;
    public static ArrayList<String> names = new ArrayList<String>();
    public static HashMap<String,String> headPortraits=new HashMap<>();
    public final static String SOURCEFLAG="source";
    private String currentType;
    @Override
    public View initView(LayoutInflater inflater) {

        context=getActivity();
        names.clear();
        headPortraits.clear();
        currentType=getArguments().getString(SOURCEFLAG);
        if(currentType.equals(getString(R.string.source_contactslistfragment))){
            names.addAll(ContactsListFragment.names);
            headPortraits.putAll(ContactsListFragment.headPortraits);
        }
        else if(currentType.equals(getString(R.string.source_choosecontacts))){
            names.addAll(ChooseContacts.names);
            headPortraits.putAll(ChooseContacts.headPortraits);
        }

        if (view == null) {
            view = View.inflate(getActivity(), R.layout.choose_contacts_normal, null);
        }
        initViews();
        return view;
    }

    public static Normal_List_Contacts newInstance(String text){
        Normal_List_Contacts fragment = new Normal_List_Contacts();
        Bundle bundle = new Bundle();
        bundle.putString(SOURCEFLAG, text);
        //fragment保存参数，传入一个Bundle对象
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initViews() {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar)view.findViewById(R.id.sidrbar);
        dialog = (TextView)view.findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position=mAdapter.getPositionForSection(s.charAt(0));
                if(position!=-1){
                    mRecyclerView.smoothScrollToPosition(position);   //应该是保持不动
                }
            }
        });

        SourceDateList = filledData(names);   //获取到源数据，其中包含名字与其首字母
        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.id_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter = new Entirety_ContactsListAdapter(context,SourceDateList,mRecyclerView,currentType));

        mClearEditText = (ClearEditText)view.findViewById(R.id.filter_edit);
        mClearEditText.setHint(getString(R.string.searchedithintfirst)+names.size()+getString(R.string.searchedithintlast));
        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                selectDataDisplay(filterData(s.toString()));
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 为ListView填充数据
     * @param date
     * @return
     */
    private List<SortModel> filledData(ArrayList<String> date){
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for(int i=0; i<date.size(); i++){
            SortModel sortModel = new SortModel();
            sortModel.setName(date.get(i));
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(date.get(i));
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                sortModel.setSortLetters(sortString.toUpperCase());
            }else{
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private List<SortModel> filterData(String filterStr){
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if(TextUtils.isEmpty(filterStr)){    //如果为空，则显示所有联系人
            filterDateList = SourceDateList;
        }else{
            filterDateList.clear();
            for(SortModel sortModel : SourceDateList){
                String name = sortModel.getName();
                /*
                **这一块有很大改进空间，更多的搜索算法和排序算法可以补充在这里
                 */
                if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
                    filterDateList.add(sortModel);
                }
            }
        }
        return filterDateList;
    }
    public void selectDataDisplay(List<SortModel> filterDataList){
        // 根据a-z进行排序
        Collections.sort(filterDataList, pinyinComparator);
        mAdapter.updateListView(filterDataList);
    }

    @Override
    public void onStart(){
        super.onStart();
    }
}
