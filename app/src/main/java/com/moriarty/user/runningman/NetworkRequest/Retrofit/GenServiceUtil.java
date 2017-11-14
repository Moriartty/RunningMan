package com.moriarty.user.runningman.NetworkRequest.Retrofit;

import com.moriarty.user.runningman.Object.User;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by user on 17-10-11.
 */
public class GenServiceUtil {
    private static final String BASE_URL="https://api.github.com/";
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static Retrofit.Builder builder=new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    private static Retrofit retrofit = builder.client(httpClient.build()).build();
    public static <S> S createService(Class<S> serviceClass){
        return retrofit.create(serviceClass);
    }
}
