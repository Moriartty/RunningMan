package com.moriarty.user.runningman.NetworkRequest.Retrofit;

import com.moriarty.user.runningman.NetworkRequest.APIManager;
import com.moriarty.user.runningman.Object.ResponseInfoFromNet;
import com.moriarty.user.runningman.Object.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by user on 17-10-11.
 */
public interface ResponseInfoService {
    @POST(APIManager.BASE_URL+APIManager.SIGNUP_URL)
    Call<ResponseInfoFromNet> doPost(@Query("content") String content);
}
