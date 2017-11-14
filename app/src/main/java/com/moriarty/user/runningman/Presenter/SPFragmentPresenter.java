package com.moriarty.user.runningman.Presenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.Fragment.SportsPartyFragment;
import com.moriarty.user.runningman.NetworkRequest.APIManager;
import com.moriarty.user.runningman.NetworkRequest.XUtils.Request;
import com.moriarty.user.runningman.Object.ActivityData;
import com.moriarty.user.runningman.Object.ResponseInfoFromNet;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Service.AddActivityService;
import com.moriarty.user.runningman.Utils.JsonUtils;
import com.moriarty.user.runningman.Utils.NetworkUtil;
import com.moriarty.user.runningman.Utils.PreferenceUtil;
import com.moriarty.user.svprogress.SVProgressHUD;

import java.util.ArrayList;

/**
 * Created by user on 17-10-10.
 */
public class SPFragmentPresenter {
    Context context;
    SportsPartyFragment sportsPartyFragment;
    AddActivityService addActivityService;
    private final int WRITE = 0;
    public static final int READ = 1;
    private final int ADDA = 0x453;
    Handler handler;
    ArrayList<ActivityData> list;
    Request request;

    public SPFragmentPresenter(Context context, SportsPartyFragment sportsPartyFragment) {
        this.context = context;
        this.sportsPartyFragment = sportsPartyFragment;
        initHandler();
        sportsPartyFragment.initList(list);
        request = new Request();
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message m) {
                switch (m.what){
                    case Request.EXCEPTION_FLAG:
                        sportsPartyFragment.setRefreshing(false);
                        SVProgressHUD.showInfoWithStatus(context,"网络连接超时");
                        doRefreshFromLocal(READ);
                        break;
                    case ADDA:
                        sportsPartyFragment.setRefreshing(false);
                        ResponseInfoFromNet ri = JsonUtils.json2RI(m.obj.toString());
                        if (ri.getReturnState() == true)
                            writeDataInSQL(ri);
                        else
                            SVProgressHUD.showInfoWithStatus(context, "无活动");
                        break;
                }
            }
        };
    }

    private void writeDataInSQL(ResponseInfoFromNet ri) {
        list = JsonUtils.json2Activities(ri.getContent());
        doRefreshFromLocal(WRITE);
    }

    public void doRefreshFromLocal(int state) {
        RefreshActivitiesTask task = new RefreshActivitiesTask(state);
        task.execute();
    }

    public void doRefreshFromNet() {
       /* SyncNetDatasTask task=new SyncNetDatasTask();
        task.execute();*/
        Log.d(MainActivity.TAG,"doRefreshFromLocal");
        if (NetworkUtil.isNetworkAvailable(context)) {
            sportsPartyFragment.setRefreshing(true);
            request.doPost(APIManager.BASE_URL + APIManager.QUERY_ACTIVITY_URL, getRequestParams(), handler, ADDA);
        } else
            SVProgressHUD.showInfoWithStatus(context, context.getString(R.string.network_inaccessible));
    }

    private RequestParams getRequestParams() {
        RequestParams p = new RequestParams();
        p.addBodyParameter("useruid", PreferenceUtil.getUuid(context));
        return p;
    }

    class RefreshActivitiesTask extends AsyncTask<Void, Void, Boolean> {
        private int state;

        public RefreshActivitiesTask(int state) {
            this.state = state;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Intent intent = new Intent();
            intent.setAction("com.moriarty.service.AddActivityService");
            intent.setPackage(context.getPackageName());
            switch (state) {
                case WRITE:
                    context.bindService(intent, connection_write, context.BIND_AUTO_CREATE);
                    break;
                case READ:
                    context.bindService(intent, connection_read, context.BIND_AUTO_CREATE);
                    break;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            //refreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(Boolean b) {
            //refreshLayout.setRefreshing(false);
        }
    }

    private ServiceConnection connection_read = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            addActivityService = ((AddActivityService.AddActivityBinder) service).getService();
            if(list!=null)
                list.clear();
            list = addActivityService.getDataFromSQLite();
            //Log.d(MainActivity.TAG,"list size is:"+list.size());
            context.unbindService(connection_read);
            sportsPartyFragment.updateList(list);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private ServiceConnection connection_write = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            addActivityService = ((AddActivityService.AddActivityBinder) service).getService();
            if(addActivityService.clearTable())
                addActivityService.addListIntoSQLite(list);
            context.unbindService(connection_write);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
