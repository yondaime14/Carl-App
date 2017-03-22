package com.carllewis14.recyclerview.network;

import com.carllewis14.recyclerview.datamodel.Response;
import com.carllewis14.recyclerview.datamodel.User;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Retrofit class
 * POST and @GET
 * Calls made from api to: Register, Authenticate details, get User profile
 * change password
 */

public interface RetrofitInterface {

    @POST("user")
    Observable<Response> register(@Body User user);

    @POST("authenticate")
    Observable<Response> login();

    @GET("users/{email}")
    Observable<User> getProfile(@Path("email") String email);

    @PUT("users/{email}")
    Observable<Response> changePassword(@Path("email") String email, @Body User user);

    @POST("users/{email}/password")
    Observable<Response> resetPasswordInit(@Path("email") String email);

    @POST("users/{email}/password")
    Observable<Response> resetPasswordFinish(@Path("email") String email, @Body User user);


}
