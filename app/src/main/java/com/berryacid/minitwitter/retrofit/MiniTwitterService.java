package com.berryacid.minitwitter.retrofit;

import com.berryacid.minitwitter.retrofit.request.RequestLogin;
import com.berryacid.minitwitter.retrofit.request.RequestSignUp;
import com.berryacid.minitwitter.retrofit.response.ResponseAuth;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MiniTwitterService {

    @POST("auth/login")
    Call<ResponseAuth> doLogin(@Body RequestLogin requestLogin);

    @POST("auth/signup")
    Call<ResponseAuth> doSignUp(@Body RequestSignUp requestSignUp);

}
