package com.carllewis14.recyclerview.network;

import android.util.Base64;

import com.carllewis14.recyclerview.utils.Constants;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

/**
 * This class calls the different network functionality
 * once user makes an action to signup/login/change password
 */

public class NetworkUtil {

    public static RetrofitInterface getRetrofit(){
        RxJavaCallAdapterFactory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitInterface.class);
    }

    /**
     *
     * @param email
     * @param password
     * @return
     * Pass Params email + password then they are combined and encoded with Base 64
     */

    public static RetrofitInterface getRetrofit(String email, String password) {

        String credentials = email + ":" + password;
        String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder()
                    .addHeader("Authorization", basic)
                    .method(original.method(), original.body());
            return chain.proceed(builder.build());

        });

        RxJavaCallAdapterFactory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(httpClient.build())
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitInterface.class);

    }

    public RetrofitInterface getRetrofit(String token) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {

            Request original = chain.request();
            Request.Builder builder = original.newBuilder()
                    .addHeader("x-access-token", token)
                    .method(original.method(), original.body());
            return chain.proceed(builder.build());

        });

        RxJavaCallAdapterFactory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitInterface.class);

    }

}
