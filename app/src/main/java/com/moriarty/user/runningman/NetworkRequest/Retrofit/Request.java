package com.moriarty.user.runningman.NetworkRequest.Retrofit;

import android.os.Handler;
import android.os.Message;

import com.moriarty.user.runningman.Object.ResponseInfoFromNet;
import com.moriarty.user.runningman.Object.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 17-10-11.
 */
public class Request {
    public void request(String content, final Handler handler) {
        ResponseInfoService service = GenServiceUtil.createService(ResponseInfoService.class);
        Call<ResponseInfoFromNet> call = service.doPost(content);
        call.enqueue(new Callback<ResponseInfoFromNet>() {
            @Override
            public void onResponse(Call<ResponseInfoFromNet> call, Response<ResponseInfoFromNet> response) {
                ResponseInfoFromNet ei = response.body();
                Message m = new Message();
                m.obj = ei;
                handler.sendMessage(m);
            }

            @Override
            public void onFailure(Call<ResponseInfoFromNet> call, Throwable throwable) {

            }
        });
    }
}
